package com.suheng.structure.net.response;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
