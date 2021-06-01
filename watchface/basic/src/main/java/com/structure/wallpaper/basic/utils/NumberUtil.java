package com.structure.wallpaper.basic.utils;


public class NumberUtil {

    /**
     * 统计一个整数有多少位
     *
     * @param number 数值
     * @param shift  数的进制
     * @return 数的位数
     */
    public static int countUnits(int number, final int shift) {
        int count = 0;
        do {
            number /= shift;
            count++;
        } while (number != 0);

        return count;
    }

    public static int countUnits(int number) {
        return countUnits(number, 10);
    }

    /**
     * 获取一个整数位上的数字（高位在前，低位在后）
     *
     * @param number 数值
     * @param shift  数的进制
     * @return 数对应位上的数值
     */
    public static int[] obtainUnits(int number, final int shift) {
        final int len = countUnits(number, shift);
        final int[] units = new int[len];
        int count = 0;
        do {
            units[len - count - 1] = number % shift;
            number /= shift;
            count++;
        } while (number != 0);

        return units;
    }

    public static int[] obtainUnits(int number) {
        return obtainUnits(number, 10);
    }

}