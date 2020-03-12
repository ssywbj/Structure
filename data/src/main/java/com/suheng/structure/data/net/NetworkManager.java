package com.suheng.structure.data.net;

import com.suheng.structure.data.net.request.LoginTask;

public class NetworkManager {

    public LoginTask doLoginRequest(String name, String pwd) {
        final LoginTask loginTask = new LoginTask(name, pwd);
        loginTask.doRequest();
        return loginTask;
    }
}
