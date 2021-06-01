package com.structure.wallpaper.basic.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class FaceParams {

    @SerializedName("sty")
    private final ArrayList<Integer> mArrayList;

    public FaceParams() {
        mArrayList = new ArrayList<>();
    }

    public void addStyle(Integer... style) {
        mArrayList.addAll(Arrays.asList(style));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
