package com.suheng.structure.ui.architecture.presenter;

import android.content.Context;

import com.suheng.structure.ui.architecture.basic.BasicActivity;
import com.suheng.structure.ui.architecture.view.MvpView;

public abstract class BasicPresenter<View extends MvpView> implements IPresenter<View> {
    protected String mTag;
    private View mView;

    public BasicPresenter(View view) {
        mView = view;
    }

    @Override
    public View getView() {
        return mView;
    }

    protected BasicActivity getActivity() {
        return mView.getBasicActivity();
    }

    protected Context getContext() {
        return this.getActivity().getApplicationContext();
    }

    protected boolean isUnsafe() {
        if (mView == null) {
            return true;
        } else {
            return mView.isUnsafe();
        }
    }

    public void onCreate() {
        mTag = getClass().getSimpleName();
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
        mView = null;
    }
}
