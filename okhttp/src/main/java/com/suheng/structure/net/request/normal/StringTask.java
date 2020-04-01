package com.suheng.structure.net.request.normal;

import com.suheng.structure.net.request.basic.OkHttpTask;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;

public abstract class StringTask<T> extends OkHttpTask<T> {
    private static final String JSON = "{" + "\"code\":0" + ",\"msg\":密码错误" + ",data:{"
            + "\"member_id\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";

    protected abstract T parseResult(String result);

    @Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        //result = JSON;
        setFinishCallback(this.parseResult(result));
    }
}