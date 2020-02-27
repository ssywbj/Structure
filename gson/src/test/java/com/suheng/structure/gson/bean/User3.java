package com.suheng.structure.gson.bean;

public class User3 {
    private String id;
    private int age;
    private String name;

    public User3(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * name.hashCode() * age;
    }
}
