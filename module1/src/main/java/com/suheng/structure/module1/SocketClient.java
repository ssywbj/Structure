package com.suheng.structure.module1;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    //private static final String TAG = SocketClient.class.getSimpleName();
    private static final String TAG = "SocketWatch";
    private static SocketClient sInstance = new SocketClient();
    private static final String CHARSET_NAME = "UTF-8";
    private ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();
    private ConnectRunnable mConnectRunnable;

    private SocketClient() {
    }

    public static SocketClient getInstance() {
        return sInstance;
    }

    public void connect(String host, int port) {
        mConnectRunnable = new ConnectRunnable(host, port);
        mCachedThreadPool.execute(mConnectRunnable);
    }

    public void sendRequest(String request) {
        if (mConnectRunnable != null && mConnectRunnable.isConnected()) {
            mCachedThreadPool.execute(new WorkRunnable(mConnectRunnable.getOutput(), request));
        }
    }

    public void disconnect() {
        this.sendRequest("bye");
    }

    private class ConnectRunnable implements Runnable {
        private Socket mSocket;
        private String mHost;
        private int mPort;
        private PrintWriter mOutput;

        ConnectRunnable(String host, int port) {
            mHost = host;
            mPort = port;
        }

        @Override
        public void run() {
            try {
                //mSocket = new Socket(mHost, mPort);
                mSocket = new Socket();
                mSocket.connect(new InetSocketAddress(mHost, mPort), 1000);
                if (mSocket.isConnected()) {
                    Log.d(TAG, "client connect success, remote socket address: " + mSocket
                            .getRemoteSocketAddress() + ", local socket address: " + mSocket.getLocalSocketAddress());

                    BufferedReader input = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), CHARSET_NAME));
                    mOutput = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream(), CHARSET_NAME), true);

                    while (true) {
                        String readLine = input.readLine();
                        if (readLine == null) {
                            break;
                        } else {
                            Log.d(TAG, "server return info: " + readLine);
                        }
                    }

                    input.close();//客户端收到服务端主动关闭连接的响应后，会跳出while循环往下执行，关闭IO操作流和Socket连接
                    mOutput.close();
                    mSocket.close();
                    Log.d(TAG, "client close connect, ip: " + mSocket.getInetAddress() + ", port: " + mSocket.getPort()
                            + ", local ip: " + mSocket.getLocalAddress() + ", local port: " + mSocket.getLocalPort());
                } else {
                    Log.d(TAG, "client connect fail, please try again!");
                }
            } catch (Exception e) {
                Log.e(TAG, "client connect fail: " + e.toString());
            }
        }

        boolean isConnected() {
            return mSocket != null && mSocket.isConnected();
        }

        PrintWriter getOutput() {
            return mOutput;
        }
    }

    private class WorkRunnable implements Runnable {
        private PrintWriter mOutput;
        private String mRequest;

        WorkRunnable(PrintWriter output, String request) {
            mOutput = output;
            mRequest = request;
        }

        @Override
        public void run() {
            if (mOutput != null && mRequest != null) {
                mOutput.println(mRequest);
            }
        }
    }

}
