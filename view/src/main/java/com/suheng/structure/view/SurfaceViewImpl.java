package com.suheng.structure.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

    private final Rect mRectClock = new Rect();
    private final Rect mRectSecondPointer = new Rect();
    private final Rect mRectMinutePointer = new Rect();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Second Runnable, Second Runnable");
            drawSecond();

            drawSecondPointer();

            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            postDelayed(mRunnable, delayMillis);
        }
    };

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Time Changed Receiver: action = " + intent.getAction());
            drawMinute();
            drawMinutePointer();
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
        mPaintText.setColor(Color.GREEN);

        mPaintSecond = new Paint();
        mPaintSecond.setStyle(Paint.Style.FILL);
        mPaintSecond.setColor(Color.YELLOW);

        mPaintMinute = new Paint(mPaintSecond);
        mPaintMinute.setColor(Color.RED);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: holder = " + holder);
        mIsRunning = true;
        //new Thread(this).start();
        //this.draw();

        setZOrderOnTop(true);//设置画布  背景透明
        setZOrderMediaOverlay(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);

        this.drawBg(holder);

        post(mRunnable);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        getContext().registerReceiver(mTimeChangedReceiver, intentFilter);
        this.drawMinute();

        this.drawCircle(holder);
        this.drawMinutePointer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: format = " + format + ", width = " + width + ", height = " + height);
        final int rectWidth = 100, rectHeight = 80;
        mRectSecond.set((width - rectWidth) / 2, 5, (width + rectWidth) / 2, 5 + rectHeight);
        mRectMinute.set((width - rectWidth) / 2, 10 + rectHeight, (width + rectWidth) / 2, 10 + 2 * rectHeight);

        final int radius = 300, secondPointerWidth = 6, minutePointerWidth = 10;
        mRectClock.set((width - radius) / 2, 20 + 2 * rectHeight, (width + radius) / 2, 20 + 2 * rectHeight + radius);
        mRectSecondPointer.set((width - secondPointerWidth) / 2, 20 + 2 * rectHeight, (width + secondPointerWidth) / 2, 20 + 2 * rectHeight + radius / 2);
        mRectMinutePointer.set((width - minutePointerWidth) / 2, 60 + 2 * rectHeight, (width + minutePointerWidth) / 2, 20 + 2 * rectHeight + radius / 2);

        this.drawBg(holder);
        this.drawSecond();
        this.drawMinute();

        this.drawCircle(holder);
        this.drawSecondPointer();
        this.drawMinutePointer();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed, surfaceDestroyed");
        mIsRunning = false;
        removeCallbacks(mRunnable);
        getContext().unregisterReceiver(mTimeChangedReceiver);
    }

    /*@Override
    public boolean gatherTransparentRegion(Region region) {
        region.op(mRectSecond.left, mRectSecond.top, mRectSecond.right, mRectSecond.bottom, Region.Op.UNION);
        return false;
    }*/

    @Override
    public void run() {
        while (mIsRunning) {
            drawSecond();
        }
    }

    private void drawBg(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            if (canvas == null) {
                return;
            }

            canvas.drawColor(Color.BLUE);
            //Rect rect = new Rect(0, 0, getWidth(), getHeight());
            //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.beauty), null, rect, null);
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

            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.drawPaint(paint);
            /*paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);*/

            int saveLayer = canvas.saveLayer(mRectSecond.left, mRectSecond.top, mRectSecond.right, mRectSecond.bottom, null);
            //canvas.drawRect(mRectSecond, mPaintSecond);
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);
            String text = second / 10 + "" + second % 10;
            mPaintText.getTextBounds(text, 0, text.length(), mRectTextSecond);
            canvas.drawText(text, mRectSecond.centerX() - mRectTextSecond.centerX(), mRectSecond.centerY() - mRectTextSecond.centerY(), mPaintText);
            canvas.restoreToCount(saveLayer);
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

            //canvas.drawRect(mRectMinute, mPaintMinute);
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

    private void drawCircle(SurfaceHolder holder) {
        /*Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(mRectCircle);
            if (canvas == null) {
                return;
            }

            canvas.drawCircle(mRectCircle.centerX(), mRectCircle.centerY()
                    , mRectCircle.width() / 2f, mPaintText);
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }*/
    }

    private void drawSecondPointer() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(mRectClock);
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
                surfaceHolder.unlockCanvasAndPost(canvas);
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
