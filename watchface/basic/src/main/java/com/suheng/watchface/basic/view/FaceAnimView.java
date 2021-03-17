package com.suheng.watchface.basic.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FaceAnimView extends WatchFaceView {
    public static final int TIME_NONE = -1;
    private static final int ANIM_DURATION = 800;
    private int mDefaultHour, mDefaultMinute, mDefaultSecond; //08:36:55
    protected boolean mIsPlayingAppearAnim;
    protected boolean mIsAppearAnimPointer, mIsAppearAnimNumber;

    private static final String PROPERTY_HOUR_POINTER = "property_hour_pointer";
    private static final String PROPERTY_MINUTE_POINTER = "property_minute_pointer";
    private static final String PROPERTY_SECOND_POINTER = "property_second_pointer";
    private static final String PROPERTY_HOUR_NUMBER = "property_hour_number";
    private static final String PROPERTY_MINUTE_NUMBER = "property_minute_number";
    private static final String PROPERTY_SECOND_NUMBER = "property_second_number";
    private ValueAnimator mAppearAnimator, mSecondAnimator;

    public FaceAnimView(Context context) {
        super(context);
        this.initView();
    }

    public FaceAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        updateTime();
        mDefaultHour = mHour;
        mDefaultMinute = mMinute;
        mDefaultSecond = mSecond;

        post(() -> {
            Log.d(mTAG, "post(new Runnable() {})");
            mIsPlayingAppearAnim = true;
            startAppearAnim();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(mTAG, "onAttachedToWindow, onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.releaseAnim(mAppearAnimator);
        this.releaseAnim(mSecondAnimator);
    }

    private void initAppearAnim() {
        mAppearAnimator = ValueAnimator.ofPropertyValuesHolder();
        mAppearAnimator.addUpdateListener(animation -> {
            Object animatedValue;
            if (mIsAppearAnimPointer) {
                animatedValue = animation.getAnimatedValue(PROPERTY_HOUR_POINTER);
                if (animatedValue instanceof Float) {
                    mHourRatio = (float) animatedValue;
                    Log.i(mTAG, "hour pointer anim: " + mHourRatio);
                }
                animatedValue = animation.getAnimatedValue(PROPERTY_MINUTE_POINTER);
                if (animatedValue instanceof Float) {
                    mMinuteRatio = (float) animatedValue;
                    Log.i(mTAG, "minute pointer anim: " + mMinuteRatio);
                }
                animatedValue = animation.getAnimatedValue(PROPERTY_SECOND_POINTER);
                if (animatedValue instanceof Float) {
                    mSecondRatio = (float) animatedValue;
                    Log.i(mTAG, "second pointer anim: " + mSecondRatio);
                }
            }

            if (mIsAppearAnimNumber) {
                animatedValue = animation.getAnimatedValue(PROPERTY_HOUR_NUMBER);
                if (animatedValue instanceof Integer) {
                    mHour = (int) animatedValue;
                    Log.d(mTAG, "hour number anim: " + mHour);
                }
                animatedValue = animation.getAnimatedValue(PROPERTY_MINUTE_NUMBER);
                if (animatedValue instanceof Integer) {
                    mMinute = (int) animatedValue;
                    Log.d(mTAG, "minute number anim: " + mMinute);
                }
                animatedValue = animation.getAnimatedValue(PROPERTY_SECOND_NUMBER);
                if (animatedValue instanceof Integer) {
                    mSecond = (int) animatedValue;
                    Log.d(mTAG, "second number anim: " + mSecond);
                }
            }

            invalidate();
        });
        mAppearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onAppearAnimFinished();
            }
        });
        mAppearAnimator.setDuration(ANIM_DURATION);
        mAppearAnimator.setInterpolator(new LinearInterpolator());
    }

    private void startAppearAnim() {
        if (mAppearAnimator == null) {
            this.initAppearAnim();
        }
        if (mAppearAnimator.isRunning()) {
            return;
        }

        final List<PropertyValuesHolder> valuesHolderList = new ArrayList<>();
        float endAnimatorValue;
        if (mHour != mDefaultHour) {
            if (mIsAppearAnimPointer) {
                mHourRatio = mDefaultHour / 12f;
                endAnimatorValue = (mHour + mMinute / 60f) / 12;
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_HOUR_POINTER, mHourRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_HOUR_NUMBER, mDefaultHour, mHour));
            }
        }
        if (mMinute != mDefaultMinute) {
            if (mIsAppearAnimPointer) {
                mMinuteRatio = mDefaultMinute / 60f;
                endAnimatorValue = (mMinute + mSecond / 60f) / 60;
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_MINUTE_POINTER, mMinuteRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_MINUTE_NUMBER, mDefaultMinute, mMinute));
            }
        }
        if (mSecond != mDefaultSecond) {
            if (mIsAppearAnimPointer) {
                mSecondRatio = mDefaultSecond / 60f;
                endAnimatorValue = (mSecond + ANIM_DURATION / 1000f) / 60;
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_SECOND_POINTER, mSecondRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_SECOND_NUMBER, mDefaultSecond, mSecond));
            }
        }

        if (valuesHolderList.size() == 0) {
            return;
        }
        final PropertyValuesHolder[] propertyValuesHolders = new PropertyValuesHolder[valuesHolderList.size()];
        valuesHolderList.toArray(propertyValuesHolders);
        mAppearAnimator.setValues(propertyValuesHolders);
        mAppearAnimator.start();
    }

    /**
     * 设置默认显示的时间。若没有默认显示的时间，可以不调用此方法。
     *
     * @param defaultHour   小时，若没有传{@link #TIME_NONE}
     * @param defaultMinute 分钟，若没有传{@link #TIME_NONE}
     * @param defaultSecond 秒钟，若没有传{@link #TIME_NONE}
     */
    public void setDefaultTime(int defaultHour, int defaultMinute, int defaultSecond) {
        if (defaultHour > TIME_NONE) {
            mDefaultHour = defaultHour;
        }
        if (defaultMinute > TIME_NONE) {
            mDefaultMinute = defaultMinute;
        }
        if (defaultSecond > TIME_NONE) {
            mDefaultSecond = defaultSecond;
        }
    }

    public void setAppearAnimPointer(boolean appearAnimPointer) {
        mIsAppearAnimPointer = appearAnimPointer;
    }

    public void setAppearAnimNumber(boolean appearAnimNumber) {
        mIsAppearAnimNumber = appearAnimNumber;
    }

    protected void onAppearAnimFinished() {
        mIsPlayingAppearAnim = false;
    }

    private void initSecondPointerAnim() {
        mSecondAnimator = ValueAnimator.ofFloat(0, 0);//属性动画
        mSecondAnimator.addUpdateListener(animation -> {
            if (animation.getAnimatedValue() instanceof Float) {
                updateTime();
                mSecondRatio = (Float) animation.getAnimatedValue();
                //Log.d(mTAG, "pointer anim: " + mHourRatio + ", " + mMinuteRatio + ", " + mSecondRatio);
                invalidate();
            }
        });

        mSecondAnimator.setDuration(TimeUnit.MINUTES.toMillis(1L));
        mSecondAnimator.setInterpolator(new LinearInterpolator());
        mSecondAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    protected void startSecondPointerAnim() {
        if (mSecondAnimator == null) {
            this.initSecondPointerAnim();
        }
        if (mSecondAnimator.isRunning()) {
            mSecondAnimator.cancel();
        }
        Calendar calendar = Calendar.getInstance();
        float offsetValue = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f) / 60f;
        mSecondAnimator.setFloatValues(offsetValue, 1 + offsetValue);
        mSecondAnimator.start();
    }

    protected void releaseAnim(ValueAnimator animator) {
        if (animator == null) {
            return;
        }
        if (animator.isRunning()) {
            animator.cancel();
        }
    }

}
