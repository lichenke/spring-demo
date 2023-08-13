package com.babyblue;

import com.babyblue.controller.UserController;
import com.babyblue.pojo.ValueTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AppTest {

    @Test
    public void testCase() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(JavaConfig.class);
        UserController user = ac.getBean(UserController.class);
        System.out.println(user.getUsers());

    }


    @Test
    public void testValue() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(JavaConfig.class);
        ValueTest value = ac.getBean(ValueTest.class);
        System.out.println(value);
        System.out.println(value.getJdbcUrl());
        Resource resourceFile = value.getResourceFile();
        try (BufferedInputStream is = (BufferedInputStream) resourceFile.getInputStream()) {
            System.out.println(new String(is.readAllBytes(), UTF_8));
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}
