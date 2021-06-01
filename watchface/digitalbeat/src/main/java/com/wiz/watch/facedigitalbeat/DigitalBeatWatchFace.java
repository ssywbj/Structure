package com.wiz.watch.facedigitalbeat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;

public class DigitalBeatWatchFace extends WatchFaceView {
    private static final int SECONDS_SCALE = 60;
    private static final int MSG_UPDATE_SECOND = 1;
    private static final int ANIM_DURATION = 500;
    private final LinearInterpolator mInterpolator = new LinearInterpolator();

    private static final int PER_SCREEN_SECOND = 5;
    private float mWidthSecond, mUnitOffsetSecond, mTotalOffsetSecond;
    private ValueAnimator mAnimSecond;
    private float mAnimValueSecond;
    private int mCurrentSecond;

    private static final int PER_SCREEN_MINUTE = 2;
    private float mWidthMinute, mUnitOffsetMinute, mTotalOffsetMinute;
    private ValueAnimator mAnimMinute;
    private float mAnimValueMinute;
    private int mCurrentMinute;

    private int mHoursScale;
    private static final int PER_SCREEN_HOUR = 3;
    private float mWidthHour, mUnitOffsetHour, mTotalOffsetHour;
    private ValueAnimator mAnimHour;
    private float mAnimValueHour;
    private int mCurrentHour;

    private DigitalBeatBitmapManager mBitmapManager;
    private Paint mPaintShade;
    private LinearGradient mShaderLeft, mShaderRight;

    public DigitalBeatWatchFace(Context context) {
        super(context);
        this.init();
    }

    public DigitalBeatWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapManager = new DigitalBeatBitmapManager(mContext);

        mPaintShade = new Paint();
        mPaintShade.setAntiAlias(true);
        mPaintShade.setColor(Color.BLACK);
        mPaintShade.setStyle(Paint.Style.FILL);

        mWidthSecond = 2 * mContext.getResources().getDimension(R.dimen.second_width);
        mCurrentSecond = Calendar.getInstance().get(Calendar.SECOND);

        mWidthMinute = 2 * mContext.getResources().getDimension(R.dimen.minute_width);
        mCurrentMinute = Calendar.getInstance().get(Calendar.MINUTE);

