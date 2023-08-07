package com.babyblue.pojo;

public class Company {

    private String name;

    public Company(String name) {
        this.name = name;
    }

    public Company() {
        System.out.println("Company被初始化了");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
