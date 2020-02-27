package com.suheng.structure.common.data.net.callback;

public interface LoginListener {
    void onLoginFail(String reason, int code);

    void onLoginSuccess();
}
