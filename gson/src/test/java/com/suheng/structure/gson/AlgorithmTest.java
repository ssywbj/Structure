package com.suheng.structure.gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlgorithmTest {

    @Before
    public void before() {
        System.out.println("-------------------开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------结束测试-------------------");
    }

    @Test
    public void testCountUnits() {
        Assert.assertEquals(1, countUnits(0));
        Assert.assertEquals(1, countUnits(1));
        Assert.assertEquals(1, countUnits(9));
        Assert.assertEquals(2, countUnits(10));
        Assert.assertEquals(2, countUnits(99));
        Assert.assertEquals(3, countUnits(100));
        Assert.assertEquals(3, countUnits(101));
        Assert.assertEquals(3, countUnits(999));
        Assert.assertEquals(4, countUnits(1000));
        Assert.assertEquals(4, countUnits(2121));
        Assert.assertEquals(4, countUnits(19931));
        Assert.assertEquals(9, countUnits(576734534));
        Assert.assertEquals(10, countUnits(1132123142));
    }

    /**
     * 统计一个整数有多少位
     */
    public int countUnits(int number) {
        /*int count = 0;
        do {
            number /= 10;
            count++;
        } while (number != 0);

        return count;*/
        return countUnits(number, 10);
    }

    /**
     * 获取一个整数位上的数字
     */
    public int[] obtainUnits(int number) {
        final int len = countUnits(number);
        final int[] units = new int[len];
        int count = 0;
        do {
            units[len - count - 1] = number % 10;
            number /= 10;
            count++;
        } while (number != 0);

        return units;
    }

    @Test
    public void testObtainUnits() {
        //printArray(obtainUnits(0));
        Assert.assertArrayEquals(new int[]{0}, obtainUnits(0));
        //printArray(obtainUnits(1));
        Assert.assertArrayEquals(new int[]{1}, obtainUnits(1));
        //printArray(obtainUnits(9));
        Assert.assertArrayEquals(new int[]{9}, obtainUnits(9));
        //printArray(obtainUnits(10));
        Assert.assertArrayEquals(new int[]{1, 0}, obtainUnits(10));
        //printArray(obtainUnits(99));
        Assert.assertArrayEquals(new int[]{9, 9}, obtainUnits(99));
        //printArray(obtainUnits(104));
        Assert.assertArrayEquals(new int[]{1, 0, 4}, obtainUnits(104));
        //printArray(obtainUnits(993));
        Assert.assertArrayEquals(new int[]{9, 9, 3}, obtainUnits(993));
        //printArray(obtainUnits(1000));
        Assert.assertArrayEquals(new int[]{1, 0, 0, 0}, obtainUnits(1000));
        //printArray(obtainUnits(2131));
        Assert.assertArrayEquals(new int[]{2, 1, 3, 1}, obtainUnits(2131));
        //printArray(obtainUnits(19435));
        Assert.assertArrayEquals(new int[]{1, 9, 4, 3, 5}, obtainUnits(19435));
        //printArray(obtainUnits(512764538));
        Assert.assertArrayEquals(new int[]{5, 1, 2, 7, 6, 4, 5, 3, 8}, obtainUnits(512764538));
        //printArray(obtainUnits(1042687345));
        Assert.assertArrayEquals(new int[]{1, 0, 4, 2, 6, 8, 7, 3, 4, 5}, obtainUnits(1042687345));

        /*printArray(obtainBinary(0));
        printArray(obtainBinary(1));
        printArray(obtainBinary(2));
        printArray(obtainBinary(3));
        printArray(obtainBinary(4));
        printArray(obtainBinary(5));
        printArray(obtainBinary(6));
        printArray(obtainBinary(7));
        printArray(obtainBinary(8));
        printArray(obtainBinary(9));
        printArray(obtainBinary(10));
        printArray(obtainBinary(11));
        printArray(obtainBinary(12));
        printArray(obtainBinary(32));
        printArray(obtainBinary(33));
        printArray(obtainBinary(63));
        printArray(obtainBinary(64));
        printArray(obtainBinary(65));*/

        printArray(obtainHex(0));
        printArray(obtainHex(1));
        printArray(obtainHex(2));
        printArray(obtainHex(3));
        printArray(obtainHex(4));
        printArray(obtainHex(5));
        printArray(obtainHex(6));
        printArray(obtainHex(7));
        printArray(obtainHex(8));
        printArray(obtainHex(9));
        printArray(obtainHex(10));
        printArray(obtainHex(11));
        printArray(obtainHex(12));
        printArray(obtainHex(13));
        printArray(obtainHex(14));
        printArray(obtainHex(15));
        printArray(obtainHex(16));
        printArray(obtainHex(32));
        printArray(obtainHex(33));
        printArray(obtainHex(63));
        printArray(obtainHex(64));
        printArray(obtainHex(127));
        printArray(obtainHex(128));
        printArray(obtainHex(129));
        printArray(obtainHex(128));
        printArray(obtainHex(159));
        printArray(obtainHex(160));
        printArray(obtainHex(161));
        printArray(obtainHex(512));
        printArray(obtainHex(513));
    }

    private <T> void printArray(T[] array) {
        for (T t : array) {
            System.out.print(t);
        }
        System.out.println();
    }

    public int countBinary(int number) {
        int count = 0;
        do {
            number /= 2;
            count++;
        } while (number != 0);

        return count;
    }

    public int[] obtainBinary(int number) {
        System.out.println(number);
        final int len = countBinary(number);
        final int[] units = new int[len];
        int count = 0;
        do {
            units[len - count - 1] = number % 2;
            number /= 2;
            count++;
        } while (number != 0);

        return units;
    }

    public int countHex(int number) {
        int count = 0;
        do {
            number /= 16;
            count++;
        } while (number != 0);

        return count;
    }

    public Character[] obtainHex(int number) {
        System.out.println(number);
        final char[] hexUnits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final int len = countHex(number);
        final Character[] units = new Character[len];
        int count = 0;
        do {
            units[len - count - 1] = hexUnits[number % 16];
            number /= 16;
            count++;
        } while (number != 0);

        return units;
    }

    /**
     * 统计一个整数有多少位
     */
    public int countUnits(int number, int shift) {
        int count = 0;
        do {
            number /= shift;
            count++;
        } while (number != 0);

        return count;
    }

    @Test
    public void testLetterAdd() {
        /*printArray(letterAdd(0));
        printArray(letterAdd(1));
        printArray(letterAdd(2));
        printArray(letterAdd(25));
        printArray(letterAdd(26 * 1 + 0));
        printArray(letterAdd(26 * 1 + 1));
        printArray(letterAdd(26 * 25));
        printArray(letterAdd(26 * 25 + 25));
        printArray(letterAdd(26 * 26));*/

        /*final char a = 'a';
        System.out.println(a);
        final char b = a + 1;
        System.out.println(b);
        System.out.println((char) (a + 2));
        System.out.println((char) (a + 25));
        System.out.println((char) (a + 26));*/

        System.out.println(Integer.toHexString('庆'));

        try {
            byte[] bytes = "中".getBytes("utf-16");
            System.out.println(bytes.length);
            for (byte b : bytes) {
                System.out.print(Integer.toHexString(Byte.toUnsignedInt(b)) + " ");
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String emoji = "\u0001[微笑]\u0002";
        System.out.println(emoji.length());
        emoji = "\u0001";
        System.out.println(emoji.length());
        emoji = "[";
        System.out.println(emoji.length());
        emoji = "微";
        System.out.println(emoji.length());

        System.out.println("\uD83D\uDE0E" + ", " + "\uD83D\uDE0E".length());
    }

    public Character[] letterAdd(int i) {
        final char a = 'a';
        final int len = countUnits(i, 26);
        Character[] characters = new Character[len];
        int count = 0;
        do {
            characters[len - count - 1] = (char) (a + i % 26);
            i /= 26;
            count++;
        } while (i != 0);

        return characters;
    }

    //1.0.0.aa
    @Test
    public void testVersionAdd() {
        String version = "1.0.0.aa";
        if (version.matches("^[0-9]+\\.([0-9]\\.){2}[a-z]{2}$")) {
            String[] split = version.split("\\.");
            printArray(split);
        } else {
            System.out.println("版本号不符合规范");
        }
    }

}