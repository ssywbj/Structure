package com.suheng.structure.bluetooth.connect;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.suheng.structure.common.utils.FileUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Socket通信服务端
 */
public class BluetoothServer implements Runnable {
    public static final String TAG = "SocketWatch";
    private BluetoothSocket mBluetoothSocket;

    public BluetoothServer(BluetoothSocket bluetoothSocket) {
        mBluetoothSocket = bluetoothSocket;
    }

    @Override
    public void run() {
        try {
            //用于接收客户端发送过来的消息
            InputStream inputStream = mBluetoothSocket.getInputStream();
            //用于向客户端发送消息
            OutputStream outputStream = mBluetoothSocket.getOutputStream();

            while (true) {
                byte[] bytes = FileUtil.readInputStream(inputStream);//读取客户端消息
                String source = new String(bytes);
                if ("bye".equals(source.toLowerCase())) {
                    outputStream.write("bye".getBytes(/*StandardCharsets.UTF_8*/));
                    break;//跳出循环，return;//跳出方法
                } else {
                    Log.d(TAG, "receive：" + source);
                    outputStream.write(("ECHO: " + source).getBytes(/*StandardCharsets.UTF_8*/));//向客户端发送消息
                }
            }

            outputStream.close();
            inputStream.close();
            mBluetoothSocket.close();//收到指令后，服务端不仅主动关闭连接，还会向客户端回应该关闭操作
            Log.d(TAG, "服务端关闭IO操作流与Socket连接！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
