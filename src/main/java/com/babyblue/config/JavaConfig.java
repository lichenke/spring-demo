package com.babyblue.config;

import com.babyblue.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 通过Java类的方式初始化IoC容器
@Configuration
public class JavaConfig {

    @Bean
    public User getUser() {
        User user = new User();
        user.setAge("28");
        user.setName("老李");
        return user;
    }

}
