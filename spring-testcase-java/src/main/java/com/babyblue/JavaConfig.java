package com.babyblue;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

@Configuration
// 指定扫描路径
// 如果使用默认的过滤规则
// 会默认扫描@Component @Repository @Service @Controller四种注解
// 将useDefaultFilters = false，则不使用以上的默认过滤规则，用includeFilters可以指定要扫描的注解
@ComponentScan(value = "com.babyblue.controller", useDefaultFilters = false, includeFilters = {@ComponentScan.Filter(Controller.class)})
// 将useDefaultFilters = true，则使用以上的默认过滤规则，用excludeFilters可以指定不要扫描的注解
@ComponentScan(value = {"com.babyblue.dao.impl", "com.babyblue.service.impl"}, excludeFilters = {@ComponentScan.Filter(Controller.class)})
@ComponentScan(value = "com.babyblue.pojo")
@PropertySource("classpath:db.properties")
public class JavaConfig {
}
