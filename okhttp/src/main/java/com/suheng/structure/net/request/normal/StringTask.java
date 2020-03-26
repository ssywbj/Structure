package com.suheng.structure.net.request.normal;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnResponseListener;
import com.suheng.structure.net.request.basic.BasicTask;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;

public abstract class StringTask extends BasicTask<String> {
    private static final String JSON = "{" + "\"code\":0" + ",\"msg\":密码错误" + ",data:{"
            + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";
    private static final int MSG_ON_RESPONSE = 1;
    private UIHandler mUIHandler;
    private OnResponseListener mOnResponseListener;

    protected StringTask() {
        mUIHandler = new UIHandler(this);
    }

    /*@Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        result = JSON;
        this.parseResponseResult(result);
        Log.d(getLogTag(), "onResponse: " + result);
    }*/

    @Override
    protected String parseResponseBody(@NotNull ResponseBody responseBody) throws Exception {
        String result = responseBody.string();
        result = JSON;
        return result;
    }

    protected void parseResponseResult(String result) {
        if (mOnResponseListener != null) {
            Message msg = new Message();
            msg.what = MSG_ON_RESPONSE;
            msg.obj = result;
            mUIHandler.sendMessage(msg);
        }
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        mOnResponseListener = onResponseListener;
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

            if (msg.what == MSG_ON_RESPONSE) {
                if (msg.obj instanceof String) {
                    task.mOnResponseListener.onResponse((String) msg.obj);
                }
            }
        }
    }
}