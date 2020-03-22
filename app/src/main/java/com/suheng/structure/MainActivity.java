package com.suheng.structure;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.arouter.RouteTable;
import com.suheng.structure.data.DataManager;
import com.suheng.structure.eventbus.LoginEvent;
import com.suheng.structure.receiver.InstallApkReceiver;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BasicActivity {

    @Autowired
    DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);

        findViewById(R.id.item_module1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataManager.isLoginSuccessful()) {
                    ARouter.getInstance().build(RouteTable.MODULE1_ATY_MODULE1_MAIN).navigation();
                } else {
                    ARouter.getInstance().build(RouteTable.MODULE3_ATY_MVP_LOGIN).navigation();
                }
            }
        });

        findViewById(R.id.item_module2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouteTable.MODULE2_ATY_MODULE2_MAIN)
                        .navigation(MainActivity.this,
                                new NavCallback() {
                                    @Override
                                    public void onFound(Postcard postcard) {
                                        super.onFound(postcard);
                                        Log.d(mTag, "onFound, " + postcard);
                                    }

                                    @Override
                                    public void onLost(Postcard postcard) {
                                        super.onLost(postcard);
                                        Log.d(mTag, "onLost, " + postcard);
                                    }

                                    @Override
                                    public void onArrival(Postcard postcard) {
                                        Log.d(mTag, "onArrival, " + postcard);
                                    }

                                    @Override
                                    public void onInterrupt(Postcard postcard) {
                                        super.onInterrupt(postcard);
                                        Log.d(mTag, "onInterrupt, " + postcard);
                                    }
                                });
            }
        });

        mInstallApkReceiver = new InstallApkReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        registerReceiver(mInstallApkReceiver, intentFilter);
    }

    private InstallApkReceiver mInstallApkReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mInstallApkReceiver != null) {
            unregisterReceiver(mInstallApkReceiver);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        if (event.isSuccess()) {
            ARouter.getInstance().build(RouteTable.MODULE1_ATY_MODULE1_MAIN).navigation();
        }
    }

}
