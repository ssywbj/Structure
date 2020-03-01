package com.suheng.structure.gson.bean;

import androidx.annotation.NonNull;
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
        if (this == obj) {//如果两个对象的地址相同，那么他们一定是同一个对象，里面的内容肯定是相等的
            return true;
        }
        if (obj instanceof User4) {//如果不相同，那么需要根据他们内容比较的结果来判定是否为同一个对象
            User4 user4 = (User4) obj;
            return this.id.equals(user4.id) && this.name.equals(user4.name) && this.age == user4.age;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "User4{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }
}
