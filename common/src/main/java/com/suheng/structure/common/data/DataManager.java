package com.suheng.structure.common.data;

import android.content.Context;

import com.suheng.structure.common.data.db.DatabaseManager;
import com.suheng.structure.common.data.net.NetworkManager;
import com.suheng.structure.common.data.net.request.LoginTask;
import com.suheng.structure.common.data.prefs.PrefsManager;

public class DataManager {
    private PrefsManager mPrefsManager;
    private DatabaseManager mDatabaseManager;
    private NetworkManager mNetworkManager;

    public void init(Context context) {
        mPrefsManager = new PrefsManager(context);
        mNetworkManager = new NetworkManager();
        mDatabaseManager = new DatabaseManager(context);
    }

    public void setLoginSuccessful(boolean isSuccessful) {
        mPrefsManager.setLoginSuccessful(isSuccessful);
    }

    public boolean isLoginSuccessful() {
        return mPrefsManager.isLoginSuccessful();
    }

    public LoginTask doLoginRequest(String name, String pwd) {
        return mNetworkManager.doLoginRequest(name, pwd);
    }

    public LoginTask doExitLoginRequest() {
        return mNetworkManager.doExitLoginRequest();
    }
}
