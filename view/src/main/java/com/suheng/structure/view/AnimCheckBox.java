package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimCheckBox extends CheckBox {
    public static final String TAG = AnimCheckBox.class.getSimpleName();

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
        //setButtonDrawable(null);
        setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_style_selector));
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        Log.d(TAG, "setChecked, checked: " + checked);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
