package com.wiz.watch.faceshuttle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.wiz.watch.faceshuttle.R;

public class ClockPanel extends PanelView {
    private Bitmap mSecondPointer, mMinutePointer, mHourPointer;

    public ClockPanel(Context context) {
        super(context);
        this.init();
    }

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
        Log.d(mTAG, "hour pointer: " + mHourPointer + ", minute pointer: " + mMinutePointer
                + ", second pointer: " + mSecondPointer);

        this.init();
    }

    public void init() {
        setDefaultTime(10, 10, 30);
        setNeedPropertyAnim(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());
        if (mHourPointer != null) {
            canvas.save();
            canvas.rotate(mHourAnimatorValue * 360);
            canvas.drawBitmap(mHourPointer, -mHourPointer.getWidth() / 2f, -mHourPointer.getHeight() / 2f, null);
            canvas.restore();
        }
        if (mMinutePointer != null) {
            canvas.save();
            canvas.rotate(mMinuteAnimatorValue * 360);
            canvas.drawBitmap(mMinutePointer, -mMinutePointer.getWidth() / 2f, -mMinutePointer.getHeight() / 2f, null);
            canvas.restore();
        }
        if (mSecondPointer != null) {
            canvas.save();
            canvas.rotate(mSecondAnimatorValue * 360);
            canvas.drawBitmap(mSecondPointer, -mSecondPointer.getWidth() / 2f, -mSecondPointer.getHeight() / 2f, null);
            canvas.restore();
        }
        canvas.restore();
    }

    public void setSecondPointer(Bitmap secondPointer) {
        mSecondPointer = secondPointer;
    }

    public void setMinutePointer(Bitmap minutePointer) {
        mMinutePointer = minutePointer;
    }

    public void setHourPointer(Bitmap hourPointer) {
        mHourPointer = hourPointer;
    }

    @Override
    protected void onAppearanceAnimFinished() {
        super.onAppearanceAnimFinished();
        startSecondPointerAnim();
    }
}
