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
 * <p>
 * https://blog.csdn.net/weixin_51182518/article/details/114578329
 * https://blog.csdn.net/weixin_45627369/article/details/124074552
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
        List<Integer> list = this.spiralOrder(matrix);
        System.out.println(list.toString());

        int[][] matrix2 = {{1, 2, 3}, {10, 11, 4}, {9, 12, 5}, {8, 7, 6}};
        list = this.spiralOrder(matrix2);
        System.out.println(list.toString());

        int[][] matrix3 = {{1, 2, 3, 4}, {10, 11, 12, 5}, {9, 8, 7, 6}};
        list = this.spiralOrder(matrix3);
        System.out.println(list.toString());

        int[][] matrix4 = {{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}};
        list = this.spiralOrder(matrix4);
        System.out.println(list.toString());

        int[][] matrix5 = {{1, 2}, {10, 3}, {9, 4}, {8, 5}, {7, 6}};
        list = this.spiralOrder(matrix5);
        System.out.println(list.toString());

        int[][] matrix6 = {{1, 2, 3, 4, 5}, {10, 9, 8, 7, 6}};
        list = this.spiralOrder(matrix6);
        System.out.println(list.toString());

        int[][] matrix7 = {{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}};
        list = this.spiralOrder(matrix7);
        System.out.println(list.toString());

        int[][] matrix8 = {{1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}};
        list = this.spiralOrder(matrix8);
        System.out.println(list.toString());

        /*System.out.println("----111111111111111111111111111111111111111111111111111111111111");

        int[][] result = this.buildSpiralMatrix(7);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }

        System.out.println("----22222222222222222222222222222222222222222222222222222222222");

        result = this.buildSpiralMatrix(4);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }

        System.out.println("----3333333333333333333333333333333333333333333333333333333333");

        result = this.buildSpiralMatrix(5, 11);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }*/
    }

    //https://leetcode.cn/problems/spiral-matrix/
    public List<Integer> spiralOrder(int[][] matrix) {
        final int rows = matrix.length, columns = matrix[0].length;
        System.out.println("matrix rows: " + rows + ", columns: " + columns);
        List<Integer> list = new ArrayList<>();
        this.splitMatrix(matrix, 0, columns - 1, 0, rows - 1, list);
        return list;
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
     * @param list        保存读取结果
     */
    private void splitMatrix(int[][] matrix, int leftIndex, int rightIndex, int topIndex, int bottomIndex, List<Integer> list) {
        if (leftIndex > rightIndex || topIndex > bottomIndex) {
            return;
        }

        for (int i = leftIndex; i <= rightIndex; i++) { //读取顶部：从左到右读取
            list.add(matrix[topIndex][i]);
        }

        for (int i = topIndex + 1; i <= bottomIndex; i++) { //读取右边：从顶到底读取，+1指针下移一位，因为读取顶部的时候把最右侧的一个给读取了，不加1会重复读取
            list.add(matrix[i][rightIndex]);
        }

        if (topIndex < bottomIndex) { //小于，表示剩下的行数是大于1的，正常读取；大于或等于，表示剩下的行数就只有1行，当只剩下一行读取顶部就已经完全读取了，不需要重复读取
            for (int i = rightIndex - 1; i >= leftIndex; i--) { //读取底部：从右到左读取，-1指针左移一位，因为读取右边的时候把最底部的一个给读取了，不减1会重复读取
                list.add(matrix[bottomIndex][i]);
            }
        }

        if (leftIndex < rightIndex) { //小于，表示剩下的列数是大于1的，正常读取；大于或等于，表示剩下的列数就只有1列，当只剩下一列读取右边就已经完全读取了，不需要重复读取
            //读取左边：从底到顶读取，-1指针底部上移一位，因为读取底部的时候把最左边的一个给读取了，不减1会重复读取；
            //+1指针顶部下移一位，因为读取顶部的时候把最左边的一个给读取了，不加1会重复读取。
            for (int i = bottomIndex - 1; i >= topIndex + 1; i--) {
                list.add(matrix[i][leftIndex]);
            }
        }

        splitMatrix(matrix, leftIndex + 1, rightIndex - 1, topIndex + 1, bottomIndex - 1, list);
    }

    public int[][] buildSpiralMatrix(final int len) {
        return this.buildSpiralMatrix(len, 1);
    }

    public int[][] buildSpiralMatrix(final int len, final int startValue) {
        int[][] matrix = new int[len][len];
        this.buildSpiralMatrix(matrix, 0, len, startValue);
        return matrix;
    }

    private void buildSpiralMatrix(final int[][] matrix, int left, int right, int startValue) {
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

        buildSpiralMatrix(matrix, left, right, startValue);
    }

    @Test
    public void testMerge2() {
        //int[] arr = {6, 5, 3, 1};
        int[] arr = {6, 5, 3, 1, 2, 7, 4, 0, 8, 9, 10, 11, 12, 13, 14, 15};
        this.merge(arr);
        for (int i : arr) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private void merge(int[] arr) {
        int[] temp = new int[arr.length];
        this.divide(arr, 0, arr.length - 1, temp);
    }

    //leftIndex: 0, midIndex: 0, rightIndex: 1
    //leftIndex: 2, midIndex: 2, rightIndex: 3
    //leftIndex: 0, midIndex: 1, rightIndex: 3
    //leftIndex: 4, midIndex: 4, rightIndex: 5
    //leftIndex: 6, midIndex: 6, rightIndex: 7
    //leftIndex: 4, midIndex: 5, rightIndex: 7
    //leftIndex: 0, midIndex: 3, rightIndex: 7
    //https://zh.wikipedia.org/wiki/%E5%BD%92%E5%B9%B6%E6%8E%92%E5%BA%8F
    //https://www.cnblogs.com/onepixel/articles/7674659.html
    private void divide(int[] arr, int leftIndex, int rightIndex, int[] temp) {
        if (leftIndex < rightIndex) {
            int midIndex = leftIndex + (rightIndex - leftIndex) / 2;
            divide(arr, leftIndex, midIndex, temp);
            divide(arr, midIndex + 1, rightIndex, temp);
            System.out.println("leftIndex: " + leftIndex + ", midIndex: " + midIndex + ", rightIndex: " + rightIndex);
            this.merge(arr, leftIndex, midIndex, rightIndex, temp);
        }
    }

    private void merge(int[] arr, int leftIndex, int midIndex, int rightIndex, int[] temp) {
        int i = leftIndex, j = midIndex + 1, indexTemp = 0;
        while (i <= midIndex && j <= rightIndex) {
            if (arr[i] <= arr[j]) {
                temp[indexTemp++] = arr[i];
                i++;
            } else {
                temp[indexTemp++] = arr[j];
                j++;
            }
        }

        for (; i <= midIndex; i++) {
            temp[indexTemp++] = arr[i];
        }

        for (; j <= rightIndex; j++) {
            temp[indexTemp++] = arr[j];
        }

        System.arraycopy(temp, 0, arr, leftIndex, indexTemp);
    }

    @Test
    public void testMerge() {
        int[] arr1 = {1, 2, 3, 0, 0, 0};
        int[] aar2 = {2, 5, 6};
        this.merge(arr1, 3, aar2, aar2.length);
        for (int i : arr1) {
            System.out.print(i + " ");
        }
        System.out.println();

        arr1 = new int[]{1};
        this.merge(arr1, 1, null, 0);
        for (int i : arr1) {
            System.out.print(i + " ");
        }
        System.out.println();

        arr1 = new int[]{0};
        aar2 = new int[]{1};
        this.merge(arr1, 0, aar2, aar2.length);
        for (int i : arr1) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    //https://leetcode.cn/problems/merge-sorted-array/submissions/
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (nums1 == null || nums1.length == 0) {
            return;
        }

        if (nums2 == null || n == 0) {
            return;
        }

        int[] temp = new int[m];
        System.arraycopy(nums1, 0, temp, 0, temp.length);

        int indexTemp = 0, indexArr2 = 0, indexArr1 = 0;
        while (indexTemp < temp.length && indexArr2 < nums2.length) {
            if (temp[indexTemp] <= nums2[indexArr2]) {
                nums1[indexArr1] = temp[indexTemp++];
            } else {
                nums1[indexArr1] = nums2[indexArr2++];
            }
            indexArr1++;
        }

        for (; indexTemp < temp.length; indexTemp++) {
            nums1[indexArr1++] = temp[indexTemp];
        }

        for (; indexArr2 < nums2.length; indexArr2++) {
            nums1[indexArr1++] = nums2[indexArr2];
        }
    }

    @Test
    public void testHeapSort() {
        //int[] arr = {16, 14, 10, 8, 7, 9, 3, 2, 4, 1};

        int[] arr = {16, 4, 10, 14, 7, 9, 3, 2, 8, 1};
        //this.maxHeap(arr, arr.length, 1);
        System.out.println(Arrays.toString(arr));

        arr = new int[]{4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
        //this.buildMaxHeap(arr);
        System.out.println(Arrays.toString(arr));

        arr = new int[]{4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
        this.heapSort(arr);
        System.out.println(Arrays.toString(arr));

        arr = new int[]{4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
        mHeapSize = arr.length;
        this.heapSort2(arr);
        System.out.println(Arrays.toString(arr));
    }

    private int leftChildIndex(int index) {
        return index * 2 + 1;
        //return index << 1 + 1;
    }

    private int rightChildIndex(int index) {
        return index * 2 + 2;
        //return index << 1 + 2;
    }

    private void maxHeap(int[] arr, final int heapSize, final int index) {
        final int leftChildIndex = this.leftChildIndex(index);
        final int rightChildIndex = this.rightChildIndex(index);
        int largestIndex = index;
        //System.err.println("leftChildIndex < heapSize: " + (leftChildIndex < heapSize) + ", leftChildIndex: " + leftChildIndex);
        if (leftChildIndex < heapSize && arr[leftChildIndex] > arr[index]) {
            largestIndex = leftChildIndex;
        }
        //System.err.println("rightChildIndex < heapSize: " + (rightChildIndex < heapSize) + ", rightChildIndex: " + rightChildIndex);
        if (rightChildIndex < heapSize && arr[rightChildIndex] > arr[largestIndex]) {
            largestIndex = rightChildIndex;
        }
        if (largestIndex != index) {
            int temp = arr[index];
            arr[index] = arr[largestIndex];
            arr[largestIndex] = temp;
            this.maxHeap(arr, heapSize, largestIndex);
        }
    }

    private void buildMaxHeap(int[] arr) {
        final int heapSize = arr.length;
        for (int i = heapSize / 2 - 1; i >= 0; i--) {
            this.maxHeap(arr, heapSize, i);
        }
    }

    public void heapSort(int[] arr) {
        this.buildMaxHeap(arr);
        for (int i = arr.length - 1; i >= 1; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            System.out.println("heapSize: " + i + ", leftChildIndex: " + this.leftChildIndex(0) + ", rightChildIndex: " + this.rightChildIndex(0));
            this.maxHeap(arr, i, 0);
        }
    }

    private int mHeapSize;

    private void maxHeap(int[] arr, int index) {
        final int leftChildIndex = this.leftChildIndex(index);
        final int rightChildIndex = this.rightChildIndex(index);
        int largestIndex = index;
        if (leftChildIndex < mHeapSize && arr[leftChildIndex] > arr[index]) {
            largestIndex = leftChildIndex;
        }
        if (rightChildIndex < mHeapSize && arr[rightChildIndex] > arr[largestIndex]) {
            largestIndex = rightChildIndex;
        }
        if (largestIndex != index) {
            int temp = arr[index];
            arr[index] = arr[largestIndex];
            arr[largestIndex] = temp;
            this.maxHeap(arr, largestIndex);
        }
    }

    private void buildMaxHeap2(int[] arr) {
        for (int i = mHeapSize / 2 - 1; i >= 0; i--) {
            this.maxHeap(arr, i);
        }
    }

    public void heapSort2(int[] arr) {
        this.buildMaxHeap2(arr);
        for (int i = arr.length - 1; i >= 1; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            mHeapSize--;
            this.maxHeap(arr, 0);
        }
    }

    @Test
    public void testQuickSort() {
        //int[] arr = {4, 10, 14, 16, 7, 9, 3, 2, 8, 1};
        //int[] arr = {4, 1, 5, 7, 2, 3};
        int[] arr = {2, 8, 7, 1, 3, 5, 6, 4};
        //this.partition(arr, 0, arr.length - 1);
        //this.quickSort(arr);
        this.randomizedQuickSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public void quickSort(int[] arr) {
        this.quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int partition = this.partition(arr, leftIndex, rightIndex);
            this.quickSort(arr, leftIndex, partition - 1);
            this.quickSort(arr, partition + 1, rightIndex);
        }
    }

    private int partition(int[] arr, int leftIndex, int rightIndex) {
        final int pivot = arr[rightIndex];
        int partition = leftIndex;
        for (int index = leftIndex; index <= rightIndex - 1; index++) {
            if (arr[index] < pivot) {
                int temp = arr[partition];
                arr[partition] = arr[index];
                arr[index] = temp;
                partition++;
                //System.out.println(Arrays.toString(arr));
            }
        }

        int temp = arr[partition];
        arr[partition] = pivot;
        arr[rightIndex] = temp;
        System.err.println(Arrays.toString(arr) + ", partition: " + partition);

        return partition;
    }

    public void randomizedQuickSort(int[] arr) {
        this.randomizedQuickSort(arr, 0, arr.length - 1);
    }

    private void randomizedQuickSort(int[] arr, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int partition = this.randomizedPartition(arr, leftIndex, rightIndex);
            this.randomizedQuickSort(arr, leftIndex, partition - 1);
            this.randomizedQuickSort(arr, partition + 1, rightIndex);
        }
    }

    private int randomizedPartition(int[] arr, int leftIndex, int rightIndex) {
        //Random random = new Random();
        //int ran = random.nextInt(rightIndex - leftIndex + 1) + leftIndex;
        int ran = (int) (Math.random() * (rightIndex - leftIndex + 1)) + leftIndex;
        int temp = arr[ran];
        arr[ran] = arr[rightIndex];
        arr[rightIndex] = temp;
        return this.partition(arr, leftIndex, rightIndex);
    }

    @Test
    public void testBubbleSort() {
        int[] arr = {4, 10, 14, 16, 7, 9, 3, 2, 8, 1/*, -1, 0, 17, 11*/};
        this.bubbleSort(arr);
        System.err.println(Arrays.toString(arr));

        this.testBubbleSortRec();
    }

    public void bubbleSort(int[] arr) {
        int sortedSize;
        for (sortedSize = 0; sortedSize <= arr.length - 2; sortedSize++) {
            for (int i = 0; i < arr.length - 1 - sortedSize; i++) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                }
            }
            System.out.println(Arrays.toString(arr) + ", sortedSize: " + sortedSize);
        }

        System.out.println("----sortedSize: " + sortedSize);
    }

    @Test
    public void testBubbleSortRec() {
        int[] arr = {4, 10, 14, 16, 7, 9, 3, 2, 8, 1/*, -1, 0, 17, 11*/};
        this.bubbleSortRec(arr);
        System.err.println(Arrays.toString(arr));
    }

    public void bubbleSortRec(int[] arr) {
        this.bubbleSort(arr, 0);
    }

    private void bubbleSort(int[] arr, int sortedSize) {
        if (sortedSize >= arr.length - 1) {
            System.out.println("rec ----sortedSize: " + sortedSize);
            return;
        }

        for (int i = 0; i < arr.length - 1 - sortedSize; i++) {
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
            }
        }

        System.out.println(Arrays.toString(arr) + ", rec sortedSize: " + sortedSize);
        bubbleSort(arr, sortedSize + 1);
    }

    @Test
    public void testCountingSort() {
        int[] arr = {6, 5, 3, 2, 7, 3, 2, 3, 4};
        //int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] countingSort = this.countingSort(arr);
        System.out.println("countingSort: " + Arrays.toString(countingSort));
        this.countingSort2(arr);
        System.out.println("countingSort2, arr: " + Arrays.toString(arr));
    }

    public int[] countingSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return arr;
        }

        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (max < arr[i]) {
                max = arr[i];
            }
        }

        int[] count = new int[max + 1];
        for (int value : arr) {
            count[value]++;
        }
        System.out.println(Arrays.toString(count));

        for (int i = 1; i < count.length; i++) {
            count[i] = count[i] + count[i - 1];
        }
        System.out.println(Arrays.toString(count));

        int[] dst = new int[arr.length];
        for (int j = arr.length - 1; j >= 0; j--) {
            dst[count[arr[j]] - 1] = arr[j];
            count[arr[j]]--;
        }

        return dst;
    }


    public void countingSort2(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        int max = arr[0], min = max;
        for (int i = 1; i < arr.length; i++) {
            int a = arr[i];
            if (max < a) {
                max = a;
            }
            if (min > a) {
                min = a;
            }
        }
        System.out.println("countingSort2, min: " + min + ", max: " + max);

        int k = max - min + 1;
        int[] count = new int[k];
        for (int value : arr) {
            count[value - min]++;
        }
        System.out.println(Arrays.toString(count));

        int dstIndex = 0;
        for (int index = 0; index < count.length; index++) {
            while (count[index] > 0) {
                arr[dstIndex++] = index + min;
                count[index]--;
            }
        }
    }

}