package com.wiz.watch.facehealthdata;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watch.facehealthdata.bean.PathBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HealthDataWatchFace extends FaceAnimView {
    private static final String PREFS_FILE = "healthy_data_watch_face";
    private static final String PREFS_KEY_HEART_RATE = "prefs_key_heart_rate";

    private BitmapManager2 mBitmapManager;

    private float mTimeTopOffset;

    private final RectF mRectFSportTime = new RectF();
    private final RectF mRectFStep = new RectF();
    private final RectF mRectFCalories = new RectF();
    private final Paint mPaintText = new Paint();
    private final Paint mPaintArc = new Paint();
    private final Rect mRectText = new Rect();

    private static final int TIME_UPDATE_HEART_RATE = 2500;
    private static final int HEART_RATE_TIME = 30;
    private final Paint mPaintHeartCurve = new Paint();
    private ValueAnimator mAnimatorHeartCurve;
    private float mValueHeartCurve;

    private static final String DEFAULT_HEART_RATE = "--";
    private String mHeartRateText = DEFAULT_HEART_RATE;
    private final Rect mRectHeartRate = new Rect();
    private final Paint mPaintHeartRate = new Paint();

    private final RectF mRectFHeartRate = new RectF();

    private float mTotalOffset;
    private int mOffsetCount = 0;
    private int mWaveWidth;
    private int mWaveHeight;

    private ValueAnimator mScaleAnimator;
    private float mScaleValue = 1;

    private final List<PathBean> mPathList = new ArrayList<>();
    private int mHeartRate;
    private PowerManager.WakeLock mWakeLock;

    private final Paint mPaintRect = new Paint();

    private final PointF mPointScreenCenter = new PointF();
    private SportDataManager mSportDataManager;
    private float mScale;

    private SharedPreferences mPrefs;
    private int mBgColor;

    private final Runnable mRunnableStopMeasure = new Runnable() {
        @Override
        public void run() {
            //Log.d(mTAG, "Stop Measure Runnable: " + mHeartRate);
            mPrefs.edit().putInt(PREFS_KEY_HEART_RATE, mHeartRate).apply();
            mMeasuringHeartRate = false;
            stopMeasure();
            mScaleValue = 1;
        }
    };

    private final Runnable mRunnableUpdateHeartRate = new Runnable() {
        @Override
        public void run() {
            //mHeartRate = "101";
            //<100,2;>=100<110,3;>=110,4
            //mHeartRate = new Random().nextInt(130);
            //Log.i(mTAG, "Update Heart Rate Runnable: " + mHeartRate);
            mHeartRateText = getHeartText(mHeartRate);

            final int waveNumber = getWaveNumber(mHeartRate);
            if (waveNumber == 0) {
                mPathList.add(new PathBean(productDashLine(mPathList.size() * 2 * mPointScreenCenter.x), PathBean.TYPE_DASH_LINE));
            } else {
                mPathList.add(new PathBean(productWave(mPathList.size() * 2 * mPointScreenCenter.x, waveNumber), PathBean.TYPE_WAVE));
            }

            startHeartCurveAnim();

            getHandler().postDelayed(mRunnableUpdateHeartRate, TIME_UPDATE_HEART_RATE);
        }
    };

    public HealthDataWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public HealthDataWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setDefaultTime(8, 36, TIME_NONE);
        needAppearAnimNumber();
        mBitmapManager = new BitmapManager2(mContext);
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, mTAG);

        mTimeTopOffset = DimenUtil.dip2px(mContext, 25);
        mWaveWidth = DimenUtil.dip2px(mContext, 34);
        mWaveHeight = DimenUtil.dip2px(mContext, 80);

        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(0xFFFFFFFF);
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setTextSize(DimenUtil.dip2px(mContext, 20));

        mPaintHeartRate.setAntiAlias(true);
        mPaintHeartRate.setStyle(Paint.Style.FILL);
        mPaintHeartRate.setColor(0xFFFFFFFF);
        mPaintHeartRate.setTextSize(DimenUtil.dip2px(mContext, 24));

        mPaintArc.setAntiAlias(true);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);
        mPaintArc.setStrokeWidth(DimenUtil.dip2px(mContext, 8));

        mPaintHeartCurve.setAntiAlias(true);
        mPaintHeartCurve.setStyle(Paint.Style.STROKE);
        mPaintHeartCurve.setColor(0xFFFF0000);
        mPaintHeartCurve.setStrokeWidth(DimenUtil.dip2px(mContext, 3.5f));

        mSportDataManager = new SportDataManager(mContext);
        this.queryStepAndCalories();

        if (!mIsDimMode || !mIsEditMode) {
            this.initHeartCurveAnim();
            this.initHeartTextAnim();

            try {
                mContext.getContentResolver().registerContentObserver(URI_HEART_RATE, true, mContentObserver);
            } catch (Exception e) {
                Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
            }
        }

        mBgColor = ContextCompat.getColor(mContext, android.R.color.black);
        mPaintRect.setColor(mBgColor);
        mScale = Float.parseFloat(mContext.getString(R.string.ratio));

        mPrefs = mContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    private void initHeartCurveAnim() {
        mAnimatorHeartCurve = ValueAnimator.ofFloat(0, 0);
        mAnimatorHeartCurve.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mValueHeartCurve = (Float) animation.getAnimatedValue();
                    //Log.d(mTAG, "onAnimationUpdate, mValueHeartCurve: " + mValueHeartCurve);
                    mOffsetCount = (int) (mValueHeartCurve / (2 * mPointScreenCenter.x));
                    invalidate();
                }
            }
        });
        mAnimatorHeartCurve.setDuration(TIME_UPDATE_HEART_RATE);
        mAnimatorHeartCurve.setInterpolator(new LinearInterpolator());
    }

    private void initHeartTextAnim() {
        mScaleAnimator = ValueAnimator.ofFloat(1.0f, 0.85f);
        mScaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mScaleAnimator.setDuration(700);
        mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimator.getAnimatedValue() instanceof Float) {
                    mScaleValue = (float) valueAnimator.getAnimatedValue();
                }
            }
        });
    }

    private void startHeartCurveAnim() {
        mAnimatorHeartCurve.setFloatValues(mTotalOffset, mTotalOffset += 2 * mPointScreenCenter.x);
        mAnimatorHeartCurve.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointScreenCenter.x = w / 2f;
        mPointScreenCenter.y = h / 2f;
        final int rectFMargin = DimenUtil.dip2px(mContext, 18);
        final int rectFWidth = (w - rectFMargin * 4) / 3;
        mRectFSportTime.top = h - rectFWidth - rectFMargin * 1.1f;
        mRectFSportTime.bottom = mRectFSportTime.top + rectFWidth;
        mRectFStep.top = mRectFSportTime.top;
        mRectFStep.bottom = mRectFSportTime.bottom;
        mRectFCalories.top = mRectFSportTime.top;
        mRectFCalories.bottom = mRectFSportTime.bottom;
        mRectFSportTime.left = rectFMargin;
        mRectFSportTime.right = mRectFSportTime.left + rectFWidth;
        mRectFStep.left = mRectFSportTime.right + rectFMargin;
        mRectFStep.right = mRectFStep.left + rectFWidth;
        mRectFCalories.left = mRectFStep.right + rectFMargin;
        mRectFCalories.right = mRectFCalories.left + rectFWidth;

        final float rectFHeartRateWidth = DimenUtil.dip2px(mContext, 70);
        mRectFHeartRate.left = 2 * mPointScreenCenter.x - rectFHeartRateWidth;
        mRectFHeartRate.top = mPointScreenCenter.y - rectFHeartRateWidth / 2;
        mRectFHeartRate.right = mRectFHeartRate.left + rectFHeartRateWidth;
        mRectFHeartRate.bottom = mRectFHeartRate.top + rectFHeartRateWidth;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(new Runnable() {
            @Override
            public void run() {
                mHeartRate = mPrefs.getInt(PREFS_KEY_HEART_RATE, 0);
                mHeartRateText = getHeartText(mHeartRate);
                //Log.d(mTAG, "init Heart Rate: " + mHeartRate);

                mPathList.clear();
                final int waveNumber = getWaveNumber(mHeartRate);
                if (waveNumber <= 0) {
                    mPathList.add(new PathBean(productDashLine(0), PathBean.TYPE_DASH_LINE));
                } else {
                    mPathList.add(new PathBean(productWave(0, waveNumber), PathBean.TYPE_WAVE));
                }

                invalidate();
            }
        });
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
    }

    @Override
    protected void onAppearAnimFinished() {
        this.queryStepAndCalories();
        updateTime();
        invalidate();
        registerTimeTickReceiver();
    }

    private boolean mPassActionMove, mMeasuringHeartRate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (!(mRectFHeartRate.contains(x, y)) || mIsEditMode) {
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

                if (mRectFHeartRate.contains(x, y)) {
                    if (mMeasuringHeartRate) {
                        return true;
                    }
                    mMeasuringHeartRate = true;

                    mHeartRate = 0;
                    mScaleAnimator.start();
                    mWakeLock.acquire((HEART_RATE_TIME + 5) * 1000);

                    try {
                        mContext.getContentResolver().registerContentObserver(URI_HEART_RATE, true, mContentObserver);
                    } catch (Exception e) {
                        Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
                    }

                    this.invokeHeartRate(true);
                    if (getHandler() != null) {
                        getHandler().post(mRunnableUpdateHeartRate);
                        getHandler().postDelayed(mRunnableStopMeasure, HEART_RATE_TIME * 1000);
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    public void destroy() { ;
        this.stopMeasure();
        releaseAnim(mAnimatorHeartCurve);
        mMeasuringHeartRate = false;
        mTotalOffset = 0;
        mOffsetCount = 0;
        mValueHeartCurve = 0;
        mScaleValue = 1;
        mPathList.clear();
        mBitmapManager.clear();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private void stopMeasure() {
        releaseAnim(mScaleAnimator);
        this.invokeHeartRate(false);
        if (getHandler() != null) {
            getHandler().removeCallbacks(mRunnableUpdateHeartRate);
            getHandler().removeCallbacks(mRunnableStopMeasure);
        }
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    protected void onTimeTick() {
        this.queryStepAndCalories();
        if (mMeasuringHeartRate) {
            updateTime();
        } else {
            super.onTimeTick();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBgColor);

        this.paintHeartCurve(canvas);
        this.paintHeartText(canvas);
        this.paintTime(canvas);
        this.paintBottomIcons(canvas);
    }

    private final float[] mIntervals = {10, 9};

    private void paintHeartCurve(Canvas canvas) {
        if (mPathList.size() == 0) {
            return;
        }

        canvas.save();
        canvas.translate(-mValueHeartCurve, mPointScreenCenter.y);

        if (mOffsetCount < mPathList.size()) {
            PathBean pathBean = mPathList.get(mOffsetCount);
            if (pathBean.getType() == PathBean.TYPE_DASH_LINE) {
                mPaintHeartCurve.setPathEffect(new DashPathEffect(mIntervals, 0));
            } else {
                mPaintHeartCurve.setPathEffect(null);
            }
            canvas.drawPath(pathBean.getPath(), mPaintHeartCurve);
        }

        if (mOffsetCount + 1 < mPathList.size()) {
            PathBean pathBean = mPathList.get(mOffsetCount + 1);
            if (pathBean.getType() == PathBean.TYPE_DASH_LINE) {
                mPaintHeartCurve.setPathEffect(new DashPathEffect(mIntervals, 0));
            } else {
                mPaintHeartCurve.setPathEffect(null);
            }
            canvas.drawPath(pathBean.getPath(), mPaintHeartCurve);
        }

        canvas.restore();

        //盖住最右侧的虚线
        canvas.drawRect(2 * mPointScreenCenter.x - 40, mPointScreenCenter.y - 50
                , 2 * mPointScreenCenter.x, mPointScreenCenter.y + 60, mPaintRect);
    }

    private Path productWave(float startX, int waveNumber) {
        Path path = new Path();

        if (waveNumber < 2) {
            waveNumber = 2;
        }
        path.reset();
        final float margin = (1.0f * 2 * mPointScreenCenter.x - waveNumber * mWaveWidth) / (waveNumber + 1);
        path.moveTo(startX, 0);
        float offset = startX + margin;
        path.lineTo(offset, 0);

        for (int index = 0; index < waveNumber; index++) {
            path.lineTo(offset, 0);
            path.lineTo(offset + mWaveWidth / 4f, mWaveHeight * 0.6f);
            path.lineTo(offset + mWaveWidth / 4f * 3, -mWaveHeight * 0.4f);
            path.lineTo(offset + mWaveWidth, 0f);

            offset += (mWaveWidth + margin);
        }
        path.lineTo(offset, 0);

        return path;
    }

    private int getWaveNumber(int heartRate) {
        if (heartRate == 0) {
            return 0;
        } else if (heartRate > 0 && heartRate < 100) {
            return 2;
        } else if (heartRate >= 100 && heartRate < 110) {
            return 3;
        } else {
            return 4;
        }
    }

    private String getHeartText(int heartRate) {
        return heartRate == 0 ? DEFAULT_HEART_RATE : String.valueOf(heartRate);
    }

    private Path productDashLine(float startX) {
        Path path = new Path();
        path.moveTo(startX, 0);
        path.lineTo(startX + 2 * mPointScreenCenter.x, 0);
        return path;
    }

    private void paintHeartText(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleValue, mScaleValue, mRectFHeartRate.centerX(), mRectFHeartRate.centerY());

        //canvas.drawRect(mRectFHeartRate, mPaintHeartCurve);
        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_collection);
        canvas.drawBitmap(bitmap, mRectFHeartRate.centerX() - bitmap.getWidth() / 2f
                , mRectFHeartRate.centerY() - bitmap.getHeight() / 2f, null);
        mRectHeartRate.setEmpty();
        mPaintHeartRate.getTextBounds(mHeartRateText, 0, mHeartRateText.length(), mRectHeartRate);
        if (DEFAULT_HEART_RATE.equals(mHeartRateText)) {
            canvas.drawText(mHeartRateText, mRectFHeartRate.centerX() - mRectHeartRate.width() / 2f - DimenUtil.dip2px(mContext, 2)
                    , mRectFHeartRate.centerY() + DimenUtil.dip2px(mContext, 8), mPaintHeartRate);
        } else {
            canvas.drawText(mHeartRateText, mRectFHeartRate.centerX() - mRectHeartRate.width() / 2f - DimenUtil.dip2px(mContext, 2)
                    , mRectFHeartRate.centerY() + mRectHeartRate.height() / 2f - DimenUtil.dip2px(mContext, 5), mPaintHeartRate);
        }

        canvas.restore();
    }

    private void paintBottomIcons(Canvas canvas) {
        String text = mSportTime[0] + "" + mSportTime[1] + ":" + mSportTime[2] + "" + mSportTime[3];
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        canvas.drawText(text, mRectFSportTime.centerX() - mRectText.width() / 2f
                , mRectFSportTime.centerY() + mRectText.height() / 2f, mPaintText);

        text = mSteps[0] + "" + mSteps[1] + "" + mSteps[2] + "" + mSteps[3] + "" + mSteps[4] + "" + mSteps[5];
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        canvas.drawText(text, mRectFStep.centerX() - mRectText.width() / 2f
                , mRectFStep.centerY() + mRectText.height() / 2f, mPaintText);

        text = mCalories[0] + "" + mCalories[1] + "" + mCalories[2] + "" + mCalories[3] + "" + mCalories[4];
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        canvas.drawText(text, mRectFCalories.centerX() - mRectText.width() / 2f
                , mRectFCalories.centerY() + mRectText.height() / 2f, mPaintText);

        final int offsetAngle = 33;
        final int startAngle = 90 + offsetAngle;
        final int maxAngle = 360 - 2 * offsetAngle;
        mPaintArc.setColor(0xFF0092CC);
        canvas.drawArc(mRectFSportTime, startAngle, maxAngle, false, mPaintArc);
        int progressColor = 0xFF00B7FF;
        mPaintArc.setColor(progressColor);
        canvas.drawArc(mRectFSportTime, startAngle, maxAngle * mSportDataManager.getSportTimeProgress(), false, mPaintArc);
        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_sport_flag, progressColor);
        canvas.drawBitmap(bitmap, mRectFSportTime.centerX() - bitmap.getWidth() / 2f
                , mRectFSportTime.bottom - bitmap.getHeight() / 1.7f, null);

        mPaintArc.setColor(0xFF14CC2E);
        canvas.drawArc(mRectFStep, startAngle, maxAngle, false, mPaintArc);
        progressColor = 0xFF19FF39;
        mPaintArc.setColor(progressColor);
        canvas.drawArc(mRectFStep, startAngle, maxAngle * mSportDataManager.getStepsProgress(), false, mPaintArc);
        bitmap = mBitmapManager.get(R.drawable.icon_steps, progressColor);
        canvas.drawBitmap(bitmap, mRectFStep.centerX() - bitmap.getWidth() / 2f
                , mRectFStep.bottom - bitmap.getHeight() / 1.7f, null);

        mPaintArc.setColor(0xFFCC0808);
        canvas.drawArc(mRectFCalories, startAngle, maxAngle, false, mPaintArc);
        progressColor = 0xFFFF0A0A;
        mPaintArc.setColor(progressColor);
        canvas.drawArc(mRectFCalories, startAngle, maxAngle * mSportDataManager.getCaloriesProgress(), false, mPaintArc);
        bitmap = mBitmapManager.get(R.drawable.icon_calorie, progressColor);
        canvas.drawBitmap(bitmap, mRectFCalories.centerX() - bitmap.getWidth() / 2f
                , mRectFCalories.bottom - bitmap.getHeight() / 1.7f, null);
    }

    private void paintTime(Canvas canvas) {
        canvas.save();
        canvas.translate(mPointScreenCenter.x, mTimeTopOffset);

        int color = 0xFFFFFFFF;
        Bitmap bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
        final float leftTmp = -bitmap.getWidth() / 2f;
        float left = leftTmp;

        bitmap = this.getNumberBitmap(mHour % 10, color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, 0, null);
        bitmap = this.getNumberBitmap(mHour / 10, color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, 0, null);

        left = Math.abs(leftTmp);
        bitmap = this.getNumberBitmap(mMinute / 10, color);
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        bitmap = this.getNumberBitmap(mMinute % 10, color);
        canvas.drawBitmap(bitmap, left, 0, null);

        float top = bitmap.getHeight() / 2f;
        bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
        canvas.drawBitmap(bitmap, leftTmp, top - bitmap.getHeight() / 2f, null);

        canvas.restore();
    }

    private static final String AUTHORITY_HEALTH = "com.wiz.watch.health.provider";
    private final Uri URI_HEART_RATE = Uri.parse("content://" + AUTHORITY_HEALTH + "/heart_rate");
    private final int[] mSteps = new int[6];
    private final int[] mCalories = new int[5];
    private final int[] mSportTime = new int[4];

    private void queryStepAndCalories() {
        mSportDataManager.queryStepAndCalories();

        int steps = mSportDataManager.getSteps();
        mSteps[0] = steps / 100000;
        mSteps[1] = (steps % 100000) / 10000;
        mSteps[2] = (steps % 10000) / 1000;
        mSteps[3] = (steps % 1000) / 100;
        mSteps[4] = (steps % 100) / 10;
        mSteps[5] = steps % 10;

        int calories = mSportDataManager.getCalories();
        mCalories[0] = calories / 10000;
        mCalories[1] = (calories % 10000) / 1000;
        mCalories[2] = (calories % 1000) / 100;
        mCalories[3] = (calories % 100) / 10;
        mCalories[4] = calories % 10;

        long sportTime = mSportDataManager.getSportTime() / 1000;
        //sportTime = 60 * 90;
        int hour = (int) (sportTime / (60 * 60));
        int minute = (int) ((sportTime / 60) % 60);
        mSportTime[0] = hour / 10;
        mSportTime[1] = hour % 10;
        mSportTime[2] = minute / 10;
        mSportTime[3] = minute % 10;
    }

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.toString().startsWith(URI_HEART_RATE.toString())) {
                queryHeartRate();
            }
        }
    };

    private void invokeHeartRate(boolean start) {
        try {
            Bundle extras = new Bundle();
            extras.putBoolean("status", start);//true：开始测量， false：结束测量
            extras.putString("tag", mContext.getPackageName());
            mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY_HEALTH)
                    , "heart_rate/measure_real_time", null, extras);
        } catch (Exception e) {
            Log.e(mTAG, "measure real time error: " + e.toString(), new Exception());
        }
    }

    private void queryHeartRate() {
        Uri uri = Uri.parse(URI_HEART_RATE + "/query");
        String proValue = "value";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, new String[]{proValue}, null
                    , null, "time desc limit 1");
            if (cursor == null) {
                Log.w(mTAG, "cursor is null");
                return;
            }

            if (cursor.moveToFirst()) {
                mHeartRate = cursor.getInt(cursor.getColumnIndex(proValue));
                Log.d(mTAG, "current heart rate: " + mHeartRate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private Bitmap getNumberBitmap(int number, int color) {
        switch (number) {
            case 1:
                return mBitmapManager.get(R.drawable.paint_number_1, color, mScale);
            case 2:
                return mBitmapManager.get(R.drawable.paint_number_2, color, mScale);
            case 3:
                return mBitmapManager.get(R.drawable.paint_number_3, color, mScale);
            case 4:
                return mBitmapManager.get(R.drawable.paint_number_4, color, mScale);
            case 5:
                return mBitmapManager.get(R.drawable.paint_number_5, color, mScale);
            case 6:
                return mBitmapManager.get(R.drawable.paint_number_6, color, mScale);
            case 7:
                return mBitmapManager.get(R.drawable.paint_number_7, color, mScale);
            case 8:
                return mBitmapManager.get(R.drawable.paint_number_8, color, mScale);
            case 9:
                return mBitmapManager.get(R.drawable.paint_number_9, color, mScale);
            default:
                return mBitmapManager.get(R.drawable.paint_number_0, color, mScale);
        }
    }

}
