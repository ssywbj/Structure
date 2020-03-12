package com.suheng.structure.data.net.request;

import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.net.request.normal.RequestTask;

public class LoginTask3 extends RequestTask {

    public LoginTask3(String name, String pwd) {
        addArgument("user_name", name);
        addArgument("login_pwd", pwd);
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_USER_INFO;
    }
}
