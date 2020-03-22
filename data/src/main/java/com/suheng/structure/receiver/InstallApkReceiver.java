package com.suheng.structure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallApkReceiver extends BroadcastReceiver {
    public static final String TAG = InstallApkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String dataString = intent.getDataString();
        Log.d(TAG, "action: " + action + ", dataString = " + dataString);
    }

}
