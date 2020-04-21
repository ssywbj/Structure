package com.suheng.structure.bluetooth.connect;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.suheng.structure.common.utils.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothClient {
    //private static final String TAG = SocketClient.class.getSimpleName();
    private static final String TAG = "SocketWatch";
    private static BluetoothClient sInstance = new BluetoothClient();
    private ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();
    private ConnectRunnable mConnectRunnable;

    private BluetoothClient() {
    }

    public static BluetoothClient getInstance() {
        return sInstance;
    }

    public void init(BluetoothSocket bluetoothSocket) {
        mConnectRunnable = new ConnectRunnable(bluetoothSocket);
        mCachedThreadPool.execute(mConnectRunnable);
    }

    public void sendRequest(String request) {
        if (mConnectRunnable != null && mConnectRunnable.isConnected()) {
            mCachedThreadPool.execute(new WorkRunnable(mConnectRunnable.getOutput(), request));
        }
    }

    public void destroy() {
        this.sendRequest("bye");
    }

    private class ConnectRunnable implements Runnable {
        private BluetoothSocket mBluetoothSocket;
        private OutputStream mOutputStream;
        private boolean mIsFlag = true;

        ConnectRunnable(BluetoothSocket bluetoothSocket) {
            mBluetoothSocket = bluetoothSocket;
            if (!mBluetoothSocket.isConnected()) {
                try {
                    mBluetoothSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            try {
                if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                    Log.d(TAG, "connect bluetooth successful, BluetoothSocket: " + mBluetoothSocket);

                    InputStream inputStream = mBluetoothSocket.getInputStream();
                    mOutputStream = mBluetoothSocket.getOutputStream();

                    while (mIsFlag) {
                        byte[] bytes = FileUtil.readInputStream(inputStream);
                        Log.d(TAG, "server return info: " + new String(bytes/*, StandardCharsets.UTF_8*/));
                    }

                    inputStream.close();//客户端收到服务端主动关闭连接的响应后，会跳出while循环往下执行，关闭IO操作流和Socket连接
                    mOutputStream.close();
                    mBluetoothSocket.close();
                } else {
                    Log.d(TAG, "client connect fail, please try again!");
                }
            } catch (Exception e) {
                mIsFlag = false;
                Log.e(TAG, "client connect fail: " + e.toString());
            }
        }

        boolean isConnected() {
            return mBluetoothSocket != null && mBluetoothSocket.isConnected();
        }

        OutputStream getOutput() {
            return mOutputStream;
        }
    }

    private class WorkRunnable implements Runnable {
        private OutputStream mOutputStream;
        private String mRequest;

        WorkRunnable(OutputStream outputStream, String request) {
            mOutputStream = outputStream;
            mRequest = request;
        }

        @Override
        public void run() {
            if (mOutputStream != null && mRequest != null) {
                try {
                    mOutputStream.write(mRequest.getBytes(/*StandardCharsets.UTF_8*/));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
