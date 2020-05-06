package com.suheng.structure.module1.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.module1.request.bean.StringTaskBean;
import com.suheng.structure.net.request.normal.JsonTask;
import com.suheng.structure.net.response.Result;

public class JsonTaskImpl2 extends JsonTask<StringTaskBean> {

    public JsonTaskImpl2(String name, String pwd) {
        addArgument("user_name", name);
        addArgument("login_pwd", pwd);
    }

    @Override
    protected StringTaskBean parseResult(String result) {
        Gson gson = new Gson();
        Result<StringTaskBean> response = gson.fromJson(result, new TypeToken<Result<StringTaskBean>>() {
        }.getType());
        return response.getData();
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_LOGIN_REQUEST;
    }
}
