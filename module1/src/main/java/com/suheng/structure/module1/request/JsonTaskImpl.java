package com.suheng.structure.module1.request;

import com.suheng.structure.common.data.net.URLConstants;
import com.suheng.structure.module1.request.bean.StringTaskBean;

public class JsonTaskImpl/* extends JsonTask<StringTaskBean>*/ {

    public JsonTaskImpl(String name, String pwd) {
        //addArgument("user_name", name);
        //addArgument("login_pwd", pwd);
    }

    //@Override
    protected StringTaskBean parseResult(String result) {
        /*Gson gson = new Gson();
        Result<StringTaskBean> response = gson.fromJson(result, new TypeToken<Result<StringTaskBean>>() {
        }.getType());
        return response.getData();*/
        return null;
    }

    //@Override
    protected String getURL() {
        return URLConstants.URL_LOGIN_REQUEST;
    }
}
