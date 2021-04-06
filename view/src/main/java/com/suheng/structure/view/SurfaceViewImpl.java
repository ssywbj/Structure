package com.suheng.structure.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SurfaceViewImpl extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    public static final String TAG = SurfaceViewImpl.class.getSimpleName();
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private boolean mIsRunning;
    private Paint mPaintText;

    private Paint mPaintSecond;
    private final Rect mRectSecond = new Rect();
    private final Rect mRectTextSecond = new Rect();

    private Paint mPaintMinute;
    private final Rect mRectMinute = new Rect();
    private final Rect mRectTextMinute = new Rect();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Second Runnable, Second Runnable");
            drawSecond();

            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            if (getHandler() != null) {
                getHandler().postDelayed(mRunnable, delayMillis);
            }
        }
    };

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Time Changed Receiver: action = " + intent.getAction());
            drawMinute();
        }
    };

    public SurfaceViewImpl(Context context) {
        super(context);
        this.init();
    }

    public SurfaceViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        getHolder().addCallback(this);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(50);
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setColor(Color.BLACK);

        mPaintSecond = new Paint();
        mPaintSecond.setStyle(Paint.Style.FILL);
        mPaintSecond.setColor(Color.BLUE);

        mPaintMinute = new Paint(mPaintSecond);
        mPaintMinute.setColor(Color.WHITE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: handler = " + getHandler() + ", holder = " + holder);
        mIsRunning = true;
        //new Thread(this).start();
        //this.draw();

        this.drawBg(holder);

        if (getHandler() != null) {
            getHandler().post(mRunnable);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        getContext().registerReceiver(mTimeChangedReceiver, intentFilter);
        this.drawMinute();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: format = " + format + ", width = " + width + ", height = " + height);
        final int rectWidth = 100, rectHeight = 80;
        mRectSecond.set((width - rectWidth) / 2, height / 2 - 5 - rectHeight, (width + rectWidth) / 2, height / 2 - 5);
        mRectMinute.set((width - rectWidth) / 2, height / 2 + 5, (width + rectWidth) / 2, height / 2 + 5 + rectHeight);

        this.drawBg(holder);
        this.drawSecond();
        this.drawMinute();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed, surfaceDestroyed");
        mIsRunning = false;
        if (getHandler() != null) {
            getHandler().removeCallbacks(mRunnable);
        }
        getContext().unregisterReceiver(mTimeChangedReceiver);
    }

    @Override
    public void run() {
        while (mIsRunning) {
            drawSecond();
        }
    }

    private void drawBg(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas == null) {
                return;
            }

            canvas.drawColor(Color.RED);
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawSecond() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(mRectSecond);
            if (canvas == null) {
                return;
            }

            canvas.drawRect(mRectSecond, mPaintSecond);
            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);
            String text = second / 10 + "" + second % 10;
            mPaintText.getTextBounds(text, 0, text.length(), mRectTextSecond);
            canvas.drawText(text, mRectSecond.centerX() - mRectTextSecond.centerX(), mRectSecond.centerY() - mRectTextSecond.centerY(), mPaintText);
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawMinute() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(mRectMinute);
            if (canvas == null) {
                return;
            }

            canvas.drawRect(mRectMinute, mPaintMinute);
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            String text = minute / 10 + "" + minute % 10;
            mPaintText.getTextBounds(text, 0, text.length(), mRectTextMinute);
            canvas.drawText(text, mRectMinute.centerX() - mRectTextMinute.centerX(), mRectMinute.centerY() - mRectTextMinute.centerY(), mPaintText);
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
