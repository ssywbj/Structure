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
        //Button的onTouchEvent默认返回true，即在它这里把事件消费掉，而View的onTouchEvent默认返回false
        boolean onTouchEvent = super.onTouchEvent(event);
        onTouchEvent = false;
        int action = event.getAction();
        /*if (action == MotionEvent.ACTION_DOWN) {
            Log.v(DispatchActivity.TAG, "事件分发机制处理 ----> 子View onTouchEvent: " + onTouchEvent);
        }*/

        switch (action) {
            case MotionEvent.ACTION_DOWN: //按下后才有事件，所以一般第一个事件为Down
                Log.v(DispatchActivity.TAG, "事件分发机制处理 ----> 子View onTouchEvent: " + onTouchEvent + ", " + action);
                //如果返回false，那么move、up等事件将不会被触发，因为false表示不消费事件，事件会被直接往父容器传递
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v(DispatchActivity.TAG, "事件分发机制处理 ----> 子View onTouchEvent Move: " + action);
                break;
            case MotionEvent.ACTION_UP:
                Log.v(DispatchActivity.TAG, "事件分发机制处理 ----> 子View onTouchEvent Up: " + action);
                break;
        }

        return onTouchEvent;
    }

}
