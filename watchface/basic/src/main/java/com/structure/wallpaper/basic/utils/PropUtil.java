package com.structure.wallpaper.basic.utils;

public class PropUtil {

    public static boolean isOversea() {
        /*String oversea = SystemProperties.get("ro.wiz.oversea");
        return "yes".equals(oversea);*/
        return true;
    }

    public static String getProduct() {
        //return SystemProperties.get("ro.wiz.product");
        return "ro.wiz.product";
    }

    public static boolean is008Product() {
        return getProduct().startsWith("2020008");
    }

}
