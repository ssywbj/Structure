package com.structure.wallpaper.basic.bean;

import com.google.gson.annotations.SerializedName;

public class FacePluginParams {

    @SerializedName("corner_type")
    private int mCornerType; //0半圆，1圆角，2直角

    @SerializedName("bg_color")
    private String mBgColor; //背景颜色

    public FacePluginParams() {
    }

    public FacePluginParams(int cornerType, String bgColor) {
        mCornerType = cornerType;
        mBgColor = bgColor;
    }

}
