package com.suheng.structure.module1.request;

import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.net.request.normal.StringTask;

public class StringTaskImpl extends StringTask<String> {

    public StringTaskImpl(String name, String pwd) {
        addArgument("user_name", name);
        addArgument("login_pwd", pwd);
    }

    @Override
    protected String parseResult(String result) {
        return result;
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_LOGIN_REQUEST;
    }
}
