package com.babyblue.mvcframework.v1;

import com.babyblue.annotation.MyAutowiredAnno;
import com.babyblue.annotation.MyControllerAnno;
import com.babyblue.annotation.MyRequestMapping;
import com.babyblue.annotation.MyServiceAnno;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@WebServlet(value = "/", initParams = @WebInitParam(name = "contextConfig", value = "application.properties"))
public class MyDispatchServlet extends HttpServlet {

    // 配置文件
    private final Properties config = new Properties();

    // 包路径下扫描到的全类名
    private final List<String> classNames = new ArrayList<>();

    // 类名和实例的对应关系
    private final Map<String, Object> ioc = new HashMap<>();

    // 保存controller中url和方法的对应关系
    private final Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 根据url委派具体的调用方法
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Internal Error");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found");
            return;
        }
        Method method = handlerMapping.get(url);
        String key = firstCaseToLower(method.getDeclaringClass().getSimpleName());
        method.invoke(ioc.get(key), req, resp, req.getParameter("name")); // 硬编码测试
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1. 加载配置文件
        loadConfig(config.getInitParameter("contextConfig"));

        //2. 扫描相关的类
        scan(this.config);

        //3. 初始化IoC容器，将扫描到的类进行实例化，缓存到IoC容器中
        doInstance();

        //4. 完成依赖注入
        doAutoWired();

        //5. 完成mvc功能，初始化handlerMapping
        doInitHandlerMapping();

        System.out.println("My Spring Framework started...");
    }

    private void doInitHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(MyControllerAnno.class)) {
                continue;
            }
            String baseUrl = "";
            if (aClass.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping request = aClass.getAnnotation(MyRequestMapping.class);
                baseUrl = request.value();
            }
            for (Method method : aClass.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping request = method.getAnnotation(MyRequestMapping.class);
                String url = baseUrl + request.value();
                handlerMapping.put(url, method);
            }
        }
    }

    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Object obj = entry.getValue();
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(MyAutowiredAnno.class)) {
                    continue;
                }
                String name = firstCaseToLower(field.getType().getSimpleName());
                try {
                    // 强制访问
                    field.setAccessible(true);
                    field.set(obj, ioc.get(name));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            // 暂时只支持@Controller和@Service两种注解
            for (String className : classNames) {
                Class<?> aClass = Class.forName(className);
                if (aClass.isAnnotationPresent(MyControllerAnno.class)) {
                    String key = firstCaseToLower(aClass.getSimpleName());
                    Object instance = aClass.getDeclaredConstructor().newInstance();
                    ioc.put(key, instance);
                } else if (aClass.isAnnotationPresent(MyServiceAnno.class)) {
                    // 1. 默认使用类名首字母小写
                    String key = firstCaseToLower(aClass.getSimpleName());
                    // 2. 如果存在多个包下出现相同的类名，优先使用别名（自定义命名）
                    MyServiceAnno service = aClass.getAnnotation(MyServiceAnno.class);
                    if (!"".equals(service.value())) {
                        key = service.value();
                    }
                    Object instance = aClass.getDeclaredConstructor().newInstance();
                    ioc.put(key, instance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String firstCaseToLower(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void scan(Properties config) {
        String dir = config.getProperty("scan-package");
        URL url = this.getClass().getClassLoader().getResource("/" + dir.replaceAll("\\.", "/"));
        if (url == null) {
            throw new RuntimeException("此路径下没有可以加载的class文件");
        }
        File classpath = new File(url.getFile());
        loadClass(dir, classpath);

    }

    // 根据contextConfig的路径去classpath下找到对应的配置文件
    private void loadConfig(String contextConfig) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfig)) {
            config.load(is);
        } catch (Exception e) {
            throw new RuntimeException("配置文件加载失败", e);
        }
    }

    private void loadClass(String pkg, File file) {
        File[] fs = file.listFiles();
        if (fs == null || fs.length == 0) {
            throw new RuntimeException("此路径下没有可以加载的class文件");
        }
        for (File f : fs) {
            if (f.isDirectory()) {
                String name = f.getName();
                loadClass(pkg + "." + name, f);
            }
            if (f.isFile()) {
                if (!f.getName().endsWith(".class")) {
                    continue;
                }
                String name = f.getName().replaceAll("\\.class", "");
                classNames.add(pkg + "." +name);
            }
        }
    }
}
