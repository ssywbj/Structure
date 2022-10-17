package com.suheng.structure.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimCheckBox extends CheckBox {
    public static final String TAG = AnimCheckBox.class.getSimpleName();
    private CheckedDrawable mCheckedDrawable, mNormalDrawable, mCurrentDrawable;

    public AnimCheckBox(Context context) {
        super(context);
        this.init();
    }

    public AnimCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_style_selector));

        StateListDrawable stateListDrawable = new StateListDrawable();
        mCheckedDrawable = new CheckedDrawable(getContext(), true);
        mNormalDrawable = new CheckedDrawable(getContext());
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, mCheckedDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, ContextCompat.getDrawable(getContext(), R.drawable.checkbox_unenabled));
        stateListDrawable.addState(new int[]{}, mNormalDrawable);
        setButtonDrawable(stateListDrawable);
        //setBackground(null);

        //setChecked(true);
        //setEnabled(false);

        boolean checked = isChecked();
        Log.d(TAG, "init, checked: " + checked);
        if (checked) {
            mCurrentDrawable = mCheckedDrawable;
        } else {
            mCurrentDrawable = mNormalDrawable;
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        Log.d(TAG, "setChecked, checked: " + checked);
        if (mCurrentDrawable == null) {
            return;
        }
        /*if (mCheckedCurrent == checked) {
            return;
        }*/

        //Log.d(TAG, "setChecked, before: " + mDrawableCurrent);
        CheckedDrawable tempDrawable = mCurrentDrawable;
        mCurrentDrawable = checked ? mCheckedDrawable : mNormalDrawable;
        mCurrentDrawable.startAnim(tempDrawable);
        //Log.d(TAG, "setChecked, after: " + mDrawableCurrent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCurrentDrawable.cancelAnim();
    }
}
