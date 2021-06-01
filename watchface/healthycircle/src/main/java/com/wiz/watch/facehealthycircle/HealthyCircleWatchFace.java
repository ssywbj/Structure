package com.wiz.watch.facehealthycircle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

public class HealthyCircleWatchFace extends FaceAnimView {
    private static final String PREFS_FILE = "healthy_circle_watch_face";
    private static final String PREFS_KEY_FACE_MODE = "key_face_mode";

    private HealthyCircleBitmapManager mBitmapManager;
    private float mRatio, mRatioSecond, mRatioData;

    private long mCurrentTimeMillis;

    private static final int SCALES = 36;
    private static final float SCALE_DEGREE = 360f / SCALES;

    private float mRadius;
    private final Paint mPaintPointer = new Paint();
    private Paint mPaintPointerOuter;
    private float mPointerOffset;
    private float mLenPointerMinute = 50, mLenPointerHour = 30;

    private static final int ARCS = 3;
    private final Paint mPaintArc = new Paint();
    private final Path mPathArcSteps = new Path();
    private final Path mPathArcStepsDst = new Path();
    private final Path mPathArcCalories = new Path();
    private final Path mPathArcCaloriesDst = new Path();
    private final Path mPathArcSportTime = new Path();
    private final Path mPathArcSportTimeDst = new Path();
    private final PathMeasure mPathMeasure = new PathMeasure();

    private ValueAnimator mScaleAnimator;
    private float mScaleValue;
    private SharedPreferences mPrefs;

