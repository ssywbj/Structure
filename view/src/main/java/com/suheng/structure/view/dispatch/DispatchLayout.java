package com.suheng.structure.view.dispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class DispatchLayout extends LinearLayout {

    public DispatchLayout(Context context) {
        super(context);
        this.init();
    }

    public DispatchLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DispatchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {

    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean consume;
        if (onInterceptTouchEvent(ev)) {
            consume = onTouchEvent(ev);
        } else {
            consume = getChildAt(0).dispatchTouchEvent(ev);
        }
        return consume;
    }*/

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("事件分发机制开始分发 ----> 父容器  dispatchTouchEvent");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event);
        //onTouchEvent = true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("事件分发机制处理 ----> 父容器 LinearLayout onTouchEvent: " + onTouchEvent);
        }
        //return false;
        return onTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceptTouchEvent = super.onInterceptTouchEvent(ev);
        //interceptTouchEvent = true;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("事件分发机制开始分发 ----> 父容器是否拦截  onInterceptTouchEvent: " + interceptTouchEvent);
        }
        return interceptTouchEvent;
    }

}
