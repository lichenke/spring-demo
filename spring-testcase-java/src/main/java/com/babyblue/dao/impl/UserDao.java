package com.babyblue.dao.impl;

import com.babyblue.dao.IUserDao;
import com.babyblue.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao implements IUserDao {
    @Override
    public List<User> getUsers() {
        return Arrays.asList(new User("lichenke", 28), new User("wenyuhan", 25), new User("wutieqin", 54));
    }
}
