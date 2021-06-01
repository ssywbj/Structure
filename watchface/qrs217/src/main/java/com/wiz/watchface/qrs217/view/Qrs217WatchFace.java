package com.wiz.watchface.qrs217.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.qrs217.R;

import java.util.Calendar;

public class Qrs217WatchFace extends FaceAnimView {
    private static final float SCALE_DEGREES = 7.5f;
    private static final float START_DEGREES = -16 * SCALE_DEGREES;
    private final RectF mRectF = new RectF();
    private BitmapManager2 mBitmapManager;
    private int mCurrentDay;
    private float mDayDegrees;
    private ValueAnimator mAppearAnimator;

    public Qrs217WatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public Qrs217WatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);

        setDefaultTime(8, 36, 35);
        needAppearAnimPointer();
        setAppendTime(true);

        mBitmapManager = new BitmapManager2(mContext);

        mAppearAnimator = ValueAnimator.ofFloat();
        mAppearAnimator.setDuration(ANIM_DURATION);
        mAppearAnimator.setInterpolator(new LinearInterpolator());

        post(this::updateDay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.calcDimens(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.calcDimens(w, h);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterTimeTickReceiver();
            stopSecondPointerAnim();
        }
    }

    @Override
    protected void onTimeTick() {
        this.updateDay();
    }

    @Override
    protected void onTimeChanged() {
        super.onTimeChanged();
        this.updateDay();
    }

    private void updateDay() {
        if (mIsDimMode) {
            return;
        }

        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (mCurrentDay != day) {
            if (mIsEditMode) {
                mDayDegrees = START_DEGREES + SCALE_DEGREES * day;

                invalidate();
            } else {
                if (mAppearAnimator.isRunning()) {
                    mAppearAnimator.cancel();
                }

                float startDegrees = START_DEGREES + SCALE_DEGREES * mCurrentDay;
                float endDegrees = START_DEGREES + SCALE_DEGREES * day;
                mAppearAnimator.setFloatValues(startDegrees, endDegrees);
                mAppearAnimator.addUpdateListener(animation -> {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue instanceof Float) {
                        mDayDegrees = (float) animatedValue;

                        invalidate();
                    }
                });
                mAppearAnimator.start();
            }

            mCurrentDay = day;
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        this.onTimeChanged();
        registerTimeTickReceiver();
        startSecondPointerAnim();
    }

    public void destroy() {
        mBitmapManager.clear();
        releaseAnim(mAppearAnimator);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsDimMode) {
            canvas.drawBitmap(mBitmapManager.get(R.drawable.dim_face_panel), null, mRectF, null);
        } else {
            canvas.drawBitmap(mBitmapManager.get(R.drawable.face_panel), null, mRectF, null);
        }

        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        Bitmap bitmap;
        if (!mIsDimMode) {
            bitmap = mBitmapManager.get(R.drawable.pointer_day);
            canvas.save();
            canvas.rotate(mDayDegrees);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
            canvas.restore();
        }

        canvas.save();
        canvas.rotate(mHourRatio * 360);
        bitmap = mBitmapManager.get(R.drawable.pointer_hour);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinuteRatio * 360);
        bitmap = mBitmapManager.get(R.drawable.pointer_minute);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        bitmap = mBitmapManager.get(R.drawable.dot);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);

        if (!mIsDimMode) {
            canvas.translate(0, mRectF.height() / 4f - DimenUtil.dip2px(mContext, 7));

            canvas.save();
            canvas.rotate(mSecondRatio * 360);
            bitmap = mBitmapManager.get(R.drawable.pointer_second);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
            canvas.restore();

            bitmap = mBitmapManager.get(R.drawable.dot_pointer_second);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        }
    }

    private void calcDimens(int width, int height) {
        RectF rectF = new RectF();
        rectF.left = getPaddingStart();
        rectF.top = getPaddingTop();
        rectF.right = width - getPaddingEnd();
        rectF.bottom = height - getPaddingBottom();

        float radius = Math.min(rectF.width(), rectF.height()) / 2;
        mRectF.setEmpty();
        mRectF.left = rectF.centerX() - radius;
        mRectF.top = rectF.centerY() - radius;
        mRectF.right = rectF.centerX() + radius;
        mRectF.bottom = rectF.centerY() + radius;
    }

}
