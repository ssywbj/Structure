package com.suheng.structure.net.request.basic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;
import com.suheng.structure.net.callback.OnProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class OkHttpTask<T> {
    private static final int ERROR_CODE_ON_FEATURE = -100;
    private static final int ERROR_CODE_RESPONSE_BODY_NULL = -101;
    private static final int ERROR_CODE_RESPONSE_BODY_PARSE_EXCEPTION = -102;
    protected static final int ERROR_CODE_DOWNLOAD_EXCEPTION = -103;

    private static final int MSG_ON_FAILURE = 0;
    private static final int MSG_ON_FINISH = 1;
    private static final int MSG_ON_PROGRESS = 2;

    private String mLogTag;
    private Map<String, String> mArguments = new HashMap<>();
    private UIHandler mUIHandler;

    private OnFailureListener mOnFailureListener;
    private OnFinishListener<T> mOnFinishListener;
    private int code;
    private String mErrorMsg;
    private T mData;

    private OnProgressListener mOnProgressListener;
    protected long mTotal, mProgress;
    protected double mPercentage;
    private double mTakeTime;
    private long mCurrentTimeMillis;

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    protected abstract String getURL();

    protected abstract void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception;

    private static class UIHandler extends Handler {
        private WeakReference<OkHttpTask> mTaskReference;

        private UIHandler(OkHttpTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            OkHttpTask task = mTaskReference.get();
            if (task == null) {
                return;
            }

            switch (msg.what) {
                case MSG_ON_FAILURE:
                    task.onTaskFailure();
                    break;
                case MSG_ON_FINISH:
                    task.onTaskFinish();
                    break;
                case MSG_ON_PROGRESS:
                    task.onTaskProgress();
                    break;
                default:
                    break;
            }
        }
    }

    protected OkHttpTask() {
        mLogTag = OkHttpTask.class.getSimpleName();
        mUIHandler = new UIHandler(this);

        /*ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledTlsVersions()
                //java.net.UnknownServiceException: Unable to find acceptable protocols. isFallback=false,
                // modes=[ConnectionSpec(cipherSuites=[TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                // TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                // TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                // TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                // TLS_ECDHE_ECDSA_WITH_RC4_128_SHA, TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                // TLS_DHE_RSA_WITH_AES_128_CBC_SHA], tlsVersions=[TLS_1_2],
                // supportsTlsExtensions=true)], supported protocols=[SSLv3, TLSv1]
                *//*.tlsVersions(TlsVersion.SSL_3_0, TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA)*//*
                .build();
        mOkHttpClient = new OkHttpClient.Builder().connectionSpecs(Collections.singletonList(connectionSpec)).build();*/
    }

    public OkHttpTask<T> doRequest() {
        return this.doRequest(this);
    }

    public OkHttpTask<T> doRequest(Object tag) {
        HttpUrl httpUrl = HttpUrl.parse(this.getURL());
        if (httpUrl == null) {
            return null;
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (Map.Entry<String, String> entry : mArguments.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Log.d(getLogTag(), "get request, url: " + urlBuilder.build());

        Request request = new Request.Builder().tag(tag).headers(this.getHeaders()).url(urlBuilder.build()).build();
        this.enqueue(request);

        return this;
    }

    public OkHttpTask<T> doPostRequest() {
        return this.doPostRequest(this);
    }

    public OkHttpTask<T> doPostRequest(Object tag) {
        Request request = new Request.Builder().tag(tag).headers(this.getHeaders()).url(this.getURL()).post(this.getRequestBody()).build();
        this.enqueue(request);

        return this;
    }

    protected RequestBody getRequestBody() {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : mArguments.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        Log.d(getLogTag(), "post request, url: " + this.getURL() + ", arguments: " + formBuilder.toString());

        return formBuilder.build();
    }

    protected Headers getHeaders() {
        return new Headers.Builder().build();
    }

    protected void enqueue(final Request request) {
        mCurrentTimeMillis = System.currentTimeMillis();

        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                setFailureCallback(ERROR_CODE_ON_FEATURE, "onFailure exception: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        setFailureCallback(ERROR_CODE_RESPONSE_BODY_NULL, "onResponse ResponseBody is null");
                        return;
                    }

                    try {
                        parseResponseBody(responseBody);
                    } catch (Exception e) {
                        setFailureCallback(ERROR_CODE_RESPONSE_BODY_PARSE_EXCEPTION
                                , "onResponse parse ResponseBody cause exception: " + e.toString());
                    } finally {
                        responseBody.close();
                    }
                } else {
                    setFailureCallback(response.code(), "onResponse msg:  " + response.message());
                }

                response.close();
            }
        });
    }

    protected void setFailureCallback(int code, String errorMsg) {
        mTakeTime = 1.0 * (System.currentTimeMillis() - mCurrentTimeMillis) / 1000;
        this.code = code;
        mErrorMsg = errorMsg;
        Log.e(getLogTag(), "failure-->code: " + code + ", error msg: " + mErrorMsg + ", take time: " + mTakeTime);

        if (mOnFailureListener != null) {
            mUIHandler.sendEmptyMessage(MSG_ON_FAILURE);
        }
    }

    private void onTaskFailure() {
        mOnFailureListener.onFailure(code, mErrorMsg);
    }

    protected void setFinishCallback(T data) {
        mTakeTime = 1.0 * (System.currentTimeMillis() - mCurrentTimeMillis) / 1000;
        mData = data;
        Log.d(getLogTag(), "finish-->data: " + mData + ", take time: " + mTakeTime);

        if (mOnFinishListener != null) {
            mUIHandler.sendEmptyMessage(MSG_ON_FINISH);
        }
    }

    private void onTaskFinish() {
        mOnFinishListener.onFinish(mData);
    }

    protected void setProgressCallback() {
        Log.d(getLogTag(), "progress-->percentage: " + mPercentage + ", progress: " + mProgress + ", total: " + mTotal);

        if (mOnProgressListener != null) {
            mUIHandler.sendEmptyMessage(MSG_ON_PROGRESS);
        }
    }

    private void onTaskProgress() {
        mOnProgressListener.onProgress(mPercentage, mProgress, mTotal);
    }

    protected void cancelTask() {
        if (mCall != null) {
            mCall.cancel();
            mUIHandler.removeMessages(MSG_ON_FAILURE);
            mUIHandler.removeMessages(MSG_ON_FINISH);
        }
    }

    protected void addArgument(String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        mArguments.put(key, String.valueOf(value));
    }

    protected void addArgument(String key, int value) {
        this.addArgument(key, String.valueOf(value));
    }

    protected void addArgument(String key, long value) {
        this.addArgument(key, String.valueOf(value));
    }

    protected void addArgument(String key, double value) {
        this.addArgument(key, String.valueOf(value));
    }

    protected void addArgument(String key, boolean value) {
        this.addArgument(key, String.valueOf(value));
    }

    protected void addArgument(String key, Object value) {
        this.addArgument(key, String.valueOf(value));
    }

    public String getLogTag() {
        return mLogTag;
    }

    public OkHttpTask<T> addOnFailureListener(OnFailureListener onFailureListener) {
        mOnFailureListener = onFailureListener;
        return this;
    }

    public OkHttpTask<T> addOnFinishListener(OnFinishListener<T> onFinishListener) {
        mOnFinishListener = onFinishListener;
        return this;
    }

    public OkHttpTask<T> addOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
        return this;
    }

    public double getTakeTime() {
        return mTakeTime;
    }
}