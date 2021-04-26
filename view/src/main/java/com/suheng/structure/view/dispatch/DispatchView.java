package com.suheng.structure.view.dispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(DispatchActivity.TAG, "事件分发机制开始分发 ----> 子View  onClick");
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(DispatchActivity.TAG, "事件分发机制开始分发 ----> 子View  onTouch");
                }
                return false;
                //return true;
            }
        });

        //优先级：onTouch > onTouchEvent > onClick
        /*if (mOnTouchListener == null) {
            if (onTouchEvent(MotionEvent)) {
                onTouchEvent消费掉事件，如果设置了点击事件还能响应点击事件
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(View);
                }
            } else {
                不消费交给ViewGroup处理，如果设置了点击事件，此时不能响应点击事件
            }
        } else {
            if (mOnTouchListener.onTouch(View, MotionEvent)) {
                OnTouchListener消费掉事件，此时onTouchEvent、onClick均不响应
            } else {
                if (onTouchEvent(MotionEvent)) {
                    onTouchEvent消费掉事件，如果设置了点击事件还能响应点击事件
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(View);
                    }
                } else {
                    不消费交给ViewGroup处理，如果设置了点击事件，此时不能响应点击事件
                }
            }
        }*/

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.v(DispatchActivity.TAG, "事件分发机制开始分发 ----> 子View  dispatchTouchEvent");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event); //View的onTouchEvent默认返回true，即在它这里把事件消费掉
        onTouchEvent = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.v(DispatchActivity.TAG, "事件分发机制处理 ----> 子View onTouchEvent: " + onTouchEvent);
        }
        return onTouchEvent;
    }

}
