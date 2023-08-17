package com.babyblue.framework.core;


// 创建对象工厂的最顶层接口
public interface MyBeanFactory {

    // 根据class获取bean对象
    Object getBean(Class<?> clazz);

    // 根据配置的bean名称获取bean对象
    Object getBean(String beanName);

}
