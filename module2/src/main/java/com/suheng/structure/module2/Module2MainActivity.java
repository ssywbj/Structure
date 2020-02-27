package com.suheng.structure.module2;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.common.data.PrefsManager;
import com.suheng.structure.common.data.net.RequestManager;
import com.suheng.structure.common.data.net.callback.LoginListener;
import com.suheng.structure.common.event.ExitLoginEvent;
import com.suheng.structure.module2.request.BeautyTask;
import com.suheng.structure.net.callback.OnDownloadListener;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

@Route(path = RouteTable.MODULE2_ATY_MODULE2_MAIN)
public class Module2MainActivity extends BasicActivity implements LoginListener {

    private Button mBtnLoginStatus;
    @Autowired
    PrefsManager mPrefsManager;
    @Autowired
    RequestManager mRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module2_aty_module2_main);

        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);

        /*RequestManager loginManager = (RequestManager) ARouter.getInstance()
                .build(RouteTable.COMMON_PROVIDER_REQUEST_MANAGER).navigation();*/
        Log.d("RequestManager", "init request manager dagger = " + mRequestManager);

        mBtnLoginStatus = findViewById(R.id.btn_login_status);
        mBtnLoginStatus.setText(mPrefsManager.getLoginStatus() ? "退出" : "登录");

        mBtnLoginStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                if (mPrefsManager.getLoginStatus()) {
                    mRequestManager.doExitRequest();
                } else {
                    mRequestManager.doLoginRequest("Zhipu", "123456", Module2MainActivity.this);
                }
            }
        });

        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(R.id.btn_download);
            }
        });

        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(R.id.btn_upload);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ExitLoginEvent event) {
        dismissProgressDialog();
        if (event.isSuccess()) {
            mPrefsManager.putLoginStatus(false);
            mBtnLoginStatus.setText("登录");
        } else {
            showToast("退出登录失败！");
        }
    }

    @Override
    public void onLoginFail(String reason, int code) {
        dismissProgressDialog();
        showToast(reason);
    }

    @Override
    public void onLoginSuccess() {
        dismissProgressDialog();
        mPrefsManager.putLoginStatus(true);
        mBtnLoginStatus.setText("退出");
    }

    @Override
    public void openedExternalStoragePermission(int businessId) {
        if (businessId == R.id.btn_download) {
            /*final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_MOVIES));
            final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES).getPath());*/
            final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath(), System.currentTimeMillis() + ".jpg");
            beautyTask.doPostRequest();
            beautyTask.setOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(String error) {
                    Log.e(beautyTask.getLogTag(), "download fail: " + error);
                    showToast("下载失败");
                }
            });
            beautyTask.setOnDownloadListener(new OnDownloadListener() {
                @Override
                public void onDownloading(double percentage, long progress, long total) {
                    Log.d(beautyTask.getLogTag(), "percentage: " + percentage + ", progress: "
                            + progress + ", total: " + total + ", thread: " + Thread.currentThread().getName());
                }

                @Override
                public void onDownloadFinish(File file, double takeTime) {
                    showToast("下载完成");
                    Log.d(beautyTask.getLogTag(), "file: " + file + ", take time: " + takeTime + "s");
                }
            });
        }
    }
}
