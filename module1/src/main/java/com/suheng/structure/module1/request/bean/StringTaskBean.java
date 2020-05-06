package com.suheng.structure.module1.request.bean;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class StringTaskBean {
    @SerializedName("member_id")
    private long memberId;
    @SerializedName("age")
    private int age;
    @SerializedName("email_address")
    private String emailAddress;

    public StringTaskBean(int memberId, int age, String emailAddress) {
        this.memberId = memberId;
        this.emailAddress = emailAddress;
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "memberId=" + memberId +
                ", age=" + age +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
