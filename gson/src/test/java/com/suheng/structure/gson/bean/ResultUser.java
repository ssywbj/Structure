package com.suheng.structure.gson.bean;

public class ResultUser {
    private int code;
    private String msg;
    private User data;

    public User getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResultUser{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
