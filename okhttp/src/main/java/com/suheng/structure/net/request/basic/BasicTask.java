package com.suheng.structure.net.request.basic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BasicTask<T> {
    private static final int ERROR_CODE_ON_FEATURE = -100;
    private static final int ERROR_CODE_RESPONSE_BODY_NULL = -101;
    private static final int ERROR_CODE_DATA_NULL = -102;
    private static final int ERROR_CODE_RESPONSE_BODY_PARSE_EXCEPTION = -103;
    public static final int ERROR_CODE_DOWNLOAD_EXCEPTION = -104;

    private static final int MSG_ON_FAILURE = 0;
    private static final int MSG_ON_FINISH = 1;

    private String mLogTag;
    private Map<String, String> mArguments = new HashMap<>();
    private UIHandler mUIHandler;

    private OnFailureListener mOnFailureListener;
    private OnFinishListener<T> mOnFinishListener;

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private int code;
    private String mErrorMsg;
    private T mData;

    protected abstract String getURL();

    protected abstract T parseResponseBody(@NotNull ResponseBody responseBody) throws Exception;

    private static class UIHandler extends Handler {
        private WeakReference<BasicTask> mTaskReference;

        private UIHandler(BasicTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            BasicTask task = mTaskReference.get();
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
                default:
                    break;
            }
        }
    }

    protected BasicTask() {
        mLogTag = getClass().getSimpleName();
        mUIHandler = new UIHandler(this);

        ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledTlsVersions()
                //java.net.UnknownServiceException: Unable to find acceptable protocols. isFallback=false,
                // modes=[ConnectionSpec(cipherSuites=[TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                // TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                // TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                // TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                // TLS_ECDHE_ECDSA_WITH_RC4_128_SHA, TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                // TLS_DHE_RSA_WITH_AES_128_CBC_SHA], tlsVersions=[TLS_1_2],
                // supportsTlsExtensions=true)], supported protocols=[SSLv3, TLSv1]
                /*.tlsVersions(TlsVersion.SSL_3_0, TlsVersion.TLS_1_0)
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
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA)*/
                .build();

        mOkHttpClient = new OkHttpClient.Builder().connectionSpecs(Collections.singletonList(connectionSpec)).build();
    }

    public void doRequest() {
        this.doRequest(this);
    }

    public void doRequest(Object tag) {
        HttpUrl httpUrl = HttpUrl.parse(this.getURL());
        if (httpUrl == null) {
            return;
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (Map.Entry<String, String> entry : mArguments.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Log.d(getLogTag(), "get request, url: " + urlBuilder.build());

        Request request = new Request.Builder().tag(tag).headers(this.getHeaders()).url(urlBuilder.build()).build();
        this.enqueue(request);
    }

    public void doPostRequest() {
        this.doPostRequest(this);
    }

    public void doPostRequest(Object tag) {
        Request request = new Request.Builder().tag(tag).headers(this.getHeaders()).url(this.getURL()).post(this.getRequestBody()).build();
        this.enqueue(request);
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
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                setErrorCodeAndMsg(ERROR_CODE_ON_FEATURE, "onFailure exception: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        setErrorCodeAndMsg(ERROR_CODE_RESPONSE_BODY_NULL, "onResponse ResponseBody is null");
                        return;
                    }

                    try {
                        mData = parseResponseBody(responseBody);
                        if (mData == null) {
                            setErrorCodeAndMsg(ERROR_CODE_DATA_NULL, "data is null");
                        } else {
                            if (mOnFinishListener != null) {
                                mUIHandler.sendEmptyMessage(MSG_ON_FINISH);
                            }
                        }
                    } catch (Exception e) {
                        setErrorCodeAndMsg(ERROR_CODE_RESPONSE_BODY_PARSE_EXCEPTION
                                , "onResponse parse ResponseBody cause exception: " + e.toString());
                    } finally {
                        responseBody.close();
                    }
                } else {
                    setErrorCodeAndMsg(response.code(), "onResponse msg:  " + response.message());
                }

                response.close();
            }
        });
    }

    protected void setErrorCodeAndMsg(int code, String errorMsg) {
        this.code = code;
        mErrorMsg = errorMsg;
        Log.e(getLogTag(), "code: " + code + ", " + mErrorMsg);

        if (mOnFailureListener != null) {
            mUIHandler.sendEmptyMessage(MSG_ON_FAILURE);
        }
    }

    private void onTaskFinish() {
        mOnFinishListener.onFinish(mData);
    }

    private void onTaskFailure() {
        mOnFailureListener.onFailure(code, mErrorMsg);
    }

    protected void addArgument(String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        mArguments.put(key, String.valueOf(value));
    }

    protected void cancelTask() {
        if (mCall != null) {
            mCall.cancel();
            mUIHandler.removeMessages(MSG_ON_FAILURE);
            mUIHandler.removeMessages(MSG_ON_FINISH);
        }
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

    public void setOnFailureListener(OnFailureListener onFailureListener) {
        mOnFailureListener = onFailureListener;
    }

    public void setOnFinishListener(OnFinishListener<T> onFinishListener) {
        mOnFinishListener = onFinishListener;
    }
}