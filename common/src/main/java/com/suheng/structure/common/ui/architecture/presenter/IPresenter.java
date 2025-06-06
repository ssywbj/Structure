package com.suheng.structure.common.ui.architecture.presenter;

import com.suheng.structure.common.ui.architecture.view.IView;

public interface IPresenter<View extends IView> {

    View getView();
}
