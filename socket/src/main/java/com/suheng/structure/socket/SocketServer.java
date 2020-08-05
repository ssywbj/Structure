package com.suheng.structure.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Socket通信服务端
 */
public class SocketServer implements Runnable {
    public static final String TAG = "SocketWatch";
    private static final String CHARSET_NAME = "UTF-8";
    private Socket mSocket;

    public SocketServer(Socket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        try {
            //用于接收客户端发送过来的消息
            InputStream inputStream = mSocket.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream, CHARSET_NAME));
            //用于向客户端发送消息
            PrintWriter output = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream(), CHARSET_NAME), true);

            while (true) {
                String source = input.readLine();//读取客户端消息
                if (source != null) {
                    if ("bye".equals(source.toLowerCase())) {
                        output.println("客户端发指令请求断开连接（服务端返回）" + "\r");
                        break;//跳出循环，return;//跳出方法
                    } else {
                        Log.d(TAG, "收到客户端消息：" + source);
                        output.println("ECHO: " + source + "\r");//向客户端发送消息
                    }
                }
            }

            output.close();
            input.close();
            mSocket.close();//收到指令后，服务端不仅主动关闭连接，还会向客户端回应该关闭操作
            Log.d(TAG, "服务端关闭IO操作流与Socket连接！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void start() {
        try {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Log.d(TAG, "服务器正在运行，等待客户端连接......");
                Socket socket = serverSocket.accept();
                *//*
                 多线程处理机制：每一个客户端连接之后都启动一个线程，以保证服务器可以同时与多个客户端通信。如果是单线程
                 处理机制，那么服务器每次只能与一个客户端连接，其他客户端无法同时连接服务器，要等待服务器出现空闲才可以连接。
                 *//*
                cachedThreadPool.execute(new SocketServer(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
