package com.suheng.structure.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREFS_IS_LOGIN_SUCCESSFUL = "prefs_is_login_successful";
    private SharedPreferences mPrefs;

    public PrefsManager(Context context) {
        mPrefs = context.getSharedPreferences("ff", Context.MODE_PRIVATE);
    }

    public void setLoginSuccessful(boolean isSuccessful) {
        mPrefs.edit().putBoolean(PREFS_IS_LOGIN_SUCCESSFUL, isSuccessful).apply();
    }

    public boolean isLoginSuccessful() {
        return mPrefs.getBoolean(PREFS_IS_LOGIN_SUCCESSFUL, false);
    }
}
