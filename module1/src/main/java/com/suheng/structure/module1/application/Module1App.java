package com.suheng.structure.module1.application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.DataApplication;
import com.suheng.structure.arouter.RouteTable;

public class Module1App extends DataApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().build(RouteTable.MODULE1_PROVIDER_MODULE1_CONFIG).navigation();
    }
}
