package com.suheng.structure.net.request.normal;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;

public abstract class JsonTask<T> extends StringTask<T> {
    private static final int ERROR_JSON_NO_FIELDS = -9715;
    private static final int ERROR_JSON_FORMAT_EXCEPTION = -9716;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_MSG = "msg";
    private static final String FIELD_DATA = "data";
    private String mErrorData;

    @Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        //result = JSON;
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has(FIELD_CODE) && jsonObject.has(FIELD_MSG)) {
                mErrorData = jsonObject.optString(FIELD_DATA, "");
                Log.e(getLogTag(), "failure-->error data: " + mErrorData);

                int code = jsonObject.optInt(FIELD_CODE);
                if (code == 0) {//定义三种错误码：1.HTTP协议已定义的错误码；2.本地错误码，如IO异常、JSON解析异常；3.业务错误码，如登录失败、获取用户信息失败
                    setFinishCallback(this.parseResult(result));
                } else {
                    setFailureCallback(code, jsonObject.getString(FIELD_MSG));
                }
            } else {
                setFailureCallback(ERROR_JSON_NO_FIELDS, "don't have " + FIELD_CODE + " and " + FIELD_MSG + " field");
            }
        } catch (JSONException e) {
            setFailureCallback(ERROR_JSON_FORMAT_EXCEPTION, e.toString());
        }
    }

    public String getErrorData() {
        return mErrorData;
    }
}