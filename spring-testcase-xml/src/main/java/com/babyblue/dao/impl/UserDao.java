package com.babyblue.dao.impl;

import com.babyblue.dao.IUserDao;
import com.babyblue.pojo.User;

import java.util.Arrays;
import java.util.List;

public class UserDao implements IUserDao {
    @Override
    public List<User> getUser() {
        return Arrays.asList(new User("lichenke", 28), new User("wenyuhan", 25));
    }
}
