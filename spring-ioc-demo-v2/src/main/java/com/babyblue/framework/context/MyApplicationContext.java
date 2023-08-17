package com.babyblue.framework.context;

import com.babyblue.framework.annotation.GPAutowired;
import com.babyblue.framework.annotation.GPController;
import com.babyblue.framework.annotation.GPService;
import com.babyblue.framework.bean.MyBeanDefinition;
import com.babyblue.framework.bean.support.MyBeanDefinitionReader;
import com.babyblue.framework.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyApplicationContext implements MyBeanFactory {

    private final Map<String, MyBeanDefinition> bdMap = new LinkedHashMap<>();

    private final Map<String, Object> factoryBeanInstanceCache = new HashMap<>();


    public MyApplicationContext(String configLocation) {
        MyBeanDefinitionReader reader = new MyBeanDefinitionReader(configLocation);
        // 加载bean definition，并注册
        registerBeanDefinition(reader.loadBeanDefinition());
        // 加载非延时加载的所有的bean
        loadInstance();
    }

    private void loadInstance() {
        for (Map.Entry<String, MyBeanDefinition> e : bdMap.entrySet()) {
            String name = e.getKey();
            MyBeanDefinition bd = e.getValue();
            if (!bd.isLazyInit()) {
                getBean(name);
            }
        }
    }

    @Override
    public Object getBean(Class<?> clazz) {
        return getBean(clazz.getName());
    }

    @Override
    public Object getBean(String beanName) {
        Object o = factoryBeanInstanceCache.get(beanName);
        if (o != null) {
            return o;
        }
        MyBeanDefinition myBeanDefinition = bdMap.get(beanName);
        // 反射创建实例化对象
        Object obj = instantiateBean(myBeanDefinition);
        if (obj != null) {
            populateBean(obj);
            factoryBeanInstanceCache.put(beanName, obj);
        }
        return obj;
    }

    private void populateBean(Object obj) {
        Class<?> clazz = obj.getClass();
        if (!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            //强制访问，强吻
            field.setAccessible(true);

            try {
                //  A  --->  实例化 ---> 放入到容器 --->  属性注入 --> 把A放入到factoryBeanInstanceCache容器里面去了
                //  B  --->  实例化 ---> 放入到容器 --->  属性注入A(完成) --> 然后把B放入到factoryBeanInstanceCache里面去了
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                // b.setA()
                //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
                field.set(obj, this.factoryBeanInstanceCache.get(autowiredBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerBeanDefinition(List<MyBeanDefinition> bds) {
        // myUserService
        for (MyBeanDefinition bd : bds) {
            this.bdMap.put(bd.getFactoryBeanName(), bd);
        }
    }

    private Object instantiateBean(MyBeanDefinition bd) {
        String clazzName = bd.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(clazzName);
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.out.println("无法初始化此类：" + clazzName);
        }
        return instance;
    }

    public int getBeanDefinitionCount(){
        return this.bdMap.size();
    }

    public String[] getBeanDefinitionNames(){
        return this.bdMap.keySet().toArray(new String[0]);
    }
}
