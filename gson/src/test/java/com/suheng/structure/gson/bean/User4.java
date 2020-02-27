package com.suheng.structure.gson.bean;

import androidx.annotation.Nullable;

public class User4 {
    private String id;
    private String name;
    private int age;

    public User4(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * name.hashCode() * age;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User4) {
            User4 user4 = (User4) obj;
            return this.id.equals(user4.id) && this.name.equals(user4.name) && this.age == user4.age;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User4{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
