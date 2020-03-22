package com.suheng.structure.net.request.basic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnFailureListener;

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

public abstract class BasicTask {
    private static final int MSG_ON_FAILURE = 0;
    private String mLogTag;
    private Map<String, String> mArguments = new HashMap<>();
    private UIHandler mUIHandler;
    private OnFailureListener mOnFailureListener;
    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

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
                Log.e(getLogTag(), "onFailure, exception: " + e.toString() + ", call: " + call);
                sendFailureMessage(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Log.d(getLogTag(), "onResponse code: " + response.code() + ", msg: " + response.message() +
                        ", response: " + response);
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        Log.e(getLogTag(), "onResponse, ResponseBody is null");
                        return;
                    }

                    try {
                        parseResponseBody(responseBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(getLogTag(), "onResponse, parse ResponseBody cause exception: " + e.toString());
                        sendFailureMessage(e.toString());
                    } finally {
                        responseBody.close();
                    }
                }
            }
        });
    }

    protected void cancelTask() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    protected void sendFailureMessage(@NotNull String exception) {
        if (mOnFailureListener != null) {
            Message msg = new Message();
            msg.what = MSG_ON_FAILURE;
            msg.obj = exception;
            mUIHandler.sendMessage(msg);
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

    public void setOnFailureListener(OnFailureListener onFailureListener) {
        mOnFailureListener = onFailureListener;
    }

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

            if (msg.what == MSG_ON_FAILURE) {
                if (msg.obj instanceof String) {
                    task.mOnFailureListener.onFailure((String) msg.obj);
                }
            }
        }
    }

    protected abstract String getURL();

    protected abstract void parseResponseBody(@NotNull ResponseBody responseBody) throws Exception;
}