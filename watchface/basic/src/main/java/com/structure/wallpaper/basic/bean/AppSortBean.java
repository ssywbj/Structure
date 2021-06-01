package com.structure.wallpaper.basic.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppSortBean {

    @SerializedName("appSorts")
    public List<Info> mAppSorts;
    @SerializedName("thirdPartApps")
    public List<Info> mThirdApps;

    public static class Info {
        @SerializedName("componentName")
        public String mComponentName;

        @Override
        public String toString() {
            return "Info{" +
                    "mComponentName='" + mComponentName + '\'' +
                    '}';
        }
    }


}
