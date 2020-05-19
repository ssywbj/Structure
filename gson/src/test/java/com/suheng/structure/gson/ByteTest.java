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
        char ch = 0x31E3;//字符型与整型可互转，汉字Unicode编码范围：https://www.qqxiuzi.cn/zh/hanzi-unicode-bianma.php
        //charArray = ByteUtil.intToByteArray(ch);
        //int ch = 65535+65536;//字符型与整型可互转，Java一个汉字占几个字节：https://www.cnblogs.com/duguxiaobiao/p/9128817.html
        System.out.println("charArray: " + ByteUtil.byteArrayToHex(charArray) + ", number: " + ByteUtil.byteArrayToInt(charArray)
                + ", ch: " + ch + "， charArray: " + ByteUtil.byteArrayToHex(ByteUtil.intToByteArray(ch)));

        //toBinaryString：用二进制表示数；bitCount：二进制表示中1的个数。
        System.out.println("binary: " + Integer.toBinaryString(9) + ", " + Integer.bitCount(9));

        int i = 257;
        int j = i << 3;//<<：左移运算符，<<1相当于乘以2，<<2相当于乘以4，以此类推
        int k = i >> 2;//>>：右移运算符，>>1相当于除以2，>>2相当于除以4，以此类推
        System.out.println("i: " + i + ", j: " + j + ", k: " + k + "\n" + "i binary: " + Integer.toBinaryString(i)
                + ", j binary: " + Integer.toBinaryString(j) + ", k binary: " + Integer.toBinaryString(k));

        //当将一个int型强制类型转换为byte型的时候，最高的三个字节会被砍掉，只留下最低的8位赋值给byte型。
        byte a = (byte) i;//1 2 4 8 16 32 64
        byte d = (byte) j;
        System.out.println("a: " + a + ", a binary: " + Integer.toBinaryString(a)
                + ", d: " + d + ", d binary: " + Integer.toBinaryString(d));

        //https://blog.csdn.net/chenchao2017/article/details/79733278?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-6.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-6.nonecase
        //在计算机中数值是以该数的补码形式存在的.https://blog.csdn.net/afsvsv/article/details/94553228
        //原码：最高位表示符号位，"1"表示负号，"0"表示正号，其它位存放该数的二进制的绝对值，是最简单的机器数表示法。如10000010，最高位是1表示这是一个负数，
        //其它位是0000010，值为2，所以10000010表示的是-2；同理00000010表示的是2。原码最大的问题就在于一个数加上他的相反数不等于零，如10000010+0000010是
        //是不等于0的。

        //反码：正数的反码等于原码；负数的反码等于原码除符号位外其它位按位取反，如：-2(10000010)的反码为11111101。

        //补码：正数的补码等于原码，负数的补码等于反码+1，如：-2(10000010)的补码为11111110。或负数的补码等于原码自低位向高位，尾数的第一个‘1’及其右边的‘0’保持不变，左边的各位按位取反，符号位不变。
    }

    private byte[] makeCmd() {
        byte[] data;

        int integer = 770;
        byte[] array = ByteUtil.intToByteArray(integer);
        System.out.println(integer + " byte hex: " + ByteUtil.byteArrayToHex(array));

        String text = "韦帮杰";
        System.out.println(text + " byte hex: " + ByteUtil.byteArrayToHex(text.getBytes()));

        array = new byte[ByteUtil.INT_DATA_BYTE_LEN];
        array[3] = 0;
        array[2] = 0;
        array[1] = TAIL;
        array[0] = HEAD;
        System.out.println("test normal data have head or tail hex: " + ByteUtil.byteArrayToHex(array)
                + ", value: " + ByteUtil.byteArrayToInt(array));

        //头尾各占1字节、命令和类型各1字节、包类型1字节、长度4字节、错误码1字节、校验和4字节，共14字节
        byte[] buffer = new byte[maxBuffer];

        int offset = 0;
        buffer[offset++] = HEAD;
        byte cmd = 0x31;
        buffer[offset++] = cmd;

        data = text.getBytes();
        byte[] dataLen = ByteUtil.intToByteArray(data.length);
        System.arraycopy(dataLen, 0, buffer, offset, dataLen.length);
        offset += dataLen.length;
        System.arraycopy(data, 0, buffer, offset, data.length);
        offset += data.length;

        data = "adgagagagadgakaekgla;mgdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddakdmgamdadsfmlkgfssafafadfafadfafadfafdadfafsmfgmgadfafakflafalfalfa".getBytes();
        dataLen = ByteUtil.intToByteArray(data.length);
        System.arraycopy(dataLen, 0, buffer, offset, dataLen.length);
        offset += dataLen.length;
        System.arraycopy(data, 0, buffer, offset, data.length);
        offset += data.length;

        buffer[offset++] = TAIL;

        byte[] protocol = new byte[offset];
        System.arraycopy(buffer, 0, protocol, 0, protocol.length);

        //byte[] dataLen = int2Bytes(data.length);
        System.out.println("protocol send= " + new String(protocol) + ", dataLen:" + ByteUtil.byteArrayToInt(dataLen));
        return protocol;
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
        if ((protocol[0] != HEAD) && (protocol[protocol.length - 1] != TAIL)) {
            return;
        }
        System.out.println("protocol receive: " + new String(protocol));

        int offset = 1;
        byte cmd = protocol[offset++];
        if (cmd == 0x31) {
            byte[] dataLen = new byte[4];
            System.arraycopy(protocol, offset, dataLen, 0, dataLen.length);
            offset += dataLen.length;
            int len = ByteUtil.byteArrayToInt(dataLen);
            byte[] data = new byte[len];
            System.arraycopy(protocol, offset, data, 0, data.length);
            offset += data.length;
            String name = new String(data);
            System.out.println("dataLen = " + len + ", name: " + name);

            dataLen = new byte[4];
            System.arraycopy(protocol, offset, dataLen, 0, dataLen.length);
            offset += dataLen.length;
            len = ByteUtil.byteArrayToInt(dataLen);
            data = new byte[len];
            System.arraycopy(protocol, offset, data, 0, data.length);
            offset += data.length;
            name = new String(data);
            System.out.println("dataLen = " + len + ", name: " + name);
        }
    }

}