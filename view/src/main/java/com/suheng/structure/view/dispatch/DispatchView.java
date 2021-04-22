package com.suheng.structure.view.dispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

public class DispatchView extends androidx.appcompat.widget.AppCompatButton {

    public DispatchView(Context context) {
        super(context);
        this.init();
    }

    public DispatchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DispatchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("事件分发机制开始分发 ----> 子View  dispatchTouchEvent");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event);
        onTouchEvent = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("事件分发机制处理 ----> 子View onTouchEvent: " + onTouchEvent);
        }
        return onTouchEvent;
    }

}
