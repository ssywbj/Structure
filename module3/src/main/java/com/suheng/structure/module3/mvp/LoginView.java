package com.suheng.structure.module3.mvp;

import com.suheng.structure.ui.architecture.view.MvpView;

public interface LoginView extends MvpView<LoginPresenter> {
    /**
     * 登录失败
     *
     * @param reason 原因
     */
    void loginFail(String reason);
}
