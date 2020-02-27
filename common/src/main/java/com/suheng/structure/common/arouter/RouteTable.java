package com.suheng.structure.common.arouter;

public class RouteTable {//命名规范：/模块/组件(aty、frg等)/业务
    //#common module
    public static final String COMMON_PROVIDER_PREFS_MANAGER = "/common/provider/prefs_manager";
    public static final String COMMON_PROVIDER_REQUEST_MANAGER = "/common/provider/request_manager";

    //#module1 module
    public static final String MODULE1_ATY_MODULE1_MAIN = "/module1/aty/module1_main";
    public static final String MODULE1_PROVIDER_MODULE1_CONFIG = "/module1/provider/module1_config";

    //#module2 module
    public static final String MODULE2_ATY_MODULE2_MAIN = "/module2/aty/module2_main";

    //#module3 module
    public static final String MODULE3_ATY_MVP_LOGIN = "/module3/aty/mvp_login";
    public static final String MODULE3_ATY_MVC_LOGIN = "/module3/aty/mvc_login";
    public static final String MODULE3_ATY_MODULE3_MAIN = "/module3/aty/module3_main";
}
