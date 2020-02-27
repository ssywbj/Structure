package com.suheng.structure.ui.architecture.view;

import permissions.dispatcher.PermissionRequest;

public interface PermissionView {

    void openedExternalStoragePermission(int businessId);

    void deniedExternalStoragePermission();

    void showRationaleExternalStoragePermission(final PermissionRequest request);

    void neverAskAgainExternalStoragePermission();

    void openedTelephoneDialPermission(String phone);

    void neverAskAgainCallPhonePermission();

    void requestExternalStoragePermission(int businessId);

    void requestTelephoneDialPermission(String phone);
}
