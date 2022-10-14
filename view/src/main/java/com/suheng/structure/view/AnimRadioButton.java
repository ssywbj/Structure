package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimRadioButton extends RadioButton {
    public static final String TAG = AnimRadioButton.class.getSimpleName();
    private RadioDrawable mCheckedDrawable, mNormalDrawable, mCurrentDrawable;

    public AnimRadioButton(Context context) {
        super(context);
        this.init();
    }

    public AnimRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackground(new ColorDrawable(Color.BLACK));

        StateListDrawable stateListDrawable = new StateListDrawable();
        /*stateListDrawable.addState(new int[]{android.R.attr.state_checked}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_checked));
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_uneabled));
        stateListDrawable.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_unchecked));
        setButtonDrawable(stateListDrawable);*/

        mCheckedDrawable = new RadioDrawable(getContext(), true);
        mNormalDrawable = new RadioDrawable(getContext());
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, mCheckedDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_uneabled));
        stateListDrawable.addState(new int[]{}, mNormalDrawable);

        setButtonDrawable(stateListDrawable);

        //setChecked(true);
        boolean checked = isChecked();
        Log.v(TAG, "init, checked: " + checked);
        if (checked) {
            mCurrentDrawable = mCheckedDrawable;
        } else {
            mCurrentDrawable = mNormalDrawable;
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        //Log.d(TAG, "setChecked, checked: " + checked + ", mCurrentDrawable: " + mCurrentDrawable);
        if (mCurrentDrawable == null) {
            return;
        }
        if (checked && mCurrentDrawable == mCheckedDrawable) {
            //Log.v(TAG, "setChecked, checked && mCurrentDrawable == mCheckedDrawable");
            return;
        }
        if (!checked && mCurrentDrawable == mNormalDrawable) {
            //Log.v(TAG, "setChecked, !checked && mCurrentDrawable == mNormalDrawable");
            return;
        }

        RadioDrawable tempDrawable = mCurrentDrawable;
        tempDrawable.cancelAnim();

        mCurrentDrawable = checked ? mCheckedDrawable : mNormalDrawable;
        mCurrentDrawable.setAnimParams(tempDrawable);
        mCurrentDrawable.startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCurrentDrawable.cancelAnim();
    }
}
