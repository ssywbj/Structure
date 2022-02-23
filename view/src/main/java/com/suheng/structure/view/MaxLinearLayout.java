package com.suheng.structure.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class MaxLinearLayout extends LinearLayout {

    public MaxLinearLayout(Context context) {
        super(context);
    }

    public MaxLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        //if(mBoundedWidth > 0 && mBoundedWidth < measuredWidth) {
        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(300, measureMode);
        //}
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
