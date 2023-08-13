package com.babyblue.service.impl;

import com.babyblue.dao.IUserDao;
import com.babyblue.pojo.User;
import com.babyblue.service.IUserService;

import java.util.List;

public class UserService implements IUserService {

    private IUserDao userDao;

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getUsers() {
        return userDao.getUser();
    }
}
