package com.suheng.structure.socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.suheng.structure.common.arouter.RouteTable;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoService extends IntentService {
    public static final String TAG = "SocketWatch";
    private boolean mIsNotExceptionInterrupted = true;

    //需注意，必须传入参数
    public EchoService() {
        super("TCPEcho");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

            ServerSocket serverSocket = new ServerSocket(RouteTable.SOCKET_PORT);
            while (mIsNotExceptionInterrupted) {
                Log.d(TAG, "TCPEcho, 服务器正在运行，等待客户端连接......");
                Socket socket = serverSocket.accept();//accept()方法会阻塞线程
                /*
                多线程处理机制：每一个客户端连接之后都启动一个线程，以保证服务器可以同时与多个客户端通信。如果是单线程
                处理机制，那么服务器每次只能与一个客户端连接，其他客户端无法同时连接服务器，要等待服务器出现空闲才可以连接。
                */
                cachedThreadPool.execute(new SocketServer(socket));
            }

            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            mIsNotExceptionInterrupted = false;
        }
    }

}
