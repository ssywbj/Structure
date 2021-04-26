package com.suheng.structure.view.dispatch;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;

public class DispatchActivity extends AppCompatActivity {
    public static final String TAG = "DispatchEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispatch_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "事件分发机制开始分发 ----> Activity  dispatchTouchEvent");
        }
        return super.dispatchTouchEvent(ev);
    }

    //当一个点击事件产生后，它的传递过顺序为Activity -> Window -> View，即事件总是先传递给Activity，Activity再传递给Window，最后Window再传递给顶级View。
    //顶级View接收到事件后，就会按照事件分发机制去分发事件。考虑一种情况：如果一个View的onTouchEvent返回false，那么它父容器的onTouchEvent将会被调用，
    //依次类推。如果所有的元素都不处理这个事件，那么这个事件将会最终传递给Activity处理，即Activity的onTouchEvent方法会被调用
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouchEvent = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "事件分发机制处理 ----> Activity onTouchEvent 执行：" + onTouchEvent);
        }
        return onTouchEvent;
    }

}
