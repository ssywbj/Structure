package com.wiz.watchface.joker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.joker.R;
import com.wiz.watchface.joker.WizLunar;

import java.util.Calendar;

public class JokerWatchFace extends FaceAnimView {
    public static final float MOON_SCALE = 180 / 30f;
    protected final RectF mRectF = new RectF();
    private final RectF mRectFLeft = new RectF();
    private final RectF mRectFRight = new RectF();
    private final RectF mRectFMoon = new RectF();
    private BitmapManager2 mBitmapManager;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mLunarDayOfMonth;

    public JokerWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public JokerWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);

        needAppearAnimPointer();
        setDefaultTime(12, 0, TIME_NONE);
        setAppendTime(false);

        mBitmapManager = new BitmapManager2(mContext);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setAlpha(80);

        this.getLunarDayOfMonth();
    }

    private void getLunarDayOfMonth() {
        int[] lunar = WizLunar.solarToLunar(System.currentTimeMillis());
        if (lunar == null || lunar.length < 2) {
            mLunarDayOfMonth = 0;
        } else {
            mLunarDayOfMonth = lunar[2];
            StringBuilder stringBuilder = new StringBuilder();
            for (int i : lunar) {
                stringBuilder.append(i).append(" ");
            }
            Log.i(mTAG, "lunar: " + stringBuilder + ", lunar day of month: " + mLunarDayOfMonth);
        }
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
        }
    }

    @Override
    public void updateTime() {
        mHour = DateUtil.getHour(mContext);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);

        mHourRatio = (mHour % 12 + mMinute / 60f) / 12;
        mMinuteRatio = mMinute / 60f;
    }

    @Override
    protected void onTimeChanged() {
        super.onTimeChanged();
        this.getLunarDayOfMonth();
    }

    @Override
    protected void onAppearAnimFinished() {
        updateTime();
        invalidate();
        registerTimeTickReceiver();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap;
        if (mIsDimMode) {
            bitmap = mBitmapManager.get(R.drawable.w_idle_bg);
        } else {
            bitmap = mBitmapManager.get(R.drawable.v_clock_bg);
            canvas.drawBitmap(bitmap, null, mRectF, null);

            canvas.save();
            canvas.translate(mRectFMoon.centerX(), mRectFMoon.centerY());
            canvas.rotate(mLunarDayOfMonth * MOON_SCALE);
            bitmap = mBitmapManager.get(R.drawable.moon_phase);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
            canvas.restore();

            bitmap = mBitmapManager.get(R.drawable.v_clock_cover);
        }
        canvas.drawBitmap(bitmap, null, mRectF, null);

        //canvas.drawRect(mRectFLeft, mPaint);
        canvas.save();
        canvas.translate(mRectFLeft.centerX(), mRectFLeft.centerY());
        canvas.rotate(mHourRatio * 360);
        if (mIsDimMode) {
            bitmap = mBitmapManager.get(R.drawable.v_clock_hour_idle);
        } else {
            bitmap = mBitmapManager.get(R.drawable.v_clock_hour);
        }
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        //canvas.drawRect(mRectFRight, mPaint);
        canvas.save();
        canvas.translate(mRectFRight.centerX(), mRectFRight.centerY());
        canvas.rotate(mMinuteRatio * 360);
        if (mIsDimMode) {
            bitmap = mBitmapManager.get(R.drawable.v_clock_minute_idle);
        } else {
            bitmap = mBitmapManager.get(R.drawable.v_clock_minute);
        }
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        //canvas.drawRect(mRectFMoon, mPaint);
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

        mRectFLeft.left = 55;
        mRectFLeft.top = 117.5f;
        mRectFLeft.right = mRectFLeft.left + 118;
        mRectFLeft.bottom = mRectFLeft.top + 117;

        mRectFRight.right = width - mRectFLeft.left;
        mRectFRight.top = mRectFLeft.top;
        mRectFRight.left = mRectFRight.right - mRectFLeft.width();
        mRectFRight.bottom = mRectFRight.top + mRectFLeft.height();

        final int rectWidth = 248;
        mRectFMoon.bottom = height - 26;
        mRectFMoon.top = mRectFMoon.bottom - rectWidth;
        mRectFMoon.left = (width - rectWidth) / 2f;
        mRectFMoon.right = mRectFMoon.left + rectWidth;
    }

}