    public HealthyCircleWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        if (!isEditMode) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((System.currentTimeMillis() - mCurrentTimeMillis) <= 500) {
                        boolean faceMode = mPrefs.getBoolean(PREFS_KEY_FACE_MODE, false);
                        mPrefs.edit().putBoolean(PREFS_KEY_FACE_MODE, !faceMode).apply();
                        mScaleAnimator.start();
                    } else {
                        mCurrentTimeMillis = System.currentTimeMillis();
                    }

                }
            });
        }
        this.init();
    }

    public HealthyCircleWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mSportDataManager = new SportDataManager(mContext);
        mBitmapManager = new HealthyCircleBitmapManager(mContext);
        mRatio = Float.parseFloat(mContext.getString(R.string.ratio));
        mRatioSecond = mRatio / 2.75f;
        mRatioData = mRatio / 3.67f;

        mPrefs = mContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        setDefaultTime(10, 10, 30);
        if (mPrefs.getBoolean(PREFS_KEY_FACE_MODE, false)) {
            needAppearAnimPointer();
        } else {
            needAppearAnimNumber();
        }

        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setStrokeCap(Paint.Cap.ROUND);
        mPointerOffset = DimenUtil.dip2px(mContext, 16);
        mPaintPointerOuter = new Paint(mPaintPointer);
        mPaintPointerOuter.setStrokeCap(Paint.Cap.SQUARE);
        mPaintPointerOuter.setColor(0xFF000000);

        mPaintArc.setAntiAlias(true);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);
        mPaintArc.setStrokeWidth(getResources().getDimension(R.dimen.arc_width));

        if (!mIsEditMode || !mIsDimMode) {
            mScaleAnimator = ValueAnimator.ofFloat(0.6f, 1);
            mScaleAnimator.setDuration(200);
            mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (valueAnimator.getAnimatedValue() instanceof Float) {
                        mScaleValue = (float) valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                }
            });
            mScaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onAppearAnimFinished();
                }
            });
        }

        queryStepAndCalories();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                queryStepAndCalories();
            }
        }, mIsEditMode ? 0 : 300);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w / 2f, h / 2f) - mPaintArc.getStrokeWidth() / 2f - DimenUtil.dip2px(mContext, 2);
        final int arcOffset = 12;
        float sweepAngle = (360f - ARCS * 2 * arcOffset) / ARCS;
        mPathArcSportTime.reset();
        mPathArcSportTime.addArc(-mRadius, -mRadius, mRadius, mRadius, -90 + arcOffset, sweepAngle);
        mPathArcCalories.reset();
        mPathArcCalories.addArc(-mRadius, -mRadius, mRadius, mRadius, 30 + arcOffset, sweepAngle);
        mPathArcSteps.reset();
        mPathArcSteps.addArc(-mRadius, -mRadius, mRadius, mRadius, 150 + arcOffset, sweepAngle);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterTimeTickReceiver();
            if (mPrefs.getBoolean(PREFS_KEY_FACE_MODE, false)) {
                stopSecondPointerAnim();
            } else {
                unregisterSecondTicker();
            }
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        this.queryStepAndCalories();
        registerTimeTickReceiver();
        if (mPrefs.getBoolean(PREFS_KEY_FACE_MODE, false)) {
            unregisterSecondTicker();
            startSecondPointerAnim();
        } else {
            stopSecondPointerAnim();
            registerSecondTicker();
        }
    }

    @Override
    protected void onTimeTick() {
        this.queryStepAndCalories();
    }

    public void destroy() {
        mBitmapManager.clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        this.paintArc(canvas);
        this.paintData(canvas);

        canvas.save();
        if (mScaleAnimator.isRunning()) {
            canvas.scale(mScaleValue, mScaleValue);
        }

        if (mPrefs.getBoolean(PREFS_KEY_FACE_MODE, false)) {
            this.paintClockPointer(canvas, mSecondRatio * 360, mMinuteRatio * 360
                    , mHourRatio * 360);
        } else {
            this.paintClockNumber(canvas);
        }
        canvas.restore();
    }

    private void paintArc(Canvas canvas) {
        mPaintArc.setStyle(Paint.Style.STROKE);

        mPaintArc.setColor(0xFF0092CC);
        canvas.drawPath(mPathArcSportTime, mPaintArc);
        mPaintArc.setColor(0xFF00B7FF);
        mPathMeasure.setPath(mPathArcSportTime, false);
        mPathArcSportTimeDst.reset();
        mPathMeasure.getSegment(0, mPathMeasure.getLength() * mSportDataManager.getSportTimeProgress(), mPathArcSportTimeDst, true);
        canvas.drawPath(mPathArcSportTimeDst, mPaintArc);

        mPaintArc.setColor(0xFFCC0808);
        canvas.drawPath(mPathArcCalories, mPaintArc);
        mPaintArc.setColor(0xFFFF0A0A);
        mPathMeasure.setPath(mPathArcCalories, false);
        mPathArcCaloriesDst.reset();
        mPathMeasure.getSegment(0, mPathMeasure.getLength() * mSportDataManager.getCaloriesProgress(), mPathArcCaloriesDst, true);
        canvas.drawPath(mPathArcCaloriesDst, mPaintArc);

        mPaintArc.setColor(0xFF14CC2E);
        canvas.drawPath(mPathArcSteps, mPaintArc);
        mPaintArc.setColor(0xFF19FF39);
        mPathMeasure.setPath(mPathArcSteps, false);
        mPathArcStepsDst.reset();
        mPathMeasure.getSegment(0, mPathMeasure.getLength() * mSportDataManager.getStepsProgress(), mPathArcStepsDst, true);
        canvas.drawPath(mPathArcStepsDst, mPaintArc);

        int offset = DimenUtil.dip2px(mContext, 4);
        mPaintArc.setColor(0x2EFFFFFF);//255*0.18
        mLenPointerMinute = mRadius - mPaintArc.getStrokeWidth() - 1.4f * offset;
        canvas.drawCircle(0, 0, mLenPointerMinute, mPaintArc);
        mPaintArc.setColor(0x1FFFFFFF);//255*0.12
        mLenPointerHour = mRadius - 2 * mPaintArc.getStrokeWidth() - 2.6f * offset;
        canvas.drawCircle(0, 0, mLenPointerHour, mPaintArc);
        mPaintArc.setStyle(Paint.Style.FILL);
        mPaintArc.setColor(0x17FFFFFF);//255*0.09
        canvas.drawCircle(0, 0, mLenPointerHour - mPaintArc.getStrokeWidth() / 2f - offset, mPaintArc);
    }

    private void paintData(Canvas canvas) {
        final float outerRadius = (mLenPointerMinute + mPaintArc.getStrokeWidth() / 2f);
        final float innerRadius = (mLenPointerMinute - mPaintArc.getStrokeWidth() / 2f);
        final float centerRadius = innerRadius + (outerRadius - innerRadius) / 2f;

        Bitmap bitmap;
        int start = 4, end = start + mSportTime.length;
        for (int i = start; i < end; i++) {
            canvas.save();
            canvas.rotate(i * SCALE_DEGREE);
            int number = mSportTime[i - start];
            if (number == -1) {
                bitmap = mBitmapManager.get(R.drawable.sign_colon, 0xFF00B7FF);
            } else {
                bitmap = mBitmapManager.getNumberBitmap(number, 0xFF00B7FF, mRatioData);
            }
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -(centerRadius + bitmap.getHeight() / 2f), null);
            canvas.restore();
        }

        start = 16;
        end = start + mCalories.length;
        for (int i = start; i < end; i++) {
            canvas.save();
            canvas.rotate(i * SCALE_DEGREE);
            bitmap = mBitmapManager.getReverseBitmap(mCalories[end - i - 1], 0xFFFF0A0A, mRatioData);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -(centerRadius + bitmap.getHeight() / 2f), null);
            canvas.restore();
        }

        start = 27;
        end = start + mSteps.length;
        for (int i = start; i < end; i++) {
            canvas.save();
            canvas.rotate((i + 0.5f) * SCALE_DEGREE);
            bitmap = mBitmapManager.getNumberBitmap(mSteps[i - start], 0xFF19FF39, mRatioData);
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -(centerRadius + bitmap.getHeight() / 2f), null);
            canvas.restore();
        }
    }

    private void paintClockPointer(Canvas canvas, float secondDegree, float minuteDegree, float hourDegree) {
        canvas.save();
        canvas.rotate(hourDegree);
        float stopY = (mLenPointerHour + mPaintArc.getStrokeWidth() / 2.8f);
        mPaintPointerOuter.setStrokeWidth(DimenUtil.dip2px(mContext, 15));
        canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointerOuter);
        mPaintPointer.setColor(0xFFFFFFFF);
        mPaintPointer.setStrokeWidth(DimenUtil.dip2px(mContext, 10));
        canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointer);
        canvas.restore();

        canvas.save();
        canvas.rotate(minuteDegree);
        stopY = mLenPointerMinute + mPaintArc.getStrokeWidth() / 2.5f;
        mPaintPointerOuter.setStrokeWidth(DimenUtil.dip2px(mContext, 12));
        canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointerOuter);
        mPaintPointer.setStrokeWidth(DimenUtil.dip2px(mContext, 7));
        canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointer);
        canvas.restore();

        if (!mIsDimMode) {
            canvas.save();
            canvas.rotate(secondDegree);
            mPaintPointerOuter.setStrokeWidth(DimenUtil.dip2px(mContext, 10));
            stopY = mRadius + mPaintArc.getStrokeWidth() / 2.3f;
            canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointerOuter);
            mPaintPointer.setColor(0xFFFFE600);
            mPaintPointer.setStrokeWidth(DimenUtil.dip2px(mContext, 5));
            canvas.drawLine(0, -mPointerOffset, 0, -stopY, mPaintPointer);
            canvas.restore();
        }

        canvas.drawCircle(0, 0, DimenUtil.dip2px(mContext, 6), mPaintPointer);
    }

    private void paintClockNumber(Canvas canvas) {
        int color = 0xFFFFFFFF;
        float left, top;
        final int margin = DimenUtil.dip2px(mContext, 4);

        int tens = mHour / 10;
        int units = mHour % 10;
        Bitmap bitmap = mBitmapManager.getNumberBitmap(tens, color, mRatio);
        left = -bitmap.getWidth();
        top = -bitmap.getHeight() - margin;
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, mRatio);
        canvas.drawBitmap(bitmap, left, top, null);

        tens = mMinute / 10;
        units = mMinute % 10;
        bitmap = mBitmapManager.getNumberBitmap(tens, color, mRatio);
        left = -bitmap.getWidth();
        top = margin;
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, mRatio);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        float topTmp = top + bitmap.getHeight();
        bitmap = mBitmapManager.get(R.drawable.minute_flag, color);
        canvas.drawBitmap(bitmap, left + DimenUtil.dip2px(mContext, 1), top + margin * 1.1f, null);

        if (!mIsDimMode) {
            color = 0xFFFFE600;
            tens = mSecond / 10;
            units = mSecond % 10;
            bitmap = mBitmapManager.getNumberBitmap(tens, color, mRatioSecond);
            topTmp -= bitmap.getHeight();
            left -= DimenUtil.dip2px(mContext, 4);
            canvas.drawBitmap(bitmap, left, topTmp, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.getNumberBitmap(units, color, mRatioSecond);
            canvas.drawBitmap(bitmap, left, topTmp, null);
            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 1));
            topTmp += DimenUtil.dip2px(mContext, 1);
            bitmap = mBitmapManager.get(R.drawable.second_flag, color);
            canvas.drawBitmap(bitmap, left, topTmp, null);
            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 1));
            canvas.drawBitmap(bitmap, left, topTmp, null);
        }
    }

    private final int[] mCalories = new int[5];
    private final int[] mSportTime = new int[5];
    private final int[] mSteps = new int[6];
    private SportDataManager mSportDataManager;

    private void queryStepAndCalories() {
        mSportDataManager.queryStepAndCalories();

        int calories = mSportDataManager.getCalories();
        mCalories[0] = calories / 10000;
        mCalories[1] = (calories % 10000) / 1000;
        mCalories[2] = (calories % 1000) / 100;
        mCalories[3] = (calories % 100) / 10;
        mCalories[4] = calories % 10;

        int steps = mSportDataManager.getSteps();
        mSteps[0] = steps / 100000;
        mSteps[1] = (steps % 100000) / 10000;
        mSteps[2] = (steps % 10000) / 1000;
        mSteps[3] = (steps % 1000) / 100;
        mSteps[4] = (steps % 100) / 10;
        mSteps[5] = steps % 10;

        long sportTime = mSportDataManager.getSportTime() / 1000;
        int hour = (int) (sportTime / (60 * 60));
        int minute = (int) ((sportTime / 60) % 60);
        mSportTime[0] = hour / 10;
        mSportTime[1] = hour % 10;
        mSportTime[2] = -1;
        mSportTime[3] = minute / 10;
        mSportTime[4] = minute % 10;
    }

}
