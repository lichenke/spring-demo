package com.babyblue.framework.bean.support;

import com.babyblue.framework.bean.MyBeanDefinition;
import com.babyblue.framework.exception.MyIoCException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyBeanDefinitionReader {

    private final ClassLoader cl = this.getClass().getClassLoader();

    private final Properties contextConfig = new Properties();

    private final List<String> registryBeanClasses = new ArrayList<>();

    public MyBeanDefinitionReader(String location) {
        // 加载启动配置文件
        doLoadConfig(location);
        // 扫描目标路径下的类
        doScan(contextConfig.getProperty("scan-package"));
    }

    public List<MyBeanDefinition> loadBeanDefinition() {
        List<MyBeanDefinition> res = new ArrayList<>();
        try {
            for (String beanClazz : registryBeanClasses) {
                Class<?> clazz = Class.forName(beanClazz);
                // 如果扫描到的类是接口，那就直接跳过
                if (clazz.isInterface()) {
                    continue;
                }
                // 普通类用首字母小写的simpleName作为factoryBeanName, 全类名作为factoryClassName
                String simpleName = firstLetterToLowerCase(clazz.getSimpleName());
                res.add(doCreateBeanDefinition(simpleName, clazz.getName()));
                // 接口用接口的全类名作为factoryBeanName, 其实现类的全类名作为factoryClassName
                for (Class<?> interface_ : clazz.getInterfaces()) {
                    res.add(doCreateBeanDefinition(interface_.getName(), clazz.getName()));
                }
            }
            return res;
        } catch (Exception e) {
            throw new MyIoCException("加载bean definition失败", e);
        }
    }

    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String factoryClassName) {
        MyBeanDefinition bd = new MyBeanDefinition();
        bd.setFactoryBeanName(factoryBeanName);
        bd.setBeanClassName(factoryClassName);
        return bd;
    }



    private void doScan(String rootDir) {
        String[] rootDirs = rootDir.split(", ");
        for (String s : rootDirs) {
            URL url = cl.getResource(s.replaceAll("\\.", "/"));
            if (url == null) {
                continue;
            }
            File classpath = new File(url.getFile());
            loadClass(s, classpath);
        }
    }

    private void loadClass(String rootDir, File file) {
        File[] fs = file.listFiles();
        if (fs == null) {
            return;
        }
        for (File f : fs) {
            if (f.isDirectory()) {
                String name = f.getName();
                loadClass(rootDir + "." + name, f);
            } else if (f.isFile()) {
                if (!f.getName().endsWith(".class")) {
                    continue;
                }
                String name = f.getName().replaceAll("\\.class", "");
                String className = rootDir + "." + name;
                registryBeanClasses.add(className);
            }
        }
    }

    private void doLoadConfig(String location) {
        try (InputStream is = cl.getResourceAsStream(location)) {
            contextConfig.load(is);
        } catch (Exception e) {
            throw new MyIoCException("启动配置加载失败", e);
        }
    }

    private String firstLetterToLowerCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }
}
