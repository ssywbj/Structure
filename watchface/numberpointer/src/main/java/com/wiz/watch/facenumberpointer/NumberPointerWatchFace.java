package com.wiz.watch.facenumberpointer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.NumberBeatView;

public class NumberPointerWatchFace extends NumberBeatView {
    private NumberPointerBitmapManager mBitmapManager;

    public NumberPointerWatchFace(Context context, boolean isEditMode) {
        super(context, isEditMode);
        this.init();
    }

    public NumberPointerWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        mBitmapManager = new NumberPointerBitmapManager(getContext());
        setDefaultTime(10, 10, 30);
        setNeedPropertyAnim(true);
    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));
        canvas.translate(mPointScreenCenter.x, mPointScreenCenter.y);

        int color = ContextCompat.getColor(getContext(), R.color.number_color);
        Bitmap bitmap = mBitmapManager.getNumber(mHour / 10, color);
        canvas.drawBitmap(bitmap, -bitmap.getWidth(), -bitmap.getHeight(), null);
        bitmap = mBitmapManager.getNumber(mHour % 10, color);
        canvas.drawBitmap(bitmap, 0, -bitmap.getHeight(), null);
        bitmap = mBitmapManager.getNumber(mMinute / 10, color);
        canvas.drawBitmap(bitmap, -bitmap.getWidth(), 0, null);
        bitmap = mBitmapManager.getNumber(mMinute % 10, color);
        canvas.drawBitmap(bitmap, 0, 0, null);

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.pointer_hour);
        canvas.rotate(mHourAnimatorValue * 360);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.pointer_minute);
        canvas.rotate(mMinuteAnimatorValue * 360);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.pointer_second);
        canvas.rotate(mSecondAnimatorValue * 360);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();
    }

    @Override
    protected void onAppearanceAnimFinished() {
        super.onAppearanceAnimFinished();
        startSecondPointerAnim();
    }

}
