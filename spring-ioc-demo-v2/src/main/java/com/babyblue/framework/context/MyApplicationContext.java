package com.babyblue.framework.context;

import com.babyblue.framework.annotation.GPAutowired;
import com.babyblue.framework.annotation.GPController;
import com.babyblue.framework.annotation.GPService;
import com.babyblue.framework.bean.MyBeanDefinition;
import com.babyblue.framework.bean.support.MyBeanDefinitionReader;
import com.babyblue.framework.core.MyBeanFactory;
import com.babyblue.framework.exception.MyIoCException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext implements MyBeanFactory {

    private final Map<String, MyBeanDefinition> bdMap = new LinkedHashMap<>();

    //循环依赖的标识，当前正在创建的BeanName，Mark一下
    private final Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    //一级缓存：保存成熟的Bean（IoC容器）
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    //二级缓存：保存早期的Bean
    private final Map<String, Object> earlySingletonObjects = new HashMap<>();


    // 三级缓存：保存仅完成实例化的Bean
    private final Map<String, Object> factoryBeanObjectCache = new HashMap<>();


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
        Object o;
        if (!singletonObjects.containsKey(beanName)) {
            synchronized (this.singletonObjects) {
                if (!singletonObjects.containsKey(beanName)) {
                    MyBeanDefinition myBeanDefinition = bdMap.get(beanName);
                    Object singleton = getSingleton(beanName, myBeanDefinition);
                    if (singleton != null) {
                        return singleton;
                    }
                    //标记bean正在创建
                    singletonsCurrentlyInCreation.add(beanName);
                    //反射实例化对象，把A先放入到三级缓存中
                    o = instantiateBean(beanName, myBeanDefinition);
                    if (o != null) {
                        populateBean(o);
                        this.singletonObjects.put(beanName, o);
                    }
                    // 把创建标记清空
                    singletonsCurrentlyInCreation.remove(beanName);
                } else {
                    o = singletonObjects.get(beanName);
                }
                return o;
            }
        } else {
            return singletonObjects.get(beanName);
        }
    }

    private Object getSingleton(String beanName, MyBeanDefinition myBeanDefinition) {
        // 去一级缓存拿
        Object bean = singletonObjects.get(beanName);
        if (bean == null && singletonsCurrentlyInCreation.contains(beanName)) {
            // 从二级缓存拿
            bean = earlySingletonObjects.get(beanName);
            if (bean == null) {
                bean = instantiateBean(beanName, myBeanDefinition);
                // 将创建出的对象放入到二级缓存中
                earlySingletonObjects.put(beanName, bean);
            }
        }
        return bean;
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
                // if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                //    continue;
                // }
                // b.setA()
                field.set(obj, getBean(autowiredBeanName));
            } catch (IllegalAccessException e) {
                throw new MyIoCException(e);
            }
        }
    }

    private void registerBeanDefinition(List<MyBeanDefinition> bds) {
        // myUserService
        for (MyBeanDefinition bd : bds) {
            this.bdMap.put(bd.getFactoryBeanName(), bd);
        }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition bd) {
        // 未来可能存在循环依赖，可能需要在三级缓存中直接拿
        if (bd.isSingleton() && this.factoryBeanObjectCache.containsKey(beanName)) {
            return this.factoryBeanObjectCache.get(beanName);
        }

        String clazzName = bd.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(clazzName);
            instance = clazz.getDeclaredConstructor().newInstance();
            // 如果是代理对象，触发AOP的逻辑
            this.factoryBeanObjectCache.put(beanName, instance);
            this.factoryBeanObjectCache.put(clazz.getName(), instance);
            for (Class<?> i : clazz.getInterfaces()) {
                this.factoryBeanObjectCache.put(i.getName(), instance);
            }
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
