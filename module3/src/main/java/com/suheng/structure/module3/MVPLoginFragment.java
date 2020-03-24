package com.suheng.structure.module3;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.structure.data.net.request.LoginTask3;
import com.suheng.structure.data.net.request.LoginTask4;
import com.suheng.structure.module3.mvp.LoginPresenter;
import com.suheng.structure.module3.mvp.LoginView;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnResponseListener;
import com.suheng.structure.ui.architecture.basic.PresenterFragment;

public class MVPLoginFragment extends PresenterFragment<LoginPresenter> implements LoginView {

    private LoginPresenter mPresenter = new LoginPresenter(this);

    /*@Override
    public LoginPresenter getPresenter() {
        return mPresenter;
    }*/

    @Override
    public LoginPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.module3_aty_login;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Button) view.findViewById(R.id.btn_switch_mode)).setText("MVC");

        view.findViewById(R.id.btn_switch_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MVPLoginActivity.class));
            }
        });

        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = view.findViewById(R.id.edit_name);
                EditText editPwd = view.findViewById(R.id.edit_pwd);
                getPresenter().login(editName.getText().toString().trim(), editPwd.getText().toString().trim());
            }
        });

        TextView textView = view.findViewById(R.id.btn_permission_read);
        textView.setText("CallPhone");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTelephoneDialPermission("18819059959");
            }
        });

        view.findViewById(R.id.btn_permission_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoginTask3 loginTask3 = new LoginTask3("Wbj", "wbj89");
                loginTask3.doRequest(MVPLoginFragment.this);
                loginTask3.setOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String error) {
                        Log.e(loginTask3.getLogTag(), "onFailure: " + error);
                    }
                });
                loginTask3.setOnResponseListener(new OnResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        Log.d(loginTask3.getLogTag(), "onResponse: " + result);
                    }
                });

                final LoginTask4 loginTask4 = new LoginTask4("Wbj", "wbj89");
                loginTask4.doPostRequest();
                loginTask4.setOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String error) {
                        Log.e(loginTask4.getLogTag(), "onFailure: " + error);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(mTag, mTag + ", onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(mTag, mTag + ", onDestroy");
    }

    @Override
    public void loginFail(String reason) {
        new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("登录失败：" + reason)
                .setPositiveButton("确定", null).create().show();
    }

    @Override
    public void openedTelephoneDialPermission(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));//拨号盘Intent
        startActivity(intent);
    }
}
