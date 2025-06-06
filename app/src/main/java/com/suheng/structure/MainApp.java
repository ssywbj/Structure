package com.suheng.structure;

import com.suheng.structure.common.CommonApplication;

public class MainApp extends CommonApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.MODULE1_IS_LIBRARY) {//module1的Application配置（如果有）
        }
    }
}
