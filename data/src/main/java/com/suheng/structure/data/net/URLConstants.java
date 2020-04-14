package com.suheng.structure.data.net;

public class URLConstants {
    //http://ip:8080/StructureServlet/Login?user_name=Wbj%E9%9F%A6%E5%B8%AE%E6%9D%B0&login_pwd=abc123
    private static final String URL_SERVER = "http://192.168.121.56:8080";
    private static final String URL_STRUCTURE_SERVER = URL_SERVER + "/StructureService";
    public static final String URL_LOGIN_REQUEST = URL_STRUCTURE_SERVER + "/Login";
    //public static final String URL_LOGIN_REQUEST = "https://www.baidu.com/";
    public static final String URL_GET_PICTURES_REQUEST = URL_STRUCTURE_SERVER + "/Pictures";
}
