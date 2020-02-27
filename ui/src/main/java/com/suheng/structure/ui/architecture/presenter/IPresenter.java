package com.suheng.structure.ui.architecture.presenter;

import com.suheng.structure.ui.architecture.view.IView;

public interface IPresenter<View extends IView> {

    View getView();
}
