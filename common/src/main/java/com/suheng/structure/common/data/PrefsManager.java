package com.suheng.structure.common.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.suheng.structure.common.arouter.RouteTable;

@Route(path = RouteTable.COMMON_PROVIDER_PREFS_MANAGER)
public class PrefsManager implements IProvider {
    private static final String LOGIN_STATUS = "login_status";
    private SharedPreferences mPreferences;

    @Override
    public void init(Context context) {
        mPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void putLoginStatus(boolean value) {
        mPreferences.edit().putBoolean(LOGIN_STATUS, value).apply();
    }

    public boolean getLoginStatus() {
        return mPreferences.getBoolean(LOGIN_STATUS, false);
    }
}
