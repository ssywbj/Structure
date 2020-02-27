package com.suheng.structure.net.request.normal;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnResultListener;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public abstract class CodeMsgTask<RightResult, ErrorResult> extends RequestTask {
    private static final int MSG_ERROR_RESULT = 0;
    private static final int MSG_RIGHT_RESULT = 1;
    private static final int ERROR_EXCEPTION = -9716;
    private static final int ERROR_NO_FIELDS = -9715;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_MSG = "msg";

    private int mErrorCode;
    private String mErrorMsg;
    private ErrorResult mErrorResult;
    private RightResult mRightResult;

    private UIHandler mUIHandler;
    private OnResultListener<RightResult, ErrorResult> mOnResultListener;

    protected CodeMsgTask() {
        mUIHandler = new UIHandler(this);
    }

    @Override
    protected void parseResponseResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has(FIELD_CODE) && jsonObject.has(FIELD_MSG)) {
                if (mOnResultListener == null) {
                    return;
                }

                int code = jsonObject.optInt(FIELD_CODE);
                if (code == 0) {
                    mRightResult = this.getRightResult(result);

                    mUIHandler.sendEmptyMessage(MSG_RIGHT_RESULT);
                } else {
                    mErrorCode = jsonObject.getInt(FIELD_CODE);
                    mErrorMsg = jsonObject.getString(FIELD_MSG);
                    mErrorResult = getErrorResult(result);

                    mUIHandler.sendEmptyMessage(MSG_ERROR_RESULT);
                }
            } else {
                mErrorCode = ERROR_NO_FIELDS;
                mErrorMsg = "don't have " + FIELD_CODE + " and " + FIELD_MSG + " field";
                mUIHandler.sendEmptyMessage(MSG_ERROR_RESULT);
            }
        } catch (Exception e) {
            mErrorCode = ERROR_EXCEPTION;
            mErrorMsg = e.toString();
            mUIHandler.sendEmptyMessage(MSG_ERROR_RESULT);
        }
    }

    private void onTaskResponse() {
        mOnResultListener.onRightResult(mRightResult);
    }

    private void onTaskFailure() {
        mOnResultListener.onErrorResult(mErrorCode, mErrorMsg, mErrorResult);
    }

    public void setOnResultListener(OnResultListener<RightResult, ErrorResult> onResultListener) {
        mOnResultListener = onResultListener;
    }

    private static class UIHandler extends Handler {
        private WeakReference<CodeMsgTask> mTaskReference;

        private UIHandler(CodeMsgTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            CodeMsgTask task = mTaskReference.get();
            if (task == null) {
                return;
            }

            switch (msg.what) {
                case MSG_RIGHT_RESULT:
                    task.onTaskResponse();
                    break;
                case MSG_ERROR_RESULT:
                    task.onTaskFailure();
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract ErrorResult getErrorResult(String result);

    protected abstract RightResult getRightResult(String result);
}