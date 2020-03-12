package com.suheng.structure.data.net.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.data.net.bean.UserInfo;
import com.suheng.structure.net.request.normal.CodeMsgTask;
import com.suheng.structure.net.response.Result;

public class LoginTask2 extends CodeMsgTask<UserInfo, UserInfo> {
    private Gson mGson = new Gson();

    public LoginTask2(String name, String pwd) {
        addArgument("user_name", name);
        addArgument("login_pwd", pwd);
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_USER_INFO;
    }

    @Override
    protected UserInfo getRightResult(String result) {
        Result<UserInfo> response = mGson.fromJson(result, new TypeToken<Result<UserInfo>>() {
        }.getType());
        return response.getData();
    }

    @Override
    protected UserInfo getErrorResult(String result) {
        Result<UserInfo> response = mGson.fromJson(result, new TypeToken<Result<UserInfo>>() {
        }.getType());
        return response.getData();
    }
}
