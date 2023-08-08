package com.babyblue.pojo;

public class User {

    private String name;
    private String age;

    public String getName() {
        return name;
    }

    // 使用属性注入，必须要提供对应的set方法
    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public User() {
    }

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }

    public void sayHello() {
        System.out.println("hello world");
    }
}
