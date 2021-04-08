package com.suheng.structure.hook;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HookActivity extends AppCompatActivity {
    public static final String TAG = HookActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("HookActivity");
        textView.setTextSize(20);
        textView.setTextColor(Color.RED);
        setContentView(textView);

        textView.setOnClickListener(v -> Log.d(TAG, "onClick"));
        this.hookOnClickListener(textView);
    }

    private void hookOnClickListener(View view) {
        try {
            // 得到View的ListenerInfo对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            // 得到原始的OnClickListener对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field onClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            onClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) onClickListener.get(listenerInfo);
            // 用自定义的OnClickListener替换原始的 OnClickListener
            View.OnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            onClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (Exception e) {
            Log.e(TAG, "hook clickListener failed!", e);
        }
    }

    static class HookedOnClickListener implements View.OnClickListener {
        private final View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(v.getContext(), "hook click", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.i(TAG, "After click, do what you want to to.");
        }
    }

}
