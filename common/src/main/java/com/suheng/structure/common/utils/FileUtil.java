package com.suheng.structure.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class FileUtil {

    /**
     * 以比特流的形式读取输入流数据
     */
    public static byte[] readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048 * 2];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();
        return data;
    }
}
