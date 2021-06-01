package com.structure.wallpaper.basic.utils;

public class UnitsUtil {
    public static float centigradeToFahrenheit(float value){
        return m1(9 * value / 5f) + 32;
    }

    public static float m1(float fValue) {
        return (float) (Math.round(fValue * 10)) / 10;
    }
}
