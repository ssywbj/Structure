package com.suheng.structure.gson.bean;

import java.util.List;

public class ResultList {
    private int code;
    private String msg;
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResultList{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
