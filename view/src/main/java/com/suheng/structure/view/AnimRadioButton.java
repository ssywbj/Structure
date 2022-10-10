package com.suheng.structure.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimRadioButton extends RadioButton {
    public static final String TAG = AnimRadioButton.class.getSimpleName();

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
        setBackground(null);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_checked));
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_uneabled));
        stateListDrawable.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.radio_btn_unchecked));
        setButtonDrawable(stateListDrawable);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        Log.d(TAG, "setChecked, checked: " + checked);
    }

}
