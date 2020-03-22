package com.suheng.structure.module2;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class Module2Service extends Service {
    private static final String TAG = Module2Service.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " onCreate: " + TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " onStartCommand: " + TAG);
        this.bindModule1Service();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void bindModule1Service() {
        Intent intent = new Intent();
        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClassName("com.suheng.structure.module1",
                "com.suheng.structure.module1.Module1Service");*/
        intent.setComponent(new ComponentName("com.suheng.structure.module1",
                "com.suheng.structure.module1.Module1Service"));
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: " + name + ", service: " + service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: " + name);
            bindModule1Service();
        }
    };
}
