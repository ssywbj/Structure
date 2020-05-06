package com.suheng.structure.module1.request.bean;

import com.google.gson.annotations.SerializedName;

public class JsonTaskErrorBean {
    @SerializedName("last_time")
    private long mLastLoginTime;
    @SerializedName("error_count")
    private int mErrorCount;
    @SerializedName("error_suggestion")
    private String mErrorSuggestion;

    @Override
    public String toString() {
        return "JsonTaskErrorBean{" +
                "mLastLoginTime=" + mLastLoginTime +
                ", mErrorCount=" + mErrorCount +
                ", mErrorSuggestion='" + mErrorSuggestion + '\'' +
                '}';
    }
}
