package com.suheng.structure.module3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicFragment;

import java.util.Random;

public class MVCLoginFragment extends BasicFragment {

    @Override
    public int getLayoutId() {
        return R.layout.module3_aty_login;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                login(editName.getText().toString().trim(), editPwd.getText().toString().trim());
            }
        });
    }

    private void login(final String name, final String pwd) {
        if (name.isEmpty()) {
            showToast(R.string.module3_tip_input_user_name);
            return;
        }
        if (pwd.isEmpty()) {
            showToast(R.string.module3_tip_input_pwd);
            return;
        }

        showProgressDialog(getString(R.string.module3_login_progress), false);

        new Handler().postDelayed(new Runnable() {//模拟网络请求
            @Override
            public void run() {
                if (isUnsafe()) {
                    return;
                }

                dismissProgressDialog();

                int random = new Random().nextInt(5);
                if (random == 0) {
                    loginFail("账号不存在！");
                } else if (random == 1) {
                    loginFail("密码不正确！");
                } else {
                    ARouter.getInstance().build(RouteTable.MODULE3_ATY_MODULE3_MAIN).navigation();
                }
            }
        }, 2000);
    }

    private void loginFail(String reason) {
        new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("登录失败：" + reason)
                .setPositiveButton("确定", null).create().show();
    }
}
