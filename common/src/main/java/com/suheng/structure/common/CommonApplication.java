package com.suheng.structure.common;

import android.app.Application;

import com.alibaba.android.arouter.BuildConfig;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.arouter.RouteTable;

public abstract class CommonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {//debug模式
            ARouter.openLog();//打印日志
            ARouter.openDebug();//调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险!)
        }
        ARouter.init(this);//尽可能早，推荐在Application中初始化

        ARouter.getInstance().build(RouteTable.data_data_manager).navigation();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
