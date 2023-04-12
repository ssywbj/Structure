package com.suheng.structure.gson;

import androidx.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(Parameterized.class)
public class SpiralMatrixTest {

    private int[][] matrix;

    /*public SpiralMatrixTest(int[][] matrix) {
        this.matrix = matrix;
    }

    @NonNull
    @Parameterized.Parameters
    public static Collection<int[][]> parameters() {
        *//*return Arrays.asList(new int[][][]{
                {{1, 2, 3, 4}, {12, 13, 14, 5}, {11, 16, 15, 6}, {10, 9, 8, 7}},
                {{1, 2, 3}, {10, 11, 4}, {9, 12, 5}, {8, 7, 6}},
                {{1, 2, 3, 4}, {10, 11, 12, 5}, {9, 8, 7, 6}},
                {{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}},
                {{1, 2}, {10, 3}, {9, 4}, {8, 5}, {7, 6}},
                {{1, 2, 3, 4, 5}, {10, 9, 8, 7, 6}}
        });*//*

        return Arrays.asList(new int[][]{{1, 2, 3, 4}, {12, 13, 14, 5}, {11, 16, 15, 6}, {10, 9, 8, 7}},
                new int[][]{{1, 2, 3}, {10, 11, 4}, {9, 12, 5}, {8, 7, 6}},
                new int[][]{{1, 2, 3, 4}, {10, 11, 12, 5}, {9, 8, 7, 6}},
                new int[][]{{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}},
                new int[][]{{1, 2}, {10, 3}, {9, 4}, {8, 5}, {7, 6}},
                new int[][]{{1, 2, 3, 4, 5}, {10, 9, 8, 7, 6}}
        );
    }*/

    @Before
    public void before() {
        System.out.println("-------------------SpiralMatrix开始测试-------------------");
        this.matrix = new int[][]{{1, 2, 3, 4, 5}, {10, 9, 8, 7, 6}};
    }

    @After
    public void after() {
        System.out.println("-------------------SpiralMatrix结束测试-------------------");
    }

    @Test
    public void testSpiralOrder() {
        List<Integer> list = this.spiralOrder(matrix);
        System.out.println(list.toString());
    }

    public List<Integer> spiralOrder(@NonNull int[][] matrix) {
        final int rows = matrix.length, columns = matrix[0].length;
        System.out.println("matrix rows: " + rows + ", columns: " + columns);
        List<Integer> list = new ArrayList<>();
        this.splitMatrix(matrix, 0, columns - 1, 0, rows - 1, list);
        return list;
    }

    private void splitMatrix(int[][] matrix, int leftIndex, int rightIndex, int topIndex, int bottomIndex, List<Integer> list) {
        if (leftIndex > rightIndex || topIndex > bottomIndex) {
            return;
        }

        for (int i = leftIndex; i <= rightIndex; i++) { //读取顶部数字
            list.add(matrix[leftIndex][i]);
        }

        for (int i = topIndex + 1; i <= bottomIndex; i++) { //读取右边数字
            list.add(matrix[i][rightIndex]);
        }

        if (topIndex < bottomIndex) {
            for (int i = rightIndex - 1; i >= leftIndex; i--) { //读取底部数字
                list.add(matrix[bottomIndex][i]);
            }
        }

        for (int i = bottomIndex - 1; i >= topIndex + 1; i--) { //读取左边数字
            list.add(matrix[i][leftIndex]);
        }

        splitMatrix(matrix, leftIndex + 1, rightIndex - 1, topIndex + 1, bottomIndex - 1, list);
    }


    public int[] splitMatrix(int[][] matrix) {
        final int rows = matrix.length, columns = matrix[0].length;
        System.out.println("matrix rows: " + rows + ", columns: " + columns);
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
    private void splitMatrix(int[][] matrix, int leftIndex, int rightIndex, int topIndex, int bottomIndex, int[] dst, int dstIndex) {
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