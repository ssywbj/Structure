package com.suheng.structure.module3.net.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suheng.structure.module3.net.bean.UserInfo;
import com.suheng.structure.net.request.normal.OkHttpTask;
import com.suheng.structure.net.response.Result;

public class LoginTask extends OkHttpTask<UserInfo> {

    public LoginTask(String name, String pwd) {
        addArgument("user_name", name);
        addArgument("login_pwd", pwd);
    }

    @Override
    protected String getURL() {
        return "https://www.baidu.com/index.jsp";
    }

    @Override
    protected UserInfo getRightResult(String result) {
        Gson gson = new Gson();
        Result<UserInfo> response = gson.fromJson(result, new TypeToken<Result<UserInfo>>() {
        }.getType());
        return response.getData();
    }
}
