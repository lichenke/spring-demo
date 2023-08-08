package com.babyblue.factory;

import com.babyblue.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class StaticFactory {

    static Map<String, User> map;

    static {
        map = new HashMap<>();
        map.put("a1", new User());
        map.put("a2", new User());
    }

    // 静态工厂提供的方法
    public static User getInstance(String id) {
        return map.get(id);
    }
}
