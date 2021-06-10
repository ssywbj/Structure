package com.wiz.watchface.svendandersen.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.wiz.watchface.svendandersen.R;

import java.util.Calendar;

public class ClockPanel extends PanelView {
    private Bitmap mSecondPointer, mMinutePointer, mHourPointer;

    public ClockPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockPanel);
        Drawable drawable = typedArray.getDrawable(R.styleable.ClockPanel_secondPointer);
        if (drawable != null) {
            mSecondPointer = ((BitmapDrawable) drawable).getBitmap();
        }
        drawable = typedArray.getDrawable(R.styleable.ClockPanel_minutePointer);
        if (drawable != null) {
            mMinutePointer = ((BitmapDrawable) drawable).getBitmap();
        }
        drawable = typedArray.getDrawable(R.styleable.ClockPanel_hourPointer);
        if (drawable != null) {
            mHourPointer = ((BitmapDrawable) drawable).getBitmap();
        }
        typedArray.recycle();

        this.init();
    }

    public void init() {
        if (mSecondPointer == null) {
            setDefaultTime(10, 10, TIME_NONE);
        } else {
            setDefaultTime(20, 10, TIME_NONE);
        }
        needAppearAnimPointer();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            if (mSecondPointer == null) {
                unregisterTimeTickReceiver();
            } else {
                unregisterSecondTicker();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());

        if (mHourPointer != null) {
            canvas.save();
            canvas.rotate(mHourRatio * 360);
            canvas.drawBitmap(mHourPointer, -mHourPointer.getWidth() / 2f, -mHourPointer.getHeight() / 2f, null);
            canvas.restore();
        }
        if (mMinutePointer != null) {
            canvas.save();
            canvas.rotate(mMinuteRatio * 360);
            canvas.drawBitmap(mMinutePointer, -mMinutePointer.getWidth() / 2f, -mMinutePointer.getHeight() / 2f, null);
            canvas.restore();
        }
        if (mSecondPointer != null) {
            canvas.save();
            canvas.rotate(mSecondRatio * 360);
            canvas.drawBitmap(mSecondPointer, -mSecondPointer.getWidth() / 2f, -mSecondPointer.getHeight() / 2f, null);
            canvas.restore();
        }
        canvas.restore();
    }

    @Override
    protected void onAppearAnimFinished() {
        if (mSecondPointer == null) {
            registerTimeTickReceiver();
            updateTime();
            invalidate();
        } else {
            registerSecondTicker();
        }
    }

    @Override
    protected void onTimeTick() {
        final float scale = 360 / 24f;
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        float degrees = (hourOfDay + minute / 60f) * scale;
        setRotateDegrees(-degrees);
        super.onTimeTick();
    }

    @Override
    protected void onTimeChanged() {
        this.onTimeTick();
    }

}