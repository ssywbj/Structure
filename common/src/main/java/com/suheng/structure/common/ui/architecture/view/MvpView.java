package com.suheng.structure.common.ui.architecture.view;

import com.suheng.structure.common.ui.architecture.basic.BasicActivity;
import com.suheng.structure.common.ui.architecture.presenter.BasicPresenter;
import com.suheng.structure.common.ui.architecture.presenter.IPresenter;

public interface MvpView<Presenter extends IPresenter> extends IView {

    BasicActivity getBasicActivity();

    //Presenter getPresenter();

    <T extends BasicPresenter> T getPresenter();
}