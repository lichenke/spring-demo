package com.babyblue;

import com.babyblue.pojo.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringWithAnnotationTest {

    @Test
    public void func_get_bean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext("com.babyblue.config");
        User user = ac.getBean(User.class);
        System.out.println(user);
    }
}
