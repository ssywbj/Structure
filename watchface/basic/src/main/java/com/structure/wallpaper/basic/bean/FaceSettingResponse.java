package com.structure.wallpaper.basic.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FaceSettingResponse {

    @SerializedName("sty")
    private int mStyle;

    public FaceSettingResponse() {
    }

    public int getStyle() {
        return mStyle;
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
