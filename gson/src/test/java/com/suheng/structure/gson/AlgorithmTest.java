package com.suheng.structure.gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int shift = 2;
        Assert.assertEquals(1, countUnits(1, shift));
        Assert.assertEquals(2, countUnits(2, shift));
        Assert.assertEquals(3, countUnits(4, shift));
        Assert.assertEquals(4, countUnits(8, shift));
        Assert.assertEquals(5, countUnits(16, shift));

        shift = 8;
        Assert.assertEquals(1, countUnits(1, shift));
        Assert.assertEquals(1, countUnits(2, shift));
        Assert.assertEquals(1, countUnits(4, shift));
        Assert.assertEquals(2, countUnits(8, shift));
        Assert.assertEquals(2, countUnits(16, shift));

        shift = 16;
        Assert.assertEquals(1, countUnits(1, shift));
        Assert.assertEquals(1, countUnits(2, shift));
        Assert.assertEquals(1, countUnits(4, shift));
        Assert.assertEquals(1, countUnits(8, shift));
        Assert.assertEquals(2, countUnits(16, shift));
    }

    @Test
    public void testCountUnitsTenShift() {
        Assert.assertEquals(1, countUnits(1));
        Assert.assertEquals(1, countUnits(2));
        Assert.assertEquals(1, countUnits(4));
        Assert.assertEquals(1, countUnits(8));
        Assert.assertEquals(2, countUnits(16));
        Assert.assertEquals(3, countUnits(100));
        Assert.assertEquals(3, countUnits(999));
        Assert.assertEquals(4, countUnits(1000));
        Assert.assertEquals(4, countUnits(2121));
        Assert.assertEquals(5, countUnits(19931));
        Assert.assertEquals(9, countUnits(576734534));
        Assert.assertEquals(10, countUnits(1132123142));
    }

    @Test
    public void testObtainUnits() {
        int shift = 2; //512 256 128 64 32 16 8 4 2 1
        Assert.assertArrayEquals(new int[]{0}, obtainUnits(0, shift));
        Assert.assertArrayEquals(new int[]{1, 0, 0, 1}, obtainUnits(9, shift));
        Assert.assertArrayEquals(new int[]{1, 1, 0, 0}, obtainUnits(12, shift));
        Assert.assertArrayEquals(new int[]{1, 1, 0, 0, 0, 1, 0}, obtainUnits(98, shift));
        Assert.assertArrayEquals(new int[]{1, 1, 1, 1, 1, 0, 0, 0, 0, 1}, obtainUnits(993, shift));
        Assert.assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1, 0, 1, 0, 1}, obtainUnits(1013, shift));

        shift = 8; //512 64 8 1
        Assert.assertArrayEquals(new int[]{0}, obtainUnits(0, shift));
        Assert.assertArrayEquals(new int[]{1, 1}, obtainUnits(9, shift));
        Assert.assertArrayEquals(new int[]{1, 4}, obtainUnits(12, shift));
        Assert.assertArrayEquals(new int[]{1, 4, 2}, obtainUnits(98, shift));
        Assert.assertArrayEquals(new int[]{1, 7, 4, 1}, obtainUnits(993, shift));
        Assert.assertArrayEquals(new int[]{1, 7, 6, 5}, obtainUnits(1013, shift));

        shift = 16; //4096 256 16 1
        Assert.assertArrayEquals(new int[]{0}, obtainUnits(0, shift));
        Assert.assertArrayEquals(new int[]{12}, obtainUnits(12, shift));
        Assert.assertArrayEquals(new int[]{6, 2}, obtainUnits(98, shift));
        Assert.assertArrayEquals(new int[]{3, 14, 1}, obtainUnits(993, shift));
        Assert.assertArrayEquals(new int[]{3, 14, 15}, obtainUnits(1007, shift));
        Assert.assertArrayEquals(new int[]{1, 3, 9, 13}, obtainUnits(5021, shift));
    }

    @Test
    public void testObtainUnitsTenShift() {
        Assert.assertArrayEquals(new int[]{0}, obtainUnits(0));
        Assert.assertArrayEquals(new int[]{9}, obtainUnits(9));
        Assert.assertArrayEquals(new int[]{1, 0}, obtainUnits(10));
        Assert.assertArrayEquals(new int[]{9, 8}, obtainUnits(98));
        Assert.assertArrayEquals(new int[]{9, 9, 3}, obtainUnits(993));
        Assert.assertArrayEquals(new int[]{1, 0, 0, 0}, obtainUnits(1000));
        Assert.assertArrayEquals(new int[]{2, 1, 3, 1}, obtainUnits(2131));
        Assert.assertArrayEquals(new int[]{1, 9, 4, 3, 5}, obtainUnits(19435));
        Assert.assertArrayEquals(new int[]{5, 1, 2, 7, 6, 4, 5, 3, 8}, obtainUnits(512764538));
        Assert.assertArrayEquals(new int[]{1, 0, 4, 2, 6, 8, 7, 3, 4, 5}, obtainUnits(1042687345));
    }

    @Test
    public void testObtainUnits2() {
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
            System.out.print(t + "\t");
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
     * 统计整数有多少位
     *
     * @param number 要统计的数字
     * @param shift  数的进制，如：二进制传2、八进制传8、十进制传10，以类类推
     * @return 数字number在shift进制下的位数
     */
    public static int countUnits(int number, final int shift) {
        int count = 0;
        do {
            number /= shift;
            count++;
        } while (number != 0);

        return count;
    }

    /**
     * 统计整数在十进制位下有多少位
     *
     * @param number 要统计的数字
     * @return 数字number在shift进制下的位数
     */
    public static int countUnits(final int number) {
        return countUnits(number, 10);
    }

    /**
     * 获取整数所有数位上的数字
     *
     * @param number 要获取的数字
     * @param shift  数的进制，如：二进制传2、八进制传8、十进制传10，以类类推
     * @return 一维整型数组：保存数字number在shift进制下的所有数位上的数字（高位在前，低位在后）
     */
    public static int[] obtainUnits(int number, final int shift) {
        final List<Integer> arrayList = new ArrayList<>();
        do {
            arrayList.add(number % shift);
            number /= shift;
        } while (number != 0);

        final int size = arrayList.size();
        final int[] units = new int[size];
        for (int i = 0; i < size; i++) {
            units[size - i - 1] = arrayList.get(i);
        }
        return units;
    }

    /**
     * 获取整数在十进制位下所有数位上的数字
     *
     * @param number 要获取的数字
     * @return 一维整型数组：保存数字number在十进制下的所有数位上的数字（高位在前，低位在后）
     */
    public int[] obtainUnits(final int number) {
        return obtainUnits(number, 10);
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
        /*String version = "1.0.0.aa";
        if (version.matches("^[0-9]+\\.([0-9]\\.){2}[a-z]{2}$")) {
            String[] split = version.split("\\.");
            printArray(split);
        } else {
            System.out.println("版本号不符合规范");
        }*/

        String gmt = "GMT-09:15";
        //String gmt = "GMT-12:00";
        //String gmt = "UTC-02:00";
        //String gmt = "UTC+10:00";
        if (gmt.matches("^(GMT|UTC|gmt|utc)[-+][0-9]{2}:[0-9]{2}$")) {
            String sign = gmt.substring(3, 4);
            float offset = Float.parseFloat(gmt.substring(4, 6));
            offset += Float.parseFloat(gmt.substring(7, 9)) / 60;
            System.out.println("sign: " + sign + ", offset: " + offset);
        } else {
            System.out.println("不符合规范");
        }

        String dimen = "48.34dip";
        //String dimen = "48.0dip";
        //String dimen = "48dp";
        //boolean matches = dimen.matches("^\\d+(\\.\\d+)(dp|dip)$");
        boolean matches = dimen.matches("^\\d+(\\.\\d+)?(dp|dip)$");
        System.out.println("matches: " + matches);
        if (matches) {
            String[] split = dimen.split("[a-z]+");
            for (String s : split) {
                System.out.println("s: " + s + ", " + s.length() + ", " + split.length);
            }
        }
    }

    @Test
    public void testSnakeMatrix() {
        int[][] matrix = {{1, 2, 3, 4}, {12, 13, 14, 5}, {11, 16, 15, 6}, {10, 9, 8, 7}};
        int[] dst = this.splitMatrix(matrix);
        System.out.println(Arrays.toString(dst));
        int[][] matrix2 = {{1, 2, 3}, {10, 11, 4}, {9, 12, 5}, {8, 7, 6}};
        dst = this.splitMatrix(matrix2);
        System.out.println(Arrays.toString(dst));
        int[][] matrix3 = {{1, 2, 3, 4}, {10, 11, 12, 5}, {9, 8, 7, 6}};
        dst = this.splitMatrix(matrix3);
        System.out.println(Arrays.toString(dst));
        int[][] matrix4 = {{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}};
        dst = this.splitMatrix(matrix4);
        System.out.println(Arrays.toString(dst));
        int[][] matrix5 = {{1, 2}, {10, 3}, {9, 4}, {8, 5}, {7, 6}};
        dst = this.splitMatrix(matrix5);
        System.out.println(Arrays.toString(dst));
        int[][] matrix6 = {{1, 2, 3, 4, 5}, {10, 9, 8, 7, 6}};
        dst = this.splitMatrix(matrix6);
        System.out.println(Arrays.toString(dst));

        /*System.out.println("----111111111111111111111111111111111111111111111111111111111111");

        int[][] result = this.buildMatrix(7);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
        System.out.println(Arrays.toString(this.splitMatrix(result)));

        System.out.println("----22222222222222222222222222222222222222222222222222222222222");

        result = this.buildMatrix(4);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
        System.out.println(Arrays.toString(this.splitMatrix(result)));

        System.out.println("----3333333333333333333333333333333333333333333333333333333333");

        result = this.buildMatrix(5, 11);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
        System.out.println(Arrays.toString(this.splitMatrix(result)));*/
    }

    public int[] splitMatrix(int[][] matrix) {
        final int rows = matrix.length, columns = matrix[0].length;
        System.out.println("matrix rows: " + rows + ", column: " + columns);
        int[] dst = new int[rows * columns];
        this.splitMatrix(matrix, 0, columns - 1, 0, rows - 1, dst, 0);
        return dst;
    }

    /**
     * 矩阵的螺旋递归
     * <p>
     * 示例：
     * <p>
     * <pre>
     * [1,  2,  3,  4,  5]
     * [16, 17, 18, 19, 6]
     * [15, 24, 25, 20, 7]
     * [14, 23, 22, 21, 8]
     * [13, 12, 11, 10, 9]
     *
     * 输出：1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
     * </pre>
     * <p>
     * 思路：先按矩阵的上边、右边、下边、左边顺时针读取一圈，然后左边加1（往后）右边减1（往前）上边加1（往下）下边减1（往上），缩小一圈递归读取。
     * 当left > right时，说明完全读取。
     *
     * @param matrix      用一个二维数组表示矩阵
     * @param leftIndex   矩阵左边索引
     * @param rightIndex  矩阵右边索引
     * @param topIndex    矩阵上边索引
     * @param bottomIndex 矩阵下边索引
     * @param dst         保存读取结果
     * @param dstIndex    dst索引
     */
    private void splitMatrix(int[][] matrix, int leftIndex, int rightIndex, int topIndex, int bottomIndex, final int[] dst, int dstIndex) {
        if (leftIndex > rightIndex || topIndex > bottomIndex) {
            return;
        }

        for (int i = leftIndex; i <= rightIndex; i++) { //读取顶部数字
            dst[dstIndex] = matrix[leftIndex][i];
            dstIndex++;
        }

        for (int i = topIndex + 1; i <= bottomIndex; i++) { //读取右边数字
            dst[dstIndex] = matrix[i][rightIndex];
            dstIndex++;
        }

        if (topIndex < bottomIndex) {
            for (int i = rightIndex - 1; i >= leftIndex; i--) { //读取底部数字
                dst[dstIndex] = matrix[bottomIndex][i];
                dstIndex++;
            }
        }

        for (int i = bottomIndex - 1; i >= topIndex + 1; i--) { //读取左边数字
            dst[dstIndex] = matrix[i][leftIndex];
            dstIndex++;
        }

        splitMatrix(matrix, leftIndex + 1, rightIndex - 1, topIndex + 1, bottomIndex - 1, dst, dstIndex);
    }

    public int[][] buildMatrix(final int len) {
        return this.buildMatrix(len, 1);
    }

    public int[][] buildMatrix(final int len, final int startValue) {
        int[][] matrix = new int[len][len];
        this.buildMatrix(matrix, 0, len, startValue);
        return matrix;
    }

    private void buildMatrix(final int[][] matrix, int left, int right, int startValue) {
        if (left > right) {
            return;
        }

        for (int i = left; i < right; i++) {
            matrix[left][i] = startValue;
            startValue++;
        }

        for (int i = left + 1; i < right; i++) {
            matrix[i][right - 1] = startValue;
            startValue++;
        }

        for (int i = right - 2; i >= left; i--) {
            matrix[right - 1][i] = startValue;
            startValue++;
        }

        for (int i = right - 2; i > left; i--) {
            matrix[i][left] = startValue;
            startValue++;
        }

        left += 1;
        right -= 1;

        buildMatrix(matrix, left, right, startValue);
    }

}