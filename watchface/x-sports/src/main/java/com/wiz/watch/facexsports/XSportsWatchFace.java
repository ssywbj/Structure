package com.wiz.watch.facexsports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.NumberBeatView;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.utils.IntentHelper;

public class XSportsWatchFace extends NumberBeatView {
    private static final int MSG_INIT_SPORT_DATA = 2;
    private XSportsBitmapManager mBitmapManager;

    private float mPaddingHorizontal;
    private float mPaddingVertical;

    private Paint mPaintRect;
    private float mWidthRect;

    private int mRectPadding;
    private final RectF mRectFCalories = new RectF();
    private float mOffsetRectFCalories;
    private final Path mPathCalories = new Path();
    private final PathMeasure mPathMeasure = new PathMeasure();
    private final Path mPathCaloriesDst = new Path();

    private final RectF mRectFSteps = new RectF();
    private final Path mPathSteps = new Path();
    private final Path mPathStepsDst = new Path();

    private final RectF mRectFSportTime = new RectF();
    private final Path mPathSportTime = new Path();
    private final Path mPathSportTimeDst = new Path();

    private boolean mPassActionMove;
    private final RectF mRectFHealth = new RectF();

    public XSportsWatchFace(Context context, boolean isEditMode) {
        super(context, isEditMode);
        this.init();
    }

    public XSportsWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setDefaultTime(8, 36, 55);
        mBitmapManager = new XSportsBitmapManager(mContext);

        mWidthRect = mContext.getResources().getDimension(R.dimen.icon_info_width) + DimenUtil.dip2px(mContext, 2);
        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setStrokeWidth(mWidthRect);
        mPaintRect.setStrokeCap(Paint.Cap.ROUND);

        mPaddingHorizontal = DimenUtil.dip2px(mContext, 10);
        mPaddingVertical = DimenUtil.dip2px(mContext, 36);
        mOffsetRectFCalories = mPaddingHorizontal;

