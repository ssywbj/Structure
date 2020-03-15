package com.suheng.structure.module3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.arouter.RouteTable;
import com.suheng.structure.module3.mvp.LoginPresenter;
import com.suheng.structure.module3.mvp.LoginView;
import com.suheng.structure.ui.architecture.basic.PresenterActivity;

@Route(path = RouteTable.MODULE3_ATY_MVP_LOGIN)
public class MVPLoginActivity extends PresenterActivity<LoginPresenter> implements LoginView {

    private LoginPresenter mPresenter = new LoginPresenter(this);

    @Override
    public LoginPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module3_aty_login);

        ((Button) findViewById(R.id.btn_switch_mode)).setText("MVC");

        findViewById(R.id.btn_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MVPLoginActivity.this, FragmentActivity.class));
            }
        });

        findViewById(R.id.btn_switch_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouteTable.MODULE3_ATY_MVC_LOGIN).navigation();
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = findViewById(R.id.edit_name);
                EditText editPwd = findViewById(R.id.edit_pwd);
                getPresenter().login(editName.getText().toString().trim(), editPwd.getText().toString().trim());
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(mTag, mTag + ", onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(mTag, mTag + ", onDestroy");
    }

    @Override
    public void loginFail(String reason) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("登录失败：" + reason)
                .setPositiveButton("确定", null).create().show();
    }
}
