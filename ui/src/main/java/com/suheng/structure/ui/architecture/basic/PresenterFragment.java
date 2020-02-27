package com.suheng.structure.ui.architecture.basic;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.suheng.structure.ui.architecture.presenter.BasicPresenter;
import com.suheng.structure.ui.architecture.view.MvpView;

public abstract class PresenterFragment<Presenter extends BasicPresenter> extends BasicFragment implements MvpView<Presenter> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPresenter() != null) {
            getPresenter().onCreate();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getPresenter() != null) {
            getPresenter().onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getPresenter() != null) {
            getPresenter().onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getPresenter() != null) {
            getPresenter().onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) {
            getPresenter().onDestroy();
        }
    }

    @Override
    public BasicActivity getBasicActivity() {
        return mActivity;
    }

}
