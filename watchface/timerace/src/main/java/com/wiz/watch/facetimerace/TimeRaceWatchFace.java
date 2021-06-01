package com.wiz.watch.facetimerace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

import java.util.Calendar;

public class TimeRaceWatchFace extends FaceAnimView {
    protected final RectF mRectF = new RectF();
    private final Rect mRect = new Rect();
    private final Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BitmapManager2 mBitmapManager;
    private int mBatteryLevel;

    public TimeRaceWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public TimeRaceWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);

        setDefaultTime(10, 10, 0);
        needAppearAnimPointer();
        setAppendTime(false);

        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(DimenUtil.dip2px(getContext(), 22));
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setAlpha(217);//255*0.85

        mBitmapManager = new BitmapManager2(mContext);
        mBatteryLevel = getBatteryLevel();
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

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterSecondTicker();
            unregisterBatteryChangeReceiver();
        }
    }

    @Override
    protected void onBatteryChange(int level) {
        mBatteryLevel = level;
    }

    @Override
    public void updateTime() {
        mHour = DateUtil.getHour(mContext);
        Calendar calendar = Calendar.getInstance();
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);

        mHourRatio = (mHour % 12 + mMinute / 60f) / 12;
        mMinuteRatio = mMinute / 60f;
        mSecondRatio = mSecond / 60f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapManager.get(R.drawable.face_panel), null, mRectF, null);

        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());
        mRect.setEmpty();
        final String week = DateUtil.getWeekText(mContext).toUpperCase();
        mPaintText.getTextBounds(week, 0, week.length(), mRect);
        canvas.drawText(week, -mRect.width() / 2f, -mRectF.height() / 3.9f, mPaintText);
        final String date = this.getDateText();
        mRect.setEmpty();
        mPaintText.getTextBounds(date, 0, date.length(), mRect);
        canvas.drawText(date, -mRect.width() / 2f, mRectF.height() / 3.2f, mPaintText);
        canvas.restore();

        canvas.save();
        Bitmap bitmap = mBitmapManager.get(R.drawable.second_panel);
        canvas.translate(0, mRectF.centerY());
        canvas.drawBitmap(bitmap, 0, -bitmap.getHeight() / 2f, null);
        if (!mIsDimMode) {
            canvas.translate(bitmap.getWidth() / 2f, 0);
            bitmap = mBitmapManager.get(R.drawable.second_pointer);
            canvas.rotate(mSecondRatio * 360);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        }
        canvas.restore();

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.battery_panel);
        canvas.translate(getWidth() - bitmap.getWidth(), mRectF.centerY());
        canvas.drawBitmap(bitmap, 0, -bitmap.getHeight() / 2f, null);
        canvas.translate(bitmap.getWidth() / 2f, 0);
        bitmap = mBitmapManager.get(R.drawable.second_pointer);
        final float startDegree = -60, endDegree = 240, showDegrees = endDegree - startDegree;
        final float scaleDegree = showDegrees / 100, rotateDegrees = startDegree + (showDegrees - mBatteryLevel * scaleDegree);
        canvas.rotate(rotateDegrees);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();

        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());
        canvas.save();
        canvas.rotate(mHourRatio * 360);
        bitmap = mBitmapManager.get(R.drawable.hour_pointer);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();
        canvas.save();
        canvas.rotate(mMinuteRatio * 360);
        bitmap = mBitmapManager.get(R.drawable.minute_pointer);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);
        canvas.restore();
        canvas.restore();
    }

    private String getDateText() {
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        stringBuilder.append(calendar.get(Calendar.YEAR)).append(".");
        int month = calendar.get(Calendar.MONTH) + 1;
        stringBuilder.append(month / 10).append(month % 10).append(".");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        stringBuilder.append(day / 10).append(day % 10);
        return stringBuilder.toString();
    }

    @Override
    protected void onAppearAnimFinished() {
        registerSecondTicker();
        registerBatteryChangeReceiver();
    }

}
