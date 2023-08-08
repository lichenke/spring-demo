package com.babyblue;

import com.babyblue.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class SpringWithXMLTest {

    @Test
    public void func_normal_create() {
        // 一般创建对象的方式
        User user = new User();
        user.sayHello();
    }

    @Test
    public void func_ioc_create() {
        // 使用IoC容器创建Bean对象，完成了控制的反转
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application.xml");
        User user = (User) ac.getBean("user"); // 通过id获取对象，id只能声明一个
        System.out.println(user);
        User user1 = (User) ac.getBean("user"); // 再获取一个
        System.out.println(user == user1); // true 两个对象是同一个，所以是单例模式的，可以通过配置scope = "prototype" 变成原型模式

        // 根据name获取对象（可以声明多个，用逗号，分号，空格分割）
        User user2 = (User) ac.getBean("lichenke");
        User user3 = (User) ac.getBean("test");
        System.out.println(user == user2); // true
        System.out.println(user == user3); // true

        // 根据class字节码对象获取对象
        User user4 = ac.getBean(User.class);
        System.out.println(user == user4);

        // 如果定义了两个类型相同的bean，如何分别获取bean
        User user5 = ac.getBean("user1", User.class);
        System.out.println(user == user5);

        // 通过对bean添加primary的配置，可以优先获取这个bean，而不用指定名称或id
        User user6 = ac.getBean(User.class);
        System.out.println(user == user6);
    }


    // ApplicationContext和BeanFactory的区别
    // 前者是后者的子接口，拥有后者不具备的很多功能
    @Test
    public void func_test_application_context() {
        // IoC容器初始化时就会实例化对象
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application.xml");
        // Company被初始化了
    }

    @Test
    public void func_test_bean_factory() {
        // IoC容器初始化时不会实例化对象
        // 此方法已过时
        BeanFactory bf = new XmlBeanFactory(new ClassPathResource("application.xml"));
    }

    @Test
    public void func_static_factory() {
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application-static-factory.xml");
        User user = ac.getBean("user", User.class);
        user.sayHello();
    }

    @Test
    public void func_dynamic_factory() {
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application-dynamic-factory.xml");
        User user = ac.getBean("user", User.class);
        user.sayHello();
    }

    @Test
    public void func_init_instance() {
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application.xml");
        // 属性注入
        User user1 = ac.getBean("user1", User.class);
        // 构造器注入注入
        User user2 = ac.getBean("user2", User.class);
        System.out.println(user1);
        System.out.println(user2);
    }

    @Test
    public void func_other_type_injection() {
        ApplicationContext ac =  new ClassPathXmlApplicationContext("application01.xml");
        // 其它常用类型的注入方式
        com.babyblue.pojo_.User  user = ac.getBean("user", com.babyblue.pojo_.User .class);
        System.out.println(user);

    }

}
