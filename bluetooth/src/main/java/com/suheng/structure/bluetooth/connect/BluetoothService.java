package com.suheng.structure.bluetooth.connect;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothService extends IntentService {
    private static final String UUID_NAME = "00001105-0000-1000-8000-00805f9B34FB";
    public static final String TAG = "SocketWatch";
    private boolean mIsNotExceptionInterrupted = true;

    //需注意，必须传入参数
    public BluetoothService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

            BluetoothServerSocket bluetoothServerSocket = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingInsecureRfcommWithServiceRecord(TAG, UUID.fromString(UUID_NAME));

            while (mIsNotExceptionInterrupted) {
                Log.d(TAG, "TCPEcho, 服务器正在运行，等待客户端连接......");
                BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept();
                cachedThreadPool.execute(new BluetoothServer(bluetoothSocket));
            }

            bluetoothServerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            mIsNotExceptionInterrupted = false;
        }
    }

}
