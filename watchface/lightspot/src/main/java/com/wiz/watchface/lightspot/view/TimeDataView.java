package com.wiz.watchface.lightspot.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.utils.NumberUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.lightspot.R;

import java.util.Calendar;

public class TimeDataView extends FaceAnimView {
    private BitmapManager2 mBitmapManager;
    private Paint mPaint;
    private float mRatio, mDateRatio, mDataRatio;
    private int mDateWidth, mBatteryLevel;
    private SportDataManager mSportDataManager;
    private int mMinuteColor;

    public TimeDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速

        needAppearAnimNumber();
        setDefaultTime(8, 36, TIME_NONE);
        mSportDataManager = new SportDataManager(mContext);

        mBitmapManager = new BitmapManager2(mContext);
        mRatio = Float.parseFloat(mContext.getString(R.string.ratio));
        mDateRatio = mRatio / 4f;
        mDataRatio = mRatio / 5f;
        mDateWidth = this.getNumberBitmap(0, mDateRatio).getWidth() * 4
                + mBitmapManager.get(R.drawable.minus_sign, mDateRatio).getWidth();

        mMinuteColor = ContextCompat.getColor(mContext, R.color.minute);
        mPaint = new Paint();
        mPaint.setColor(mMinuteColor);
        mPaint.setShadowLayer(DimenUtil.dip2px(mContext, 8), 0
                , -DimenUtil.dip2px(mContext, 4), 0x7F000000); //-y：向上偏移

        mSportDataManager.setHeartRateListener((selfChange, uri) -> postInvalidate());
        mBatteryLevel = getBatteryLevel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(() -> {
            mSportDataManager.queryStepAndCalories();
            mSportDataManager.queryHeartRate();
        }, 200);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        //Log.d(mTAG, "onVisibilityChanged: " + visible + ", edit mode: " + mIsEditMode + ", this: " + this/*, new Exception()*/);
        if (visible) {
            mSportDataManager.registerContentHeartRate();
        } else {
            mSportDataManager.unregisterContentHeartRate();

            unregisterBatteryChangeReceiver();
            unregisterTimeTickReceiver();
        }
    }

    @Override
    protected void onTimeTick() {
        mSportDataManager.queryStepAndCalories();
        super.onTimeTick();
    }

    @Override
    protected void onBatteryChange(int level) {
        mBatteryLevel = level;

        invalidate();
    }

    @Override
    protected void onAppearAnimFinished() {
        registerBatteryChangeReceiver();
        registerTimeTickReceiver();

        updateTime();
        invalidate();
    }

    public void destroy() {
        mBitmapManager.clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        canvas.save();
        canvas.translate(getWidth() / 2f, DimenUtil.dip2px(mContext, 20));
        Bitmap bitmap = this.getNumberBitmap(mHour % 10, mRatio);
        float left = -bitmap.getWidth() / 2f;
        canvas.drawBitmap(bitmap, left, 0, null);
        bitmap = this.getNumberBitmap(mHour / 10, mRatio);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, 0, null);

        canvas.translate(0, bitmap.getHeight() - bitmap.getHeight() / 8f);
        bitmap = this.getNumberBitmap(mMinute / 10, mRatio);
        left = -bitmap.getWidth() / 2f;
        canvas.drawBitmap(bitmap.extractAlpha(), left, 0, mPaint);
        left += bitmap.getWidth();
        bitmap = this.getNumberBitmap(mMinute % 10, mRatio);
        canvas.drawBitmap(bitmap.extractAlpha(), left, 0, mPaint);

        canvas.translate(-mDateWidth / 2f, bitmap.getHeight() + bitmap.getHeight() / 9f);
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int color = Color.WHITE;
        bitmap = this.getNumberBitmap(month / 10, color, mDateRatio);
        canvas.drawBitmap(bitmap, 0, 0, null);
        left = bitmap.getWidth();
        bitmap = this.getNumberBitmap(month % 10, color, mDateRatio);
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(R.drawable.minus_sign, color, mDateRatio);
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        bitmap = this.getNumberBitmap(day / 10, color, mDateRatio);
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        bitmap = this.getNumberBitmap(day % 10, color, mDateRatio);
        canvas.drawBitmap(bitmap, left, 0, null);
        canvas.restore();

        canvas.save();
        //心率
        float marginLeft = mContext.getResources().getDimension(R.dimen.data_margin_left);
        canvas.translate(marginLeft, getHeight() - this.getNumberBitmap(8, Color.WHITE, mDataRatio).getHeight() - DimenUtil.dip2px(mContext, 15));
        bitmap = mBitmapManager.get(R.drawable.icon_heart_rate, mMinuteColor);
        canvas.drawBitmap(bitmap, 0, 0, null);
        left = bitmap.getWidth() + DimenUtil.dip2px(mContext, 6);
        int[] units = NumberUtil.obtainUnits(mSportDataManager.getHeartRate());
        for (int unit : units) {
            bitmap = this.getNumberBitmap(unit, mDataRatio);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
        }

        //卡路里
        canvas.translate(0, -bitmap.getHeight() - DimenUtil.dip2px(mContext, 20));
        bitmap = mBitmapManager.get(R.drawable.icon_kcal, mMinuteColor);
        left = 0;
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth() + DimenUtil.dip2px(mContext, 6);
        units = NumberUtil.obtainUnits(mSportDataManager.getCalories());
        for (int unit : units) {
            bitmap = this.getNumberBitmap(unit, mDataRatio);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
        }
        canvas.restore();

        canvas.save();
        //电量
        canvas.translate(getWidth() / 2f + marginLeft * 0.6f, getHeight() - this.getNumberBitmap(8, Color.WHITE, mDataRatio).getHeight() - DimenUtil.dip2px(mContext, 15));
        bitmap = mBitmapManager.get(R.drawable.icon_battery, mMinuteColor);
        canvas.drawBitmap(bitmap, 0, 0, null);
        left = bitmap.getWidth() + DimenUtil.dip2px(mContext, 6);

        units = NumberUtil.obtainUnits(mBatteryLevel);
        for (int unit : units) {
            bitmap = this.getNumberBitmap(unit, mDataRatio);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
        }
        bitmap = mBitmapManager.get(R.drawable.percent_sign, Color.WHITE, mDataRatio);
        canvas.drawBitmap(bitmap, left, 0, null);

        //步数
        canvas.translate(0, -bitmap.getHeight() - DimenUtil.dip2px(mContext, 20));
        bitmap = mBitmapManager.get(R.drawable.icon_steps, mMinuteColor);
        left = 0;
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth() + DimenUtil.dip2px(mContext, 6);
        units = NumberUtil.obtainUnits(mSportDataManager.getSteps());
        for (int unit : units) {
            bitmap = this.getNumberBitmap(unit, mDataRatio);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
        }
        canvas.restore();
    }

    private Bitmap getNumberBitmap(int number, float scale) {
        return mBitmapManager.get(R.drawable.number_0 + number, scale);
    }

    private Bitmap getNumberBitmap(int number, int color, float scale) {
        return mBitmapManager.get(R.drawable.number_0 + number, color, scale);
    }

    public void setMinuteColor(int minuteColor) {
        mMinuteColor = minuteColor;

        mPaint.setColor(mMinuteColor);
    }

}
