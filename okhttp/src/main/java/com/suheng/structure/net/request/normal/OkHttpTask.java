package com.suheng.structure.net.request.normal;

import org.json.JSONObject;

public abstract class OkHttpTask<RightResult> extends JsonTask<RightResult, String> {
    private static final String FIELD_DATA = "data";

    @Override
    protected String getErrorResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.optString(FIELD_DATA, "don't have " + FIELD_DATA + " field");
        } catch (Exception e) {
            e.printStackTrace();
            return "parse " + FIELD_DATA + " exception, " + e.toString();
        }
    }
}