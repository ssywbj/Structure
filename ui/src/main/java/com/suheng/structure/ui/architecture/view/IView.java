package com.suheng.structure.ui.architecture.view;

import androidx.annotation.StringRes;

public interface IView extends PermissionView {

    void showProgressDialog(CharSequence title, CharSequence message, boolean cancelable);

    void showProgressDialog(CharSequence message, boolean cancelable);

    void showProgressDialog(CharSequence message);

    void dismissProgressDialog();

    void showToast(CharSequence text);

    void showToast(@StringRes int resId);

    boolean isUnsafe();
}
