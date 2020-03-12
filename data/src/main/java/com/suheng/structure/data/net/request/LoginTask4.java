package com.suheng.structure.data.net.request;

import android.util.Log;

import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.net.request.basic.BasicTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class LoginTask4 extends BasicTask {
    private JSONObject mJSONObject = new JSONObject();

    public LoginTask4(String name, String pwd) {
        try {
            mJSONObject.put("user_name", name);
            mJSONObject.put("login_pwd", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_USER_INFO;
    }

    @Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        Log.e(getLogTag(), "parseResponseBody: " + responseBody.string() + ", thread: " + Thread.currentThread().getName());
    }

    @Override
    protected RequestBody getRequestBody() {
        String jsonParams = mJSONObject.toString();
        Log.d(getLogTag(), "jsonParams: " + jsonParams);
        return RequestBody.create("s", MediaType.parse("application/json; charset=utf-8"));
    }

    @Override
    protected Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Access-User-Token", "e5cHLWScbto3VfvYTU1llVZgl/WniA4QZZ8epmn8k/o=");
        return builder.build();
    }
}
