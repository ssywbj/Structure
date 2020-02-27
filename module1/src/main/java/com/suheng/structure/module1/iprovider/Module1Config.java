package com.suheng.structure.module1.iprovider;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.suheng.structure.common.arouter.RouteTable;

@Route(path = RouteTable.MODULE1_PROVIDER_MODULE1_CONFIG)
public class Module1Config implements IProvider {

    @Override
    public void init(Context context) {//在应用的生命周期中，只会初始化一次
        Log.d(getClass().getSimpleName(), "init module1 app-->" + context);
    }
}

