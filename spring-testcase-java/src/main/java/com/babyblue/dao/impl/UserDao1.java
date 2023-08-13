package com.babyblue.dao.impl;

import com.babyblue.dao.IUserDao;
import com.babyblue.pojo.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@Primary
public class UserDao1 implements IUserDao {
    @Override
    public List<User> getUsers() {
        return Arrays.asList(new User("lichenke", 28), new User("xiaohua", 25), new User("wutieqin", 54));
    }
}
