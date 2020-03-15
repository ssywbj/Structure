package com.suheng.structure;

import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.arouter.RouteTable;

public class MainApp extends DataApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.MODULE1_IS_LIBRARY) {//module1的Application配置（如果有）
            ARouter.getInstance().build(RouteTable.MODULE1_PROVIDER_MODULE1_CONFIG).navigation();
        }
    }
}
