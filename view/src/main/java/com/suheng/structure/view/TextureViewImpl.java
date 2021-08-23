package com.suheng.structure.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TextureViewImpl extends TextureView implements TextureView.SurfaceTextureListener {
    public static final String TAG = TextureViewImpl.class.getSimpleName();
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private Paint mPaintText;

    private Paint mPaintSecond;
    private final Rect mRectSecond = new Rect();
    private final Rect mRectTextSecond = new Rect();

    private Paint mPaintMinute;
    private final Rect mRectMinute = new Rect();
    private final Rect mRectTextMinute = new Rect();

    private final Rect mRectClock = new Rect();
    private final Rect mRectSecondPointer = new Rect();
    private final Rect mRectMinutePointer = new Rect();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            drawSecond();

            //drawSecondPointer();

            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            postDelayed(mRunnable, delayMillis);
        }
    };

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawMinute();
            drawMinutePointer();
        }
    };

    public TextureViewImpl(Context context) {
        super(context);
        this.init();
    }

    public TextureViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setSurfaceTextureListener(this);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(50);
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        //mPaintText.setColor(Color.GREEN);

        mPaintSecond = new Paint();
        mPaintSecond.setStyle(Paint.Style.FILL);
        mPaintSecond.setColor(Color.BLUE);

        mPaintMinute = new Paint(mPaintSecond);
        mPaintMinute.setColor(Color.RED);

        //setOpaque(true);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable, w:" + width + ", h:" + height);
        final int rectWidth = 100, rectHeight = 80;
        mRectSecond.set((width - rectWidth) / 2, 5, (width + rectWidth) / 2, 5 + rectHeight);
        mRectMinute.set((width - rectWidth) / 2, 10 + rectHeight, (width + rectWidth) / 2, 10 + 2 * rectHeight);

        final int radius = 300, secondPointerWidth = 6, minutePointerWidth = 10;
        mRectClock.set((width - radius) / 2, 20 + 2 * rectHeight, (width + radius) / 2, 20 + 2 * rectHeight + radius);
        mRectSecondPointer.set((width - secondPointerWidth) / 2, 20 + 2 * rectHeight, (width + secondPointerWidth) / 2, 20 + 2 * rectHeight + radius / 2);
        mRectMinutePointer.set((width - minutePointerWidth) / 2, 60 + 2 * rectHeight, (width + minutePointerWidth) / 2, 20 + 2 * rectHeight + radius / 2);

        this.drawBg();

        this.drawMinute();

        /*this.drawCircle();
        this.drawMinutePointer();*/
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged, w:" + width + ", h:" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        //removeCallbacks(mRunnable);
        //getContext().unregisterReceiver(mTimeChangedReceiver);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "onSurfaceTextureUpdated " + isAvailable() + ", " + isActivated() + ", " + surface.isReleased());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.d(TAG, "onVisibilityChanged, visibility: " + visibility);
        if (visibility == VISIBLE) {
            post(mRunnable);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            getContext().registerReceiver(mTimeChangedReceiver, intentFilter);
        } else {
            removeCallbacks(mRunnable);
            getContext().unregisterReceiver(mTimeChangedReceiver);
        }
    }

    private void drawBg() {
        Canvas canvas = null;
        try {
            canvas = lockCanvas(null);
            if (canvas == null) {
                return;
            }
            Log.v(TAG, "Bg Canvas: " + canvas);

            canvas.drawColor(Color.RED);
            //Rect rect = new Rect(0, 0, getWidth(), getHeight());
            //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.beauty), null, rect, null);
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawCircle() {
        /*Canvas canvas = null;
        try {
            canvas = lockCanvas(mRectCircle);
            if (canvas == null) {
                return;
            }

            canvas.drawCircle(mRectCircle.centerX(), mRectCircle.centerY()
                    , mRectCircle.width() / 2f, mPaintText);
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
        }*/
    }

    private void drawSecond() {
        Canvas canvas = null;
        try {
            canvas = lockCanvas(mRectSecond);
            if (canvas == null) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);
            Log.d(TAG, "Second Canvas: " + canvas + ", second: " + second);
            String text = second / 10 + "" + second % 10;
            mPaintText.getTextBounds(text, 0, text.length(), mRectTextSecond);

            int saveLayer = canvas.saveLayer(mRectSecond.left, mRectSecond.top, mRectSecond.right, mRectSecond.bottom, null);
            mPaintText.setColor(Color.TRANSPARENT);
            canvas.drawRect(mRectSecond, mPaintText);
            mPaintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaintText.setColor(Color.GREEN);
            canvas.drawText(text, mRectSecond.centerX() - mRectTextSecond.centerX(), mRectSecond.centerY() - mRectTextSecond.centerY(), mPaintText);
            mPaintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.restoreToCount(saveLayer);
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawSecondPointer() {
        Canvas canvas = null;
        try {
            canvas = lockCanvas(mRectClock);
            if (canvas == null) {
                return;
            }

            canvas.drawCircle(mRectClock.centerX(), mRectClock.centerY()
                    , mRectClock.width() / 2f, mPaintText);

            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);
            float degrees = 1f * second / 60 * 360;
            canvas.save();
            canvas.rotate(degrees, mRectClock.centerX(), mRectClock.centerY());
            canvas.drawRect(mRectSecondPointer, mPaintMinute);
            canvas.restore();

            int minute = calendar.get(Calendar.MINUTE);
            canvas.save();
            degrees = 1f * minute / 60 * 360;
            canvas.rotate(degrees, mRectClock.centerX(), mRectClock.centerY());
            canvas.drawRect(mRectMinutePointer, mPaintMinute);
            canvas.restore();
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawMinute() {
        Canvas canvas = null;
        try {
            canvas = lockCanvas(mRectMinute);
            if (canvas == null) {
                return;
            }
            Log.i(TAG, "Minute Canvas: " + canvas);

            //canvas.drawRect(mRectMinute, mPaintMinute);
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            String text = minute / 10 + "" + minute % 10;
            mPaintText.getTextBounds(text, 0, text.length(), mRectTextMinute);
            canvas.drawText(text, mRectMinute.centerX() - mRectTextMinute.centerX(), mRectMinute.centerY() - mRectTextMinute.centerY(), mPaintText);
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawMinutePointer() {
        /*SurfaceHolder surfaceHolder = getHolder();
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(mRectCircle);
            if (canvas == null) {
                return;
            }

            canvas.drawCircle(mRectCircle.centerX(), mRectCircle.centerY()
                    , mRectCircle.width() / 2f, mPaintText);

            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            float degrees = 1f * minute / 60 * 360;
            canvas.rotate(degrees, mRectCircle.centerX(), mRectCircle.centerY());
            canvas.drawRect(mRectMinutePointer, mPaintMinute);
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }*/
    }

}
