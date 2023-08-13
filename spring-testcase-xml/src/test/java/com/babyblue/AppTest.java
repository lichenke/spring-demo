package com.babyblue;

import com.babyblue.controller.UserController;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppTest {

    @Test
    public void test() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserController user = ac.getBean(UserController.class);
        System.out.println(user.getUsers());
    }
}
