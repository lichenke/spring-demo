package com.babyblue.bean.controller;

import com.babyblue.annotation.MyAutowiredAnno;
import com.babyblue.annotation.MyControllerAnno;
import com.babyblue.annotation.MyRequestMapping;
import com.babyblue.bean.service.MyService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyControllerAnno
@MyRequestMapping("/demo")
public class MyController {


    @MyAutowiredAnno
    private MyService service;


    @MyRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp, String name) {
        try {
            resp.getWriter().write("my name is " + name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
