package com.suheng.structure.ui.architecture.basic;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suheng.structure.ui.architecture.view.IView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public abstract class BasicFragment extends Fragment implements IView {

    protected BasicActivity mActivity;
    protected String mTag;

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mTag = getClass().getSimpleName();
        if (context instanceof BasicActivity) {
            mActivity = (BasicActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return inflater.inflate(this.getLayoutId(), container, true);
    }

    @Override
    public void showProgressDialog(CharSequence title, CharSequence message, boolean cancelable) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showProgressDialog(title, message, cancelable);
    }

    @Override
    public void showProgressDialog(CharSequence message, boolean cancelable) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showProgressDialog(message, cancelable);
    }

    @Override
    public void showProgressDialog(CharSequence message) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showProgressDialog(message);
    }

    @Override
    public void dismissProgressDialog() {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.dismissProgressDialog();
    }

    @Override
    public void showToast(CharSequence text) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showToast(text);
    }

    @Override
    public void showToast(int resId) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showToast(resId);
    }

    @Override
    public boolean isUnsafe() {
        return (mActivity == null || mActivity.isUnsafe() || !isAdded() || isDetached());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BasicFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void openedExternalStoragePermission(int businessId) {
    }

    @Override
    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void deniedExternalStoragePermission() {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.deniedExternalStoragePermission();
    }

    @Override
    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showRationaleExternalStoragePermission(final PermissionRequest request) {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.showRationaleExternalStoragePermission(request);
    }

    @Override
    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void neverAskAgainExternalStoragePermission() {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.neverAskAgainExternalStoragePermission();
    }

    @Override
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void openedTelephoneDialPermission(String phone) {
    }

    @Override
    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void neverAskAgainCallPhonePermission() {
        if (this.isUnsafe()) {
            return;
        }
        mActivity.neverAskAgainCallPhonePermission();
    }

    @Override
    public void requestExternalStoragePermission(int businessId) {
        BasicFragmentPermissionsDispatcher.openedExternalStoragePermissionWithPermissionCheck(this, businessId);
    }

    @Override
    public void requestTelephoneDialPermission(String phone) {
        BasicFragmentPermissionsDispatcher.openedTelephoneDialPermissionWithPermissionCheck(this, phone);
    }

}
