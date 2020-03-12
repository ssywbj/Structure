package com.suheng.structure.data;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.suheng.structure.data.db.DatabaseManager;
import com.suheng.structure.data.net.NetworkManager;
import com.suheng.structure.data.net.request.LoginTask;
import com.suheng.structure.data.prefs.PrefsManager;

public class DataManager implements IProvider {
    private PrefsManager mPrefsManager;
    private DatabaseManager mDatabaseManager;
    private NetworkManager mNetworkManager;

    @Override
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
}
