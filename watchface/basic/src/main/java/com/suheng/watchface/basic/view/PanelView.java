package com.suheng.watchface.basic.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class PanelView extends FaceAnimView {

    public PanelView(Context context) {
        super(context);
        this.init();
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setAppearAnimPointer(true);
        //setDefaultTime(8, 36, 55);
        //setDefaultTime(8, 36, TIME_NONE);
        setDefaultTime(TIME_NONE, TIME_NONE, 55);
    }

}
