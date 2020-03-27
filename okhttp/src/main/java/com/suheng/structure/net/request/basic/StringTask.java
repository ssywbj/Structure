package com.suheng.structure.net.request.basic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnFinishListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;

public abstract class StringTask<T> extends BasicTask {
    private static final String JSON = "{" + "\"code\":0" + ",\"msg\":密码错误" + ",data:{"
            + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";
    private static final int MSG_ON_FINISH = 1;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_MSG = "msg";
    private static final String FIELD_DATA = "data";

    private UIHandler mUIHandler;
    private OnFinishListener<T> mOnFinishListener;
    private T mResult;

    protected abstract T parseResult(String result);

    protected StringTask() {
        mUIHandler = new UIHandler(this);
    }

    @Override
    protected T parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        result = JSON;
        Log.d(getLogTag(), "onResponse: " + result);

        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has(FIELD_CODE) && jsonObject.has(FIELD_MSG)) {
                if (mOnFinishListener == null) {
                    return null;
                }

                int code = jsonObject.optInt(FIELD_CODE);
                if (code == 0) {
                    mResult = this.parseResult(jsonObject.optString(FIELD_DATA, ""));
                    mUIHandler.sendEmptyMessage(MSG_ON_FINISH);
                } else {
                    setErrorCodeAndMsg(code, jsonObject.getString(FIELD_MSG));
                }
            } else {
                setErrorCodeAndMsg(-2222, "don't have " + FIELD_CODE + " and " + FIELD_MSG + " field");
            }
        } catch (JSONException e) {
            setErrorCodeAndMsg(-3333, "don't have " + FIELD_CODE + " and " + FIELD_MSG + " field");
        }

        return null;
    }

    private void onTaskFinish() {
        mOnFinishListener.onFinish(mResult);
    }

    private static class UIHandler extends Handler {
        private WeakReference<StringTask> mTaskReference;

        private UIHandler(StringTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            StringTask task = mTaskReference.get();
            if (task == null) {
                return;
            }

            if (msg.what == MSG_ON_FINISH) {
                if (msg.obj instanceof String) {
                    task.onTaskFinish();
                }
            }
        }
    }

}
