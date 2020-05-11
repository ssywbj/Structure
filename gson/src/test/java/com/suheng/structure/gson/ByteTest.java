package com.suheng.structure.gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ByteTest {
    private static final byte HEAD = 0x02;
    private static final byte TAIL = 0x03;

    @Before
    public void before() {
        System.out.println("-------------------开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------结束测试-------------------");
    }

    @Test
    public void testBasic() {
        byte b = 48;
        byte[] data = new byte[2];
        data[0] = b;
        data[1] = 33;
        System.out.println("head: " + HEAD + ", tail: " + TAIL + ", b: " + b + ", charb: " + ByteUtil.byteToChar(b)
                + ", data: " + ByteUtil.byteArrayToString(data));

        char c = '龟';
        byte[] charArray = ByteUtil.intToByteArray(c);
        //char ch = 0x2EF3;//字符型与整型可互转
        char ch = 0x31E3;//字符型与整型可互转
        //charArray = ByteUtil.intToByteArray(ch);
        //int ch = 65535+65536;//字符型与整型可互转
        System.out.println("charArray: " + ByteUtil.byteArrayToHex(charArray) + ", number: " + ByteUtil.byteArrayToInt(charArray)
                + ", ch: " + ch + "， charArray: " + ByteUtil.byteArrayToHex(ByteUtil.intToByteArray(ch)));
    }

    private byte[] makeCmd() {
        byte b = 48;
        byte[] data = new byte[2];
        data[0] = b;
        data[1] = 33;
        System.out.println("head: " + HEAD + ", tail = " + TAIL + ", b = " + b + ", a = " + new String(data));

        byte[] bytes = int2Bytes(203232);
        for (byte aByte : bytes) {
            System.out.println("aByte: " + aByte);
        }

        byte[] d = "韦帮杰".getBytes();
        for (byte b1 : d) {
            System.out.println("b1 = " + b1);
        }

        //头尾各占1字节、命令和类型各1字节、包类型1字节、长度4字节、错误码1字节、校验和4字节，共14字节
        byte[] buffer = new byte[maxBuffer];

        int offset = 0;
        buffer[offset++] = HEAD;
        byte cmd = 0x31;
        buffer[offset++] = cmd;

        data = "韦帮杰".getBytes();
        byte[] dataLen = int2Bytes(data.length);
        System.arraycopy(dataLen, 0, buffer, offset, dataLen.length);
        offset += dataLen.length;
        System.arraycopy(data, 0, buffer, offset, data.length);
        offset += data.length;

        data = "adgagagagadgakaekgla;mgdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddakdmgamdadsfmlkgfssafafadfafadfafadfafdadfafsmfgmgadfafakflafalfalfa".getBytes();
        dataLen = int2Bytes(data.length);
        System.arraycopy(dataLen, 0, buffer, offset, dataLen.length);
        offset += dataLen.length;
        System.arraycopy(data, 0, buffer, offset, data.length);
        offset += data.length;

        buffer[offset++] = TAIL;

        byte[] protocol = new byte[offset];
        System.arraycopy(buffer, 0, protocol, 0, protocol.length);

        //byte[] dataLen = int2Bytes(data.length);
        System.out.println("protocol send= " + new String(protocol) + ", dataLen:" + bytes2Int(dataLen));
        return protocol;
    }

    private byte[] int2Bytes(int integer) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (integer >> 24);
        bytes[2] = (byte) (integer >> 16);
        bytes[1] = (byte) (integer >> 8);
        bytes[0] = (byte) integer;
        return bytes;
    }

    private static int bytes2Int(byte[] bytes) {
        //如果不与0xff进行按位与操作，转换结果将出错，有兴趣的同学可以试一下。
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    private byte[] receive;
    private int offset = 0, maxBuffer = 1024;

    @Test
    public void testMakeCmd() {
        byte[] buffer = this.makeCmd();
        int index = 0;
        final int length = buffer.length;
        while (index < length) {
            if (buffer[index] == HEAD) {
                receive = new byte[maxBuffer];
                offset = 0;
            }

            receive[offset++] = buffer[index];

            if (buffer[index] == TAIL) {
                byte[] protocol = new byte[offset];
                System.arraycopy(receive, 0, protocol, 0, offset);
                parseProtocol(protocol);
            }

            index++;
        }
    }

    private void parseProtocol(byte[] protocol) {
        if (protocol == null || protocol.length < 2) {
            return;
        }
        if ((protocol[0] != this.HEAD) && (protocol[protocol.length - 1] != TAIL)) {
            return;
        }
        System.out.println("protocol receive: " + new String(protocol));

        int offset = 1;
        byte cmd = protocol[offset++];
        if (cmd == 0x31) {
            byte[] dataLen = new byte[4];
            System.arraycopy(protocol, offset, dataLen, 0, dataLen.length);
            offset += dataLen.length;
            int len = bytes2Int(dataLen);
            byte[] data = new byte[len];
            System.arraycopy(protocol, offset, data, 0, data.length);
            offset += data.length;
            String name = new String(data);
            System.out.println("dataLen = " + len + ", name: " + name);

            dataLen = new byte[4];
            System.arraycopy(protocol, offset, dataLen, 0, dataLen.length);
            offset += dataLen.length;
            len = bytes2Int(dataLen);
            data = new byte[len];
            System.arraycopy(protocol, offset, data, 0, data.length);
            offset += data.length;
            name = new String(data);
            System.out.println("dataLen = " + len + ", name: " + name);
        }
    }

}