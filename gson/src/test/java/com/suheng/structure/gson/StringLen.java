package com.suheng.structure.gson;

public class StringLen {
    private final String str = "aaaa...aaa";
    private int len;
    int subLen;

    public StringLen(int len) {
        this.len = len;
    }

    public int getLen() {
        return len;
    }

    public static void main(String[] args) {
        String tmpStr = "bbbb...bbb";
        System.out.println("tmp str: " + tmpStr);
    }
}