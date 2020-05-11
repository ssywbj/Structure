package com.suheng.structure.gson;

public class ByteUtil {
    public static final int INT_DATA_BYTE_LEN = 4;
    private static final int CHAR_DATA_BYTE_LEN = 2;

    public static char byteToChar(byte b) {
        return (char) b;
    }

    /*public static byte[] charToByteArray(char ch) {
        byte[] array = new byte[CHAR_DATA_BYTE_LEN];
        array[1] = (byte) (ch >> 8);
        array[0] = (byte) ch;
        return array;
    }*/

    public static byte[] intToByteArray(int integer) {
        byte[] array = new byte[INT_DATA_BYTE_LEN];
        array[3] = (byte) (integer >> 24);
        array[2] = (byte) (integer >> 16);
        array[1] = (byte) (integer >> 8);
        array[0] = (byte) integer;
        return array;
    }

    public static int byteArrayToInt(byte[] array) {
        int int1 = array[0] & 0xff;
        int int2 = (array[1] & 0xff) << 8;
        int int3 = (array[2] & 0xff) << 16;
        int int4 = (array[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    public static String byteArrayToString(byte[] array) {
        return new String(array, 0, array.length);
    }

    public static String byteArrayToHex(byte[] array) {
        if (array == null) {
            return "";
        }

        String hex;
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : array) {
            hex = Integer.toHexString(b & 0xFF);
            stringBuilder.append(((hex.length() == 1) ? "0" + hex : hex).toUpperCase()).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}