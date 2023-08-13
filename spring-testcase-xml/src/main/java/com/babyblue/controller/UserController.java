package com.babyblue.controller;

import com.babyblue.pojo.User;
import com.babyblue.service.IUserService;

import java.util.List;

public class UserController {

    private IUserService userService;

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public List<User> getUsers() {
        return userService.getUsers();
    }
}
