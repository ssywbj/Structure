package com.suheng.structure.net.request.normal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;

public abstract class JsonTask<T> extends StringTask<T> {
    private static final String JSON = "{" + "\"code\":1" + ",\"msg\":密码错误" + ",data:{"
            + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";
    private static final int ERROR_JSON_NO_FIELDS = -9715;
    private static final int ERROR_JSON_FORMAT_EXCEPTION = -9716;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_MSG = "msg";

    @Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        result = JSON;
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has(FIELD_CODE) && jsonObject.has(FIELD_MSG)) {
                int code = jsonObject.optInt(FIELD_CODE);
                if (code == 0) {
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
}