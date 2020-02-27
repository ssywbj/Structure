package com.suheng.structure.gson.bean;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class User {
    private long memberId;
    private int age;
    /*序列化时，"emailAddress"属性被映射成json串的"email_address"字段；反序列化时，json串中
    的"email_address"、"_email_address"、"EmailAddress"均可被映射成"emailAddress"属性*/
    @SerializedName(value = "email_address", alternate = {"_email_address", "EmailAddress"})
    private String emailAddress;

    public User(int memberId, int age, String emailAddress) {
        this.memberId = memberId;
        this.emailAddress = emailAddress;
        this.age = age;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            return (this.memberId == user.memberId) && (this.age == user.age) && this.emailAddress.equals(user.emailAddress);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((int) this.memberId) * this.age * this.emailAddress.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "memberId=" + memberId +
                ", age=" + age +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
