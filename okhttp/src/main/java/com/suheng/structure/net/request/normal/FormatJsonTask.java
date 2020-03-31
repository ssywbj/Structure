package com.suheng.structure.net.request.normal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;

public abstract class FormatJsonTask<T> extends StringTask<T> {
    private static final String JSON = "{" + "\"code\":1" + ",\"msg\":密码错误" + ",data:{"
            + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";

    private static final int ERROR_JSON_NO_FIELDS = -9715;
    private static final int ERROR_JSON_FORMAT_EXCEPTION = -9716;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_MSG = "msg";
    private static final String FIELD_DATA = "data";

    @Override
    protected T parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        result = JSON;
        T data = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has(FIELD_CODE) && jsonObject.has(FIELD_MSG)) {
                int code = jsonObject.optInt(FIELD_CODE);
                if (code == 0) {
                    data = this.parseResult(result);
                } else {
                    setErrorCodeAndMsg(code, jsonObject.getString(FIELD_MSG));
                }
            } else {
                setErrorCodeAndMsg(ERROR_JSON_NO_FIELDS, "don't have " + FIELD_CODE + " and " + FIELD_MSG + " field");
            }
        } catch (JSONException e) {
            setErrorCodeAndMsg(ERROR_JSON_FORMAT_EXCEPTION, e.toString());
        }

        return data;
        //return super.parseResponseBody(responseBody);
    }

}