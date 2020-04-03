package com.suheng.structure.module2.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetWorkUtil {

    /**
     * 获取本机IP地址
     */
    public static String getHostIp() {
        try {
            InetAddress inetAddress;
            Enumeration<InetAddress> addressEnumeration;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                addressEnumeration = networkInterfaces.nextElement().getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    inetAddress = addressEnumeration.nextElement();
                    if ((inetAddress instanceof Inet4Address) && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
