package com.suheng.photo.view;

import com.suheng.photo.presenter.PhotoPresenter;
import com.suheng.structure.ui.architecture.view.MvpView;

public interface PhotoView extends MvpView<PhotoPresenter> {

    void notifyDataSetChanged();
}
