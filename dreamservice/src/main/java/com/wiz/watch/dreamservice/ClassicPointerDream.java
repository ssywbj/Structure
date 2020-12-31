package com.wiz.watch.dreamservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.wiz.watch.dreamservice.tmp.MoveScreensaverRunnable;
import com.wiz.watch.dreamservice.tmp.UiDataModel;

import java.lang.reflect.Method;
import java.util.Timer;

public class ClassicPointerDream extends DreamService {
    public static final String TAG = ClassicPointerDream.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "------onCreate-------");
        Context context = getApplication().getApplicationContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        UiDataModel.getUiDataModel().init(context, prefs);
    }

    private ClassicPointerWatchFace mClassicPointerWatchFace;
    private View mContentView;

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
        mClassicPointerWatchFace = findViewById(R.id.classic_pointer_watch_face);
        mContentView = mClassicPointerWatchFace;
        mPositionUpdater = new MoveScreensaverRunnable(mClassicPointerWatchFace);
    }

    private final Timer mTimer = new Timer();

    private MoveScreensaverRunnable mPositionUpdater;

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

        /*mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "mTimer.schedule, hour = ");
                mClassicPointerWatchFace.updateTime();
                mClassicPointerWatchFace.invalidate();
            }
        }, 0, 1000);

        mHandler.sendEmptyMessage(1);*/

        //mPositionUpdater.start();
        startPositionUpdater();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        Log.d(TAG, "------onDreamingStopped-------");
        mTimer.cancel();
        mHandler.removeMessages(1);
        //mPositionUpdater.stop();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "------onDetachedFromWindow-------");
        stopPositionUpdater();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "------onDestroy-------");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            Log.d(TAG, "dispatchMessage, hour = ");
            mClassicPointerWatchFace.updateTime();
            mClassicPointerWatchFace.invalidate();
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    /**
     * The will be drawn shortly. When that draw occurs, the position updater
     * callback will also be executed to choose a random position for the time display as well as
     * schedule future callbacks to move the time display each minute.
     */
    private void startPositionUpdater() {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().addOnPreDrawListener(mStartPositionUpdater);
        }
    }

    /**
     * This activity is no longer in the foreground; position callbacks should be removed.
     */
    private void stopPositionUpdater() {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().removeOnPreDrawListener(mStartPositionUpdater);
        }
        mPositionUpdater.stop();
    }

    private final ViewTreeObserver.OnPreDrawListener mStartPositionUpdater = new StartPositionUpdater();

    private final class StartPositionUpdater implements ViewTreeObserver.OnPreDrawListener {
        /*
         *
         * @return {@code true} to continue with the drawing pass
         */
        @Override
        public boolean onPreDraw() {
            if (mContentView.getViewTreeObserver().isAlive()) {
                // (Re)start the periodic position updater.
                mPositionUpdater.start();

                // This listener must now be removed to avoid starting the position updater again.
                mContentView.getViewTreeObserver().removeOnPreDrawListener(mStartPositionUpdater);
            }
            return true;
        }
    }
}
