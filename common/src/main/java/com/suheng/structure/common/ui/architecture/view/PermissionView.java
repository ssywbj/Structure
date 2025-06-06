package com.suheng.structure.common.ui.architecture.view;

public interface PermissionView {

    void openedExternalStoragePermission(int businessId);

    void deniedExternalStoragePermission();

    void showRationaleExternalStoragePermission(/*final PermissionRequest request*/);

    void neverAskAgainExternalStoragePermission();

    void openedTelephoneDialPermission(String phone);

    void neverAskAgainCallPhonePermission();

    void requestExternalStoragePermission(int businessId);

    void requestTelephoneDialPermission(String phone);
}
