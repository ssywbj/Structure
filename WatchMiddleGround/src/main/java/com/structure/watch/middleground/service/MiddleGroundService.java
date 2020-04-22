package com.structure.watch.middleground.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MiddleGroundService extends Service {

    private static final String TAG = MiddleGroundService.class.getSimpleName();
    private BluetoothConnectHelper mBluetoothConnectHelper = new BluetoothConnectHelper();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + ", onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + ", onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + ", onStartCommand");
        if (mBluetoothConnectHelper.getState() == BluetoothConnectHelper.STATE_NONE) {
            mBluetoothConnectHelper.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + ", onDestroy");
        mBluetoothConnectHelper.stop();
    }
}
