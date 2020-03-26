package com.suheng.structure.ui.architecture.basic;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.ui.R;
import com.suheng.structure.ui.architecture.view.IView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions//PermissionsDispatcher权限框架配置3（共5步）
public abstract class BasicActivity extends AppCompatActivity implements IView {
    protected String mTag;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getClass().getSimpleName();
    }

    @Override
    public void showProgressDialog(CharSequence title, CharSequence message, boolean cancelable) {
        if (this.isUnsafe()) {
            return;
        }

        this.dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(this, title, message, false, cancelable);
        mProgressDialog.show();
    }

    @Override
    public void showProgressDialog(CharSequence message, boolean cancelable) {
        this.showProgressDialog("", message, cancelable);
    }

    @Override
    public void showProgressDialog(CharSequence message) {
        this.showProgressDialog(message, true);
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    @Override
    public void showToast(CharSequence text) {
        if (this.isUnsafe()) {
            return;
        }

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int resId) {
        this.showToast(getString(resId));
    }

    @Override
    public boolean isUnsafe() {
        boolean finishing = isFinishing();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return (isDestroyed() || finishing);
        } else {
            return finishing;
        }
    }

    @Override//PermissionsDispatcher权限框架配置5（共5步）
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //步骤4后，Make Module后才会产生BasicActivityPermissionsDispatcher类
        BasicActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //PermissionsDispatcher权限框架配置4，配置NeedsPermission（共5步）
    //如果没有权限，会弹出系统要求申请相应权限的对话框，点击允许后执行该方法；如果已经有权限，则直接执行该方法
    @Override
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void openedExternalStoragePermission(int businessId) {
    }

    @Override
    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void deniedExternalStoragePermission() {//点击拒绝后执行该方法
        showToast("Permission WRITE Deny");
    }

    /*用户点击拒绝后，当程序再次向用户请求权限时会调用该方法，一般用于说明原因；在PositiveButton上监听request.proceed()
    用于继续往下的申请流程，在NegativeButton上监听request.cancel()用于取消申请流程*/
    @Override
    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showRationaleExternalStoragePermission(final PermissionRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("存取权限用于上传和下载文件；\n如果没有该权限则会影响App的使用！");
        builder.setPositiveButton("我明白了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.proceed();//继续往下申请的流程
            }
        });
        builder.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();//取消申请
            }
        });
        builder.create().show();
    }

    @Override
    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void neverAskAgainExternalStoragePermission() {//点击不再询问后执行的方法
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请").setMessage("在设置—应用管理—" + getString(R.string.app_name)
                + "—权限中开启存储权限，以正常使用应用。");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//引导用户跳到应用设置界面
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("下次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void openedTelephoneDialPermission(String phone) {
    }

    @Override
    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void neverAskAgainCallPhonePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请").setMessage("在设置—应用管理—" + getString(R.string.app_name)
                + "—权限中开启电话权限，以正常使用应用。");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//引导用户跳到应用设置界面
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("下次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void requestExternalStoragePermission(int businessId) {
        BasicActivityPermissionsDispatcher.openedExternalStoragePermissionWithPermissionCheck(this, businessId);
    }

    @Override
    public void requestTelephoneDialPermission(String phone) {
        BasicActivityPermissionsDispatcher.openedTelephoneDialPermissionWithPermissionCheck(this, phone);
    }
}
