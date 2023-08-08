package com.babyblue.factory;

import com.babyblue.pojo.User;

public class DynamicFactory {

    public User getInstance() {
        return new User();
    }
}