        mRectPadding = DimenUtil.dip2px(mContext, 4);
    }

    @Override
    protected void onAppearanceAnimFinished() {
        super.onAppearanceAnimFinished();
        setUpdateTimePerSecondImmediately(true);
        notifyMsgUpdateTimePerSecond();
        getHandler().sendEmptyMessageDelayed(MSG_INIT_SPORT_DATA, 1000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectFCalories.top = mPaddingHorizontal * 2;
        mRectFCalories.right = w - mPaddingHorizontal * 2;
        mRectFCalories.bottom = h - mPaddingHorizontal * 2;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            this.queryStepAndCalories();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (!(mRectFHealth.contains(x, y)) || mIsEditMode) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPassActionMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mPassActionMove = true;
                break;
            case MotionEvent.ACTION_UP:
                if (mPassActionMove) {
                    return true;
                }

                if (mRectFHealth.contains(x, y)) {
                    IntentHelper.openApp(mContext, "com.wiz.watch.health.action.SPORT_MAIN_LAUNCHER");
                }
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
    }

    @Override
    protected void dispatchMsg(Message msg) {
        super.dispatchMsg(msg);
        if (msg.what == MSG_INIT_SPORT_DATA) {
            this.queryStepAndCalories();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));

        this.paintTime(canvas);
        this.paintRect(canvas);
    }

    private void paintRect(Canvas canvas) {
        mPaintRect.setColor(0xFFCC0808);
        canvas.drawPath(mPathCalories, mPaintRect);
        mPaintRect.setColor(0xFFFF0A0A);
        canvas.drawPath(mPathCaloriesDst, mPaintRect);
        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_calories);
        float left = mRectFCalories.right - bitmap.getWidth() / 2f;
        float top = mRectFCalories.centerY() - bitmap.getHeight() / 2f;
        canvas.drawBitmap(bitmap, left, top, null);
        top += (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
        int color = 0x4D330202;
        Bitmap dst = mBitmapManager.getSmallNumberBitmap(mCalories[0], color);
        canvas.drawBitmap(dst, mRectFCalories.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mCalories[1], color);
        canvas.drawBitmap(dst, mRectFCalories.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mCalories[2], color);
        canvas.drawBitmap(dst, mRectFCalories.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mCalories[3], color);
        canvas.drawBitmap(dst, mRectFCalories.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mCalories[4], color);
        canvas.drawBitmap(dst, mRectFCalories.right - dst.getWidth() / 2f, top, null);

        mPaintRect.setColor(0xFF14CC2E);
        canvas.drawPath(mPathSteps, mPaintRect);
        mPaintRect.setColor(0xFF19FF39);
        canvas.drawPath(mPathStepsDst, mPaintRect);
        bitmap = mBitmapManager.get(R.drawable.icon_steps);
        left = mRectFSteps.right - bitmap.getWidth() / 2f;
        top = mRectFSteps.centerY() - bitmap.getHeight() / 2f;
        canvas.drawBitmap(bitmap, left, top, null);
        top += (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
        color = 0x4D05330B;
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[0], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[1], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[2], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[3], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[4], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSteps[5], color);
        canvas.drawBitmap(dst, mRectFSteps.right - dst.getWidth() / 2f, top, null);

        mPaintRect.setColor(0xFF0092CC);
        canvas.drawPath(mPathSportTime, mPaintRect);
        mPaintRect.setColor(0xFF00B7FF);
        canvas.drawPath(mPathSportTimeDst, mPaintRect);
        bitmap = mBitmapManager.get(R.drawable.icon_sport_time);
        left = mRectFSportTime.right - bitmap.getWidth() / 2f;
        top = mRectFSportTime.centerY() - bitmap.getHeight() / 2f;
        canvas.drawBitmap(bitmap, left, top, null);
        top += (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
        color = 0x4D002533;
        dst = mBitmapManager.getSmallNumberBitmap(mSportTime[0], color);
        canvas.drawBitmap(dst, mRectFSportTime.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSportTime[1], color);
        canvas.drawBitmap(dst, mRectFSportTime.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getRotate(R.drawable.paint_sign_colon, color, 90);
        canvas.drawBitmap(dst, mRectFSportTime.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSportTime[2], color);
        canvas.drawBitmap(dst, mRectFSportTime.right - dst.getWidth() / 2f, top, null);
        top += dst.getHeight();
        dst = mBitmapManager.getSmallNumberBitmap(mSportTime[3], color);
        canvas.drawBitmap(dst, mRectFSportTime.right - dst.getWidth() / 2f, top, null);
    }

    private void paintTime(Canvas canvas) {
        canvas.save();
        canvas.translate(mPaddingHorizontal, mPaddingVertical);
        int topOffset = DimenUtil.dip2px(mContext, 12);

        int color = 0xFFFFFFFF;
        float top = 0, left = 0;
        int tens = mHour / 10;
        int unit = mHour % 10;
        Bitmap bitmap = mBitmapManager.getNumberBitmap(tens, color);
        canvas.drawBitmap(bitmap, 0, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(unit, color);
        canvas.drawBitmap(bitmap, left, top, null);
        top += (bitmap.getHeight() + topOffset);
        float tmpTop = top;

        tens = mMinute / 10;
        unit = mMinute % 10;
        left = 0;
        bitmap = mBitmapManager.getNumberBitmap(tens, color);
        canvas.drawBitmap(bitmap, 0, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(unit, color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        top += (bitmap.getHeight() + topOffset);
        bitmap = mBitmapManager.get(R.drawable.minute_flag, color);
        canvas.drawBitmap(bitmap, left, tmpTop, null);
        tmpTop = top;

        tens = mSecond / 10;
        unit = mSecond % 10;
        left = 0;
        bitmap = mBitmapManager.getNumberBitmap(tens, color);
        canvas.drawBitmap(bitmap, 0, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(unit, color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        top += bitmap.getHeight();
        bitmap = mBitmapManager.get(R.drawable.minute_flag, color);
        canvas.drawBitmap(bitmap, left, tmpTop, null);
        left += bitmap.getWidth() + DimenUtil.dip2px(mContext, 2);
        bitmap = mBitmapManager.get(R.drawable.minute_flag, color);
        canvas.drawBitmap(bitmap, left, tmpTop, null);
        left += bitmap.getWidth();

        mOffsetRectFCalories = (left + mPaddingHorizontal * 3.4f);
        mRectFCalories.left = mOffsetRectFCalories;
        mPathCalories.reset();//不reset的话，每次都会添加路径，可能会导致锯齿的产生
        mPathCalories.addRoundRect(mRectFCalories, mRectFCalories.width() / 2f, mRectFCalories.width() / 2f, Path.Direction.CW);//CW：顺时针，CCW：逆时针
        mPathMeasure.setPath(mPathCalories, true);
        mPathCaloriesDst.reset();//同上
        float startRate = 0.6f, remainRate = 1 - startRate;
        float startD = mPathMeasure.getLength() * startRate;
        if (mCaloriesRate <= remainRate) {
            float stopD = startD + mPathMeasure.getLength() * mCaloriesRate;
            mPathMeasure.getSegment(startD, stopD, mPathCaloriesDst, true);
        } else {
            mPathMeasure.getSegment(startD, mPathMeasure.getLength(), mPathCaloriesDst, true);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * (mCaloriesRate - remainRate), mPathCaloriesDst, true);
        }

        mRectFSteps.left = mRectFCalories.left + mWidthRect + mRectPadding * 0.95f;
        mRectFSteps.top = mRectFCalories.top + mWidthRect + mRectPadding;
        mRectFSteps.right = mRectFCalories.right - mWidthRect - mRectPadding * 0.95f;
        mRectFSteps.bottom = mRectFCalories.bottom - mWidthRect - mRectPadding;
        mPathSteps.reset();
        mPathSteps.addRoundRect(mRectFSteps, mRectFSteps.width() / 2f, mRectFSteps.width() / 2f, Path.Direction.CW);
        mPathMeasure.setPath(mPathSteps, true);
        mPathStepsDst.reset();
        startRate = 0.629f;
        remainRate = 1 - startRate;
        startD = mPathMeasure.getLength() * startRate;
        if (mStepsRate <= remainRate) {
            mPathMeasure.getSegment(startD, startD + mPathMeasure.getLength() * mStepsRate, mPathStepsDst, true);
        } else {
            mPathMeasure.getSegment(startD, mPathMeasure.getLength(), mPathStepsDst, true);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * (mStepsRate - remainRate), mPathStepsDst, true);
        }

        mRectFSportTime.left = mRectFSteps.left + mWidthRect + mRectPadding;
        mRectFSportTime.top = mRectFSteps.top + mWidthRect + mRectPadding;
        mRectFSportTime.right = mRectFSteps.right - mWidthRect - mRectPadding;
        mRectFSportTime.bottom = mRectFSteps.bottom - mWidthRect - mRectPadding;
        mPathSportTime.reset();
        mPathSportTime.addRoundRect(mRectFSportTime, mRectFSportTime.width() / 2f, mRectFSportTime.width() / 2f, Path.Direction.CW);
        mPathMeasure.setPath(mPathSportTime, true);
        mPathSportTimeDst.reset();
        startRate = 0.69f;
        remainRate = 1 - startRate;
        startD = mPathMeasure.getLength() * startRate;
        if (mSportTimeRate <= remainRate) {
            mPathMeasure.getSegment(startD, startD + mPathMeasure.getLength() * mSportTimeRate, mPathSportTimeDst, true);
        } else {
            mPathMeasure.getSegment(startD, mPathMeasure.getLength(), mPathSportTimeDst, true);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * (mSportTimeRate - remainRate), mPathSportTimeDst, true);
        }

        left = DimenUtil.dip2px(mContext, 10);
        bitmap = mBitmapManager.get(R.drawable.icon_sport_flag);
        float topHealth = top + DimenUtil.dip2px(mContext, 14);
        canvas.drawBitmap(bitmap, left, topHealth, null);
        mRectFHealth.left = left + mPaddingHorizontal;
        mRectFHealth.top = topHealth + mPaddingVertical;
        mRectFHealth.right = mRectFHealth.left + bitmap.getWidth();
        mRectFHealth.bottom = mRectFHealth.top + bitmap.getHeight();

        canvas.restore();
    }

    private static final String AUTHORITY = "com.wiz.watch.health.provider";
    private static final String CALL_SPORT_DATA = "sport_step/query_current";
    private final int[] mCalories = new int[5];
    private final int[] mSportTime = new int[4];
    private final int[] mSteps = new int[6];
    private float mCaloriesRate;
    private float mStepsRate;
    private float mSportTimeRate;

    private void queryStepAndCalories() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY),
                    CALL_SPORT_DATA, mContext.getPackageName(), null);
            if (bundle == null) {
                Log.e(mTAG, "query sport data error: bundle is null");
                return;
            }

            int calories = (int) bundle.getFloat("calorie");
            int steps = bundle.getInt("step");
            long sportTime = bundle.getLong("time");//毫秒
            Log.d(mTAG, "calories: " + calories + ", steps: " + steps + ", sport time: " + sportTime);
            long caloriesTarget = bundle.getLong("tc"); //目标卡路里
            long stepsTarget = bundle.getLong("ts");//目标步数
            long sportTimeTarget = bundle.getLong("tt");//目标时间
                /*calories = 10;
                caloriesTarget = 100;*/
            mCaloriesRate = 1f * calories / caloriesTarget;
                /*steps = 3;
                stepsTarget = 100;*/
            mStepsRate = 1f * steps / stepsTarget;
                /*sportTime = 120 * 1000;
                sportTimeTarget = 600 * 1000;*/
            mSportTimeRate = 1f * sportTime / sportTimeTarget;
            Log.d(mTAG, "calories target: " + caloriesTarget + ", steps target: " + stepsTarget + ", sport time target: "
                    + sportTimeTarget + ", mCaloriesRate: " + mCaloriesRate + ", mStepsRate: " + mStepsRate + ", mSportTimeRate: " + mSportTimeRate);

            //calories = 20897;
            mCalories[0] = calories / 10000;
            mCalories[1] = (calories % 10000) / 1000;
            mCalories[2] = (calories % 1000) / 100;
            mCalories[3] = (calories % 100) / 10;
            mCalories[4] = calories % 10;

            //steps = 160978;
            mSteps[0] = steps / 100000;
            mSteps[1] = (steps % 100000) / 10000;
            mSteps[2] = (steps % 10000) / 1000;
            mSteps[3] = (steps % 1000) / 100;
            mSteps[4] = (steps % 100) / 10;
            mSteps[5] = steps % 10;

            sportTime = sportTime / 1000;
            //sportTime = 60 * 90;
            int hour = (int) (sportTime / (60 * 60));
            int minute = (int) ((sportTime / 60) % 60);
            mSportTime[0] = hour / 10;
            mSportTime[1] = hour % 10;
            mSportTime[2] = minute / 10;
            mSportTime[3] = minute % 10;
        } catch (Exception e) {
            Log.e(mTAG, "query step and calories error: " + e.toString(), new Exception());
        }
    }

}
