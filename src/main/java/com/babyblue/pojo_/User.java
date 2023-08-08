package com.babyblue.pojo_;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class User {

    private String name;

    private String age;

    private Cat cat;

    private List<String> hobbies;

    private String[] skills;

    private Map<String, Cat> map;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String[] getSkills() {
        return skills;
    }

    public void setSkills(String[] skills) {
        this.skills = skills;
    }

    public Map<String, Cat> getMap() {
        return map;
    }

    public void setMap(Map<String, Cat> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", cat=" + cat +
                ", hobbies=" + hobbies +
                ", skills=" + Arrays.toString(skills) +
                ", map=" + map +
                '}';
    }

    public void sayHello() {
        System.out.println("hello world");
    }
}
