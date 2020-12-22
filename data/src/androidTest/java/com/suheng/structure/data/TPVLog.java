package com.suheng.structure.data;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TPVLog {
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public static void d(String tag, String msg) {
        //打印栈帧中的所有方法调用
        //String stringMsg = new String("");
        StringBuilder stringMsg = new StringBuilder();
        //String stringFlag = new String("tpv_BBB");
        StringBuilder stringFlag = new StringBuilder("tpv_BBB");
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            for (int i = stackTrace.length - 1; i >= 0; i--) {

                String string = stackTrace[i].toString();
//                if (string.contains("com.bluetrum.abotademo") && !string.contains("$")) {
                if (string.contains("com.bluetrum.abotademo")) {

                    if (!string.contains("_")) {
                        String tempString;
                        tempString = string.replace("(", "tpv_AAA");
                        tempString = tempString.replace(")", "");

                        String[] strs = tempString.split("tpv_AAA");
                        String logString;
                        logString = strs[strs.length - 1];
                        //stringMsg = stringMsg + stringFlag + logString;
                        stringMsg.append(stringFlag).append(logString);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //String longStrMsg = new String("");
        StringBuilder longStrMsg = new StringBuilder();
        String left = "(";
        String right = ")";
        //String[] strs = stringMsg.split(stringFlag);
        String[] strs = stringMsg.toString().split(stringFlag.toString());
        for (int i = 0; i < strs.length; i++) {
            String tmp = strs[i];
            if (tmp.length() > 0) {
                if (i == strs.length - 1) {
                    longStrMsg.append(longStrMsg).append(left).append(tmp).append(right);
                } else {
                    longStrMsg.append("[").append(tmp).append("]");
                }
            }
        }

        String finalStrMsg = longStrMsg + "-" + msg;
        Log.d(tag, finalStrMsg);
        finalStrMsg = finalStrMsg.replace("(", "[");
        finalStrMsg = finalStrMsg.replace(")", "]");
        finalStrMsg = finalStrMsg.replace(".java", "");

        Date date = new Date();
        SimpleDateFormat datatime = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
        SimpleDateFormat filename = new SimpleDateFormat("yyyyMMdd");
        String strDate = datatime.format(date);
        String strFile = filename.format(date);

        File logFile = new File("sdcard/tpvlog_" + strFile + ".log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.i("barry", e.getMessage(), e);
            }
        }

        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            String launchflag = "程序启动";
            if (finalStrMsg.contains(launchflag)) {
                buf.append("\n\n-------------------------------------------------------------------\n");
            }
            buf.append(strDate);
            buf.append(finalStrMsg);
            buf.append("\n");
            buf.close();
        } catch (IOException e) {
            Log.i("barry", e.getMessage(), e);
        }
    }

    public synchronized static void dLog(final String tag, final String msg) {
        sExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                d(tag, msg);
            }
        });
    }
}
