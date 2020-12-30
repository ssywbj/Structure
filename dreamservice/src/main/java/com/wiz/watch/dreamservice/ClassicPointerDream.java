package com.wiz.watch.dreamservice;

import android.service.dreams.DreamService;
import android.util.Log;

import java.lang.reflect.Method;

public class ClassicPointerDream extends DreamService {
    public static final String TAG = ClassicPointerDream.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "------onCreate-------");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "------onAttachedToWindow-------");
        //Exit dream upon user touch
        setInteractive(false);
        //Hide system UI
        setFullscreen(true);
        //Set the dream layout
        setContentView(R.layout.view_classic_pointer_watch_face);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        Log.d(TAG, "------onDreamingStarted-------");
        try {
            Class<?> clz = Class.forName("android.service.dreams.DreamService");
            Method method = clz.getMethod("setDozeScreenState", int.class);
            method.invoke(ClassicPointerDream.this, 4);

            method = clz.getMethod("startDozing");
            method.invoke(ClassicPointerDream.this);
        } catch (Exception e) {
            Log.e(TAG, "reflect invoke error: " + e.toString());
        }
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        Log.d(TAG, "------onDreamingStopped-------");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "------onDetachedFromWindow-------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "------onDestroy-------");
    }

}
