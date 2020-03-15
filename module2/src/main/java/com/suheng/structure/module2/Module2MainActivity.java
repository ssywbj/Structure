package com.suheng.structure.module2;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.arouter.RouteTable;
import com.suheng.structure.data.DataManager;
import com.suheng.structure.data.net.bean.UserInfo;
import com.suheng.structure.data.net.request.LoginTask;
import com.suheng.structure.module2.request.BeautyTask;
import com.suheng.structure.net.callback.OnDownloadListener;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnResultListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.File;

@Route(path = RouteTable.MODULE2_ATY_MODULE2_MAIN)
public class Module2MainActivity extends BasicActivity {

    @Autowired
    DataManager mDataManager;
    private Button mBtnLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module2_aty_module2_main);

        ARouter.getInstance().inject(this);

        mBtnLoginStatus = findViewById(R.id.btn_login_status);
        mBtnLoginStatus.setText(mDataManager.isLoginSuccessful() ? "退出" : "登录");

        mBtnLoginStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                if (mDataManager.isLoginSuccessful()) {
                    mDataManager.doExitLoginRequest().setOnResultListener(new OnResultListener<UserInfo, String>() {
                        @Override
                        public void onRightResult(UserInfo data) {
                            dismissProgressDialog();
                            mDataManager.setLoginSuccessful(false);
                            mBtnLoginStatus.setText("登录");
                        }

                        @Override
                        public void onErrorResult(int code, String msg, String data) {
                            dismissProgressDialog();
                            showToast("退出登录失败！");
                        }
                    });
                } else {
                    final LoginTask loginTask = mDataManager.doLoginRequest("Wbj", "wbj89");
                    loginTask.setOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(String error) {
                            Log.e(loginTask.getLogTag(), "onFailure: " + error);

                            dismissProgressDialog();
                        }
                    });
                    loginTask.setOnResultListener(new OnResultListener<UserInfo, String>() {
                        @Override
                        public void onRightResult(UserInfo data) {
                            Log.d(loginTask.getLogTag(), "onRightResult: " + data);

                            dismissProgressDialog();
                            mDataManager.setLoginSuccessful(true);
                            mBtnLoginStatus.setText("退出");
                        }

                        @Override
                        public void onErrorResult(int code, String msg, String data) {
                            Log.e(loginTask.getLogTag(), "onErrorResult, code:" + code + ", msg: " + msg + ", onErrorResult: " + data);

                            dismissProgressDialog();
                        }
                    });

                    /*final LoginTask3 loginTask3 = new LoginTask3("Wbj", "wbj89");
                    loginTask3.doRequest();
                    loginTask3.setOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(String error) {
                            Log.e(loginTask3.getLogTag(), "onFailure: " + error);
                            dismissProgressDialog();
                            showToast(error);
                        }
                    });
                    loginTask3.setOnResponseListener(new OnResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            Log.d(loginTask3.getLogTag(), "onResponse: " + result);
                            dismissProgressDialog();
                            mDataManager.setLoginSuccessful(true);
                            mBtnLoginStatus.setText("退出");
                        }
                    });*/
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