        mWidthHour = 2 * mContext.getResources().getDimension(R.dimen.hour_width);
        mCurrentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        this.setHourScale();
        this.initSecondAnim();
        this.initMinuteAnim();
        this.initHourAnim();
    }

    private void setHourScale() {
        if (DateUtil.is24HourFormat(mContext)) {
            mHoursScale = 24;
        } else {
            mHoursScale = 12;
        }
    }

    private void initSecondAnim() {
        mAnimSecond = ValueAnimator.ofFloat(0, 0);
        mAnimSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValueSecond = (float) animation.getAnimatedValue();
                if (mAnimSecond != null) {
                    Log.d(mTAG, "onAnimationUpdate, mAnimValueSecond: " + mAnimValueSecond);
                    invalidate();
                }
            }
        });
        mAnimSecond.setDuration(ANIM_DURATION);
        mAnimSecond.setInterpolator(mInterpolator);
    }

    private void startSecondAnim() {
        mAnimSecond.setFloatValues(mTotalOffsetSecond, mTotalOffsetSecond += mUnitOffsetSecond);
        mAnimSecond.start();
    }

    private void initMinuteAnim() {
        mAnimMinute = ValueAnimator.ofFloat(0, 0);
        mAnimMinute.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValueMinute = (float) animation.getAnimatedValue();
            }
        });
        mAnimMinute.setDuration(ANIM_DURATION);
        mAnimMinute.setInterpolator(mInterpolator);
    }

    private void startMinuteAnim() {
        mAnimMinute.setFloatValues(mTotalOffsetMinute, mTotalOffsetMinute += mUnitOffsetMinute);
        mAnimMinute.start();
    }

    private void initHourAnim() {
        mAnimHour = ValueAnimator.ofFloat(0, 0);
        mAnimHour.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValueHour = (float) animation.getAnimatedValue();
            }
        });
        mAnimHour.setDuration(ANIM_DURATION);
        mAnimHour.setInterpolator(mInterpolator);
    }

    private void startHourAnim() {
        mAnimHour.setFloatValues(mTotalOffsetHour, mTotalOffsetHour += mUnitOffsetHour);
        mAnimHour.start();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            this.setHourScale();
            if (getHandler() != null) {
                getHandler().removeMessages(MSG_UPDATE_SECOND);
                getHandler().sendEmptyMessage(MSG_UPDATE_SECOND);
            }
        } else {
            getHandler().removeMessages(MSG_UPDATE_SECOND);
            this.destroy();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //秒钟数：每屏(PER_SCREEN_SECOND)个，共有(PER_SCREEN_SECOND - 1)间隙
        float secondOffset = (w - PER_SCREEN_SECOND * mWidthSecond) / (PER_SCREEN_SECOND - 1);
        mUnitOffsetSecond = (mWidthSecond + secondOffset);
        Log.d(mTAG, "mUnitOffsetSecond: " + mUnitOffsetSecond);

        //分钟数：每屏(PER_SCREEN_MINUTE)个，共有(PER_SCREEN_MINUTE)间隙
        float minuteOffset = (w - PER_SCREEN_MINUTE * mWidthMinute) / PER_SCREEN_MINUTE;
        mUnitOffsetMinute = (mWidthMinute + minuteOffset);
        Log.d(mTAG, "mUnitOffsetMinute: " + mUnitOffsetMinute);

        //时钟数：每屏(PER_SCREEN_HOUR)个，共有(PER_SCREEN_HOUR - 1)间隙
        float hourOffset = (w - PER_SCREEN_HOUR * mWidthHour) / (PER_SCREEN_HOUR - 1);
        mUnitOffsetHour = (mWidthHour + hourOffset);
        Log.d(mTAG, "mUnitOffsetHour: " + mUnitOffsetHour);

        mShaderLeft = new LinearGradient(0, h / 2f, w / 3f, h / 2f, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShaderRight = new LinearGradient(w / 3f * 2, h / 2f, w, h / 2f, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
    }

    @Override
    public void destroy() {
        super.destroy();
        getHandler().removeMessages(MSG_UPDATE_SECOND);
        releaseAnim(mAnimSecond);
        releaseAnim(mAnimMinute);
        releaseAnim(mAnimHour);
    }

    @Override
    protected void dispatchMsg(Message msg) {
        super.dispatchMsg(msg);
        if (getHandler() != null) {
            long delayMs = 1000 - (System.currentTimeMillis() % 1000);
            getHandler().sendEmptyMessageDelayed(MSG_UPDATE_SECOND, delayMs);
        }

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (mCurrentHour != currentHour) {
            mCurrentHour = currentHour;
            this.startHourAnim();
        }

        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        if (mCurrentMinute != currentMinute) {
            mCurrentMinute = currentMinute;
            this.startMinuteAnim();
        }

        //秒钟动画后启动，以避免“时间到下一分钟时，分钟数字变化后还会向左抖一下”的现象
        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
        if (mCurrentSecond != currentSecond) {
            mCurrentSecond = currentSecond;
            this.startSecondAnim();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));

        this.drawHours(canvas);
        this.drawMinutes(canvas);
        this.drawSeconds(canvas);
        this.drawDate(canvas);

        mPaintShade.setShader(mShaderLeft);
        canvas.drawRect(0, 0, getWidth() / 3f, getHeight(), mPaintShade);
        mPaintShade.setShader(mShaderRight);
        canvas.drawRect(getWidth() / 3f * 2, 0, getWidth(), getHeight(), mPaintShade);
    }

    private void drawSeconds(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mPointScreenCenter.y + mPointScreenCenter.y / 2);
        canvas.translate(mTotalOffsetSecond, 0);
        float x = -mUnitOffsetSecond;//减掉一个位置的距离让最左边的数字移到屏幕外面
        int number, preOffset = PER_SCREEN_SECOND - 2;
        int preSecond = mCurrentSecond - preOffset, end = mCurrentSecond + preOffset;
        Bitmap bitmap;
        for (int index = preSecond; index < end; index++) {//结构：当前秒数在中间，当前屏幕显示5个秒数，屏幕外两侧各一个，一共7个
            number = (index + SECONDS_SCALE) % SECONDS_SCALE;
            bitmap = mBitmapManager.getSecondBitmap(number, ContextCompat.getColor(mContext, R.color.second_color));
            canvas.drawBitmap(bitmap, x - mAnimValueSecond, -bitmap.getHeight() / 2f, null);
            x += mUnitOffsetSecond;
        }
        canvas.restore();
    }

    private void drawMinutes(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mPointScreenCenter.y);
        canvas.translate(-mWidthMinute / 2f - mUnitOffsetMinute + mTotalOffsetMinute, 0);
        float x = -mUnitOffsetMinute;//减掉一个位置的距离让最左边的数字移到屏幕外面
        int number, preOffset = PER_SCREEN_SECOND - 2;
        int preMinute = mCurrentMinute - preOffset, end = mCurrentMinute + preOffset;
        Bitmap bitmap;
        for (int index = preMinute; index < end; index++) {
            number = (index + SECONDS_SCALE) % SECONDS_SCALE;
            bitmap = mBitmapManager.getMinuteBitmap(number, ContextCompat.getColor(mContext, R.color.minute_color));
            canvas.drawBitmap(bitmap, x - mAnimValueMinute, -bitmap.getHeight() / 2f, null);
            x += mUnitOffsetMinute;
        }
        canvas.restore();
    }

    private void drawHours(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mPointScreenCenter.y - mPointScreenCenter.y / 1.75f);
        canvas.translate(mTotalOffsetHour, 0);
        float x = -mUnitOffsetHour;//减掉一个位置的距离让最左边的数字移到屏幕外面
        int number, preOffset = PER_SCREEN_HOUR - 1;
        int preHour = mCurrentHour - preOffset, end = mCurrentHour + preOffset;
        Bitmap bitmap;
        for (int index = preHour; index < end; index++) {
            number = (index + mHoursScale) % mHoursScale;
            if (!DateUtil.is24HourFormat(mContext) && number == 0) {
                number = 12;
            }
            bitmap = mBitmapManager.getHourBitmap(number, ContextCompat.getColor(mContext, R.color.hour_color));
            canvas.drawBitmap(bitmap, x - mAnimValueHour, -bitmap.getHeight() / 2f, null);
            x += mUnitOffsetHour;
        }
        canvas.restore();
    }

    private void drawDate(Canvas canvas) {
        Calendar instance = Calendar.getInstance();
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);

        int color = ContextCompat.getColor(mContext, R.color.date_color);
        float margin = mContext.getResources().getDimension(R.dimen.date_point_width) / 8;

        canvas.save();

        //月份
        Bitmap bitmap = mBitmapManager.getDateBitmap(month, color);
        canvas.translate(mPointScreenCenter.x, 2 * mPointScreenCenter.y - 1.75f * bitmap.getHeight());
        canvas.drawBitmap(bitmap, -bitmap.getWidth() - margin, 0, null);

        //点
        bitmap = mBitmapManager.get(R.drawable.number_date_point, color);
        float topOffset = mContext.getResources().getDimension(R.dimen.date_height) -
                mContext.getResources().getDimension(R.dimen.date_point_height);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, topOffset, null);

        //号数
        bitmap = mBitmapManager.getDateBitmap(day, color);
        canvas.drawBitmap(bitmap, margin, 0, null);

        canvas.restore();
    }

}
