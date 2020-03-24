package com.suheng.structure.module3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.arouter.RouteTable;
import com.suheng.structure.data.net.bean.UserInfo;
import com.suheng.structure.data.net.request.LoginTask;
import com.suheng.structure.eventbus.LoginEvent;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnResponseListener;
import com.suheng.structure.net.callback.OnResultListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import org.greenrobot.eventbus.EventBus;

@Route(path = RouteTable.MODULE3_ATY_MVC_LOGIN)
public class MVCLoginActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module3_aty_login);

        ARouter.getInstance().inject(this);

        findViewById(R.id.btn_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MVCLoginActivity.this, FragmentActivity.class));
            }
        });

        findViewById(R.id.btn_switch_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouteTable.MODULE3_ATY_MVP_LOGIN).navigation();
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = findViewById(R.id.edit_name);
                EditText editPwd = findViewById(R.id.edit_pwd);
                login(editName.getText().toString().trim(), editPwd.getText().toString().trim());
            }
        });

        findViewById(R.id.btn_permission_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(v.getId());
            }
        });

        findViewById(R.id.btn_permission_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(v.getId());
            }
        });
    }

    @Override
    public void openedExternalStoragePermission(int businessId) {
        if (businessId == R.id.btn_permission_write) {
            showToast("Write");
        } else if (businessId == R.id.btn_permission_read) {
            showToast("Read");
        }
    }

    private void login(String name, String pwd) {
        if (name.isEmpty()) {
            showToast(R.string.module3_tip_input_user_name);
            return;
        }
        if (pwd.isEmpty()) {
            showToast(R.string.module3_tip_input_pwd);
            return;
        }

        showProgressDialog(getString(R.string.module3_login_progress), true);

        final LoginTask loginTask = new LoginTask(name, pwd);
        loginTask.doRequest();
        loginTask.setOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(String error) {
                Log.e(loginTask.getLogTag(), "onFailure: " + error);

                dismissProgressDialog();
                //loginFail(error);
                showToast(error);
            }
        });
        loginTask.setOnResponseListener(new OnResponseListener() {
            @Override
            public void onResponse(String result) {
                Log.d(loginTask.getLogTag(), "onResponse: " + result);

                dismissProgressDialog();
            }
        });
        loginTask.setOnResultListener(new OnResultListener<UserInfo, String>() {
            @Override
            public void onRightResult(UserInfo data) {
                Log.d(loginTask.getLogTag(), "onRightResult: " + data);

                dismissProgressDialog();
                //mPrefsManager.putLoginStatus(true);

                if (BuildConfig.MODULE3_IS_LIBRARY) {
                    EventBus.getDefault().post(new LoginEvent());
                    finish();
                } else {
                    ARouter.getInstance().build(RouteTable.MODULE3_ATY_MODULE3_MAIN).navigation();
                }
            }

            @Override
            public void onErrorResult(int code, String msg, String data) {
                Log.e(loginTask.getLogTag(), "onErrorResult, code: " + code + ", msg: " + msg + ", onErrorResult: " + data);

                dismissProgressDialog();
                //mPrefsManager.putLoginStatus(false);
                loginFail(msg);
            }
        });
    }

    private void loginFail(String reason) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("登录失败：" + reason)
                .setPositiveButton("确定", null).create().show();
    }
}
