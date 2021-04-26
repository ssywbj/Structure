package com.suheng.structure.view.dispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(DispatchActivity.TAG, "事件分发机制开始分发 ----> 父容器  dispatchTouchEvent");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event); //ViewGroup的onTouchEvent默认返回false，即在它这里不消费事件
        //onTouchEvent = true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(DispatchActivity.TAG,"事件分发机制处理 ----> 父容器 LinearLayout onTouchEvent: " + onTouchEvent);
        }
        return onTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceptTouchEvent = super.onInterceptTouchEvent(ev);
        //interceptTouchEvent = true;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(DispatchActivity.TAG,"事件分发机制开始分发 ----> 父容器是否拦截  onInterceptTouchEvent: " + interceptTouchEvent);
        }
        return interceptTouchEvent;
        //true表示ViewGroup拦截当前事件自己处理，不进行事件分发，子View收不到任何事件；
        //false表示ViewGroup不拦截当前事件，而把它传递给子View，接着子View的dispatchTouchEvent方法就会被调用
    }

}
