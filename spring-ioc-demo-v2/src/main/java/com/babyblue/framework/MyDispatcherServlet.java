package com.babyblue.framework;

import com.babyblue.framework.annotation.GPController;
import com.babyblue.framework.annotation.GPRequestMapping;
import com.babyblue.framework.annotation.GPRequestParam;
import com.babyblue.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@WebServlet(value = "/", initParams = @WebInitParam(name = "contextConfig", value = "application.properties"))
public class MyDispatcherServlet extends HttpServlet {

    private final Map<String, Method> handlerMapping = new HashMap<>();


    private MyApplicationContext ac;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Detail: " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) {
        ac = new MyApplicationContext(config.getInitParameter("contextConfig"));
        doInitHandlerMapping();
        System.out.println("my Spring framework started");
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found");
            return;
        }

        Method method = this.handlerMapping.get(url);

        //1、先把形参的位置和参数名字建立映射关系，并且缓存下来
        Map<String,Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] pa = method.getParameterAnnotations();
        for (int i = 0; i < pa.length; i ++) {
            for (Annotation a : pa[i]) {
                if(a instanceof GPRequestParam){
                    String paramName = ((GPRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }

        Class<?> [] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }

        //2、根据参数位置匹配参数名字，从url中取到参数名字对应的值
        Object[] paramValues = new Object[paramTypes.length];

        //http://localhost/demo/query?name=Tom&name=Tomcat&name=Mic
        Map<String,String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");

            if(!paramIndexMapping.containsKey(param.getKey())){continue;}

            int index = paramIndexMapping.get(param.getKey());

            //涉及到类型强制转换
            paramValues[index] = value;
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        String beanName = firstLetterToLowerCase(method.getDeclaringClass().getSimpleName());
        //3、组成动态实际参数列表，传给反射调用
        method.invoke(ac.getBean(beanName), paramValues);
    }


    private void doInitHandlerMapping() {
        if (ac.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : this.ac.getBeanDefinitionNames()) {
            Object instance = ac.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if (!clazz.isAnnotationPresent(GPController.class)) {
                continue;
            }


            String baseUrl = "";
            if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //只迭代public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                    continue;
                }
                GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                //  //demo//query
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("Mapped : " + url + " --> " + method);
            }
        }
    }

    private String firstLetterToLowerCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }
}