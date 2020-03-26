package com.suheng.structure.module3.mvp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.common.eventbus.LoginEvent;
import com.suheng.structure.data.DataManager;
import com.suheng.structure.data.net.bean.UserInfo;
import com.suheng.structure.data.net.request.LoginTask;
import com.suheng.structure.module3.BuildConfig;
import com.suheng.structure.module3.R;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnResultListener;
import com.suheng.structure.ui.architecture.presenter.BasicPresenter;

import org.greenrobot.eventbus.EventBus;

public class LoginPresenter extends BasicPresenter<LoginView> {

    @Autowired
    DataManager mDataManager;

    public LoginPresenter(LoginView loginView) {
        super(loginView);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(mTag, mTag + ", onCreate");
        ARouter.getInstance().inject(this);
    }

    @Override
    public void onStart() {
        Log.d(mTag, mTag + ", onStart");
    }

    @Override
    public void onResume() {
        Log.d(mTag, mTag + ", onResume");
    }

    @Override
    public void onPause() {
        Log.d(mTag, mTag + ", onPause");
    }

    @Override
    public void onStop() {
        Log.d(mTag, mTag + ", onStop");
    }

    @Override
    public void onRestart() {
        Log.d(mTag, mTag + ", onRestart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(mTag, mTag + ", onDestroy");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(mTag, mTag + ", onActivityCreated");
    }

    @Override
    public void onDestroyView() {
        Log.d(mTag, mTag + ", onDestroyView");
    }

    public void login(@NonNull final String name, @NonNull final String pwd) {
        if (name.isEmpty()) {
            getView().showToast(R.string.module3_tip_input_pwd);
            return;
        }
        if (pwd.isEmpty()) {
            getView().showToast(R.string.module3_tip_input_pwd);
            return;
        }

        getView().showProgressDialog(getContext().getString(R.string.module3_login_progress), true);

        /*final LoginTask2 loginTask2 = new LoginTask2(name, pwd);
        loginTask2.doPostRequest(this);
        loginTask2.setOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(String error) {
                Log.e(loginTask2.getLogTag(), "onFailure: " + error);
                getView().dismissProgressDialog();
            }
        });
        loginTask2.setOnResultListener(new OnResultListener<UserInfo, UserInfo>() {
            @Override
            public void onRightResult(UserInfo data) {
                Log.d(loginTask2.getLogTag(), "onRightResult: " + data);

                getView().dismissProgressDialog();
                mDataManager.setLoginSuccessful(true);

                if (BuildConfig.IS_LIBRARY) {
                    EventBus.getDefault().post(new LoginEvent());
                    getActivity().finish();
                } else {
                    ARouter.getInstance().build(RouteTable.MODULE3_ATY_MODULE3_MAIN).navigation();
                }
            }

            @Override
            public void onErrorResult(int code, String msg, UserInfo data) {
                Log.e(loginTask2.getLogTag(), "onErrorResult, code:" + code + ", msg: " + msg + ", onErrorResult: " + data);

                getView().dismissProgressDialog();
                mDataManager.setLoginSuccessful(false);
                getView().loginFail(msg);
            }
        });*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {*/


        final LoginTask loginTask = mDataManager.doLoginRequest(name, pwd);
        loginTask.setOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(int code, String error) {
                getView().dismissProgressDialog();
                getView().showToast("登录失败");
            }
        });
        loginTask.setOnResultListener(new OnResultListener<UserInfo, String>() {
            @Override
            public void onRightResult(UserInfo data) {
                Log.d(loginTask.getLogTag(), "onRightResult: " + data);

                getView().dismissProgressDialog();
                mDataManager.setLoginSuccessful(true);

                if (BuildConfig.MODULE3_IS_LIBRARY) {
                    EventBus.getDefault().post(new LoginEvent());
                    getActivity().finish();
                } else {
                    ARouter.getInstance().build(RouteTable.MODULE3_ATY_MODULE3_MAIN).navigation();
                }
            }

            @Override
            public void onErrorResult(int code, String msg, String data) {
                Log.e(loginTask.getLogTag(), "onErrorResult, code:" + code + ", msg: " + msg + ", onErrorResult: " + data);

                getView().dismissProgressDialog();
                mDataManager.setLoginSuccessful(false);
                getView().loginFail(msg);
            }
        });

            /*}
        }).start();*/
    }
}
