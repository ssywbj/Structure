package com.suheng.structure.ui.architecture.basic;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.suheng.structure.ui.architecture.presenter.BasicPresenter;
import com.suheng.structure.ui.architecture.view.MvpView;

public abstract class PresenterActivity<Presenter extends BasicPresenter> extends BasicActivity implements MvpView<Presenter> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPresenter() != null) {
            getPresenter().onCreate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getPresenter() != null) {
            getPresenter().onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getPresenter() != null) {
            getPresenter().onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getPresenter() != null) {
            getPresenter().onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) {
            getPresenter().onDestroy();
        }
    }

    @Override
    public BasicActivity getBasicActivity() {
        return this;
    }
}
