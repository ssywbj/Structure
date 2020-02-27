package com.suheng.structure.module1.application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.CommonApplication;
import com.suheng.structure.common.arouter.RouteTable;

public class Module1App extends CommonApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().build(RouteTable.MODULE1_PROVIDER_MODULE1_CONFIG).navigation();
    }
}
