package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class TimeDemoView extends View {
    private static final String TAG = TimeDemoView.class.getSimpleName();
    private final Paint mPaint = new Paint();
    private final Rect mRectSmall = new Rect(), mRectBig = new Rect();
    private boolean mIsSmallRect, mIsBigRect;

    private final Runnable mSmallRunnable = new Runnable() {
        @Override
        public void run() {
            //invalidate();
            mIsSmallRect = true;
            postInvalidate(mRectSmall.left, mRectSmall.top, mRectSmall.right, mRectSmall.bottom);
            //postInvalidate(0, 10, 20, 30);

            final long delayMillis = 1000 - System.currentTimeMillis() % 1000;
            postDelayed(mSmallRunnable, delayMillis);
        }
    };

    private final Runnable mBigRunnable = new Runnable() {
        @Override
        public void run() {
            mIsBigRect = true;
            postInvalidate(mRectBig.left, mRectBig.top, mRectBig.right, mRectBig.bottom);
            //postInvalidate(0, 10, 20, 30);

            final long delayMillis = 3000 - System.currentTimeMillis() % 3000;
            postDelayed(mBigRunnable, delayMillis);
        }
    };

    public TimeDemoView(Context context) {
        super(context);
        this.init();
    }

    public TimeDemoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public TimeDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mSmallRunnable);
        post(mBigRunnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint.setTextSize(40);
        String text = "88";
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        int left = (int) ((w - rect.width()) / 2f);
        int top = 15;
        mRectSmall.set(left, top, left + rect.width(), top += rect.height());

        mPaint.setTextSize(60);
        mPaint.getTextBounds(text, 0, text.length(), rect);
        top += 30;
        mRectBig.set(left, top, left + rect.width(), top + rect.height());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mSmallRunnable);
        removeCallbacks(mBigRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsSmallRect) {
            mIsSmallRect = false;

            //canvas.save();
            //canvas.clipRect(mRectSmall);
            //canvas.drawColor(Color.RED);
            int second = Calendar.getInstance().get(Calendar.SECOND);
            String text = second / 10 + "" + second % 10;
            Log.d(TAG, "small runnable second: " + text);
            mPaint.setTextSize(40);
            canvas.drawText(text, mRectSmall.left, mRectSmall.bottom, mPaint);
            //canvas.restore();
        }

        if (mIsBigRect) {
            mIsBigRect = false;

            //canvas.save();
            //canvas.saveLayer(mRectBig.left, mRectBig.top, mRectBig.left, mRectBig.bottom,mPaint);
            //canvas.clipRect(mRectBig);
            //canvas.drawColor(Color.BLUE);
            int second = Calendar.getInstance().get(Calendar.SECOND);
            String text = second / 10 + "" + second % 10;
            Log.v(TAG, "big runnable second: " + text);
            mPaint.setTextSize(60);
            canvas.drawText(text, mRectBig.left, mRectBig.bottom, mPaint);
            //canvas.restore();
        }

        /*final long delayMillis = 1000 - System.currentTimeMillis() % 1000;
        postInvalidateDelayed(delayMillis);*/
    }

}
