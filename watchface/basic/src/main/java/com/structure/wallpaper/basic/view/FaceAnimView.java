package com.structure.wallpaper.basic.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class FaceAnimView extends WatchFaceView2 {
    private static final String PROPERTY_HOUR_POINTER = "property_hour_pointer";
    private static final String PROPERTY_MINUTE_POINTER = "property_minute_pointer";
    private static final String PROPERTY_SECOND_POINTER = "property_second_pointer";
    private static final String PROPERTY_HOUR_NUMBER = "property_hour_number";
    private static final String PROPERTY_MINUTE_NUMBER = "property_minute_number";
    private static final String PROPERTY_SECOND_NUMBER = "property_second_number";
    public static final int TIME_NONE = -1;
    public static final int ANIM_DURATION = 800;
    private int mDefaultHour, mDefaultMinute, mDefaultSecond; //08:36:55
    private boolean mIsAppearAnimPointer, mIsAppearAnimNumber; //出场所需的指针或数字动画标识，若都为false，则不做任何出场动画
    private ValueAnimator mAppearAnimator, mSecondAnimator;
    private long mCurrentTimeMillis; //用于控制秒针动画的刷新频率
    private boolean mIsAppendTime = true;

    public FaceAnimView(Context context) {
        super(context);
        this.initView();
    }

    public FaceAnimView(Context context, boolean isEditMode) {
        super(context);
        mIsEditMode = isEditMode;

        this.initView();
    }

    public FaceAnimView(Context context, boolean isEditMode, boolean isHour24Scale) {
        super(context);
        mIsEditMode = isEditMode;
        mIsHour24Scale = isHour24Scale;

        this.initView();
    }

    public FaceAnimView(Context context, boolean isEditMode, boolean isHour24Scale, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsHour24Scale = isHour24Scale;
        mIsDimMode = isDimMode;

        this.initView();
    }

    public FaceAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FaceAnimView);
        mIsEditMode = typedArray.getBoolean(R.styleable.FaceAnimView_isEditMode, false);
        mIsHour24Scale = typedArray.getBoolean(R.styleable.FaceAnimView_isHour24Scale, false);
        mIsDimMode = typedArray.getBoolean(R.styleable.FaceAnimView_isDimMode, false);
        typedArray.recycle();

        this.initView();
    }

    private void initView() {
        updateTime();
        mDefaultHour = mHour;
        mDefaultMinute = mMinute;
        mDefaultSecond = mSecond;
        mAppearAnimator = ValueAnimator.ofPropertyValuesHolder();

        post(new Runnable() {
            @Override
            public void run() {
                //Log.d(mTAG, "post(new Runnable() {})");
                if (mIsEditMode || mIsDimMode) {
                    return;
                }
                startAppearAnim();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Log.d(mTAG, "onAttachedToWindow, onAttachedToWindow");
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        //Log.d(mTAG, "onVisibilityChanged: " + visible);
        if (mIsDimMode) {
            return;
        }

        if (visible) {
            registerTimeChangedReceiver();

            if (mAppearAnimator == null) {
                //可见状态下，在出场动画播完时才执行onAppearAnimFinished()的建议用法，可防止出场动画播放时因为
                //可见状态的改变导致时间值被更新
                this.onAppearAnimFinished();
            }
        } else {
            unregisterTimeChangedReceiver();

            mCurrentTimeMillis = 0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Log.d(mTAG, "onDetachedFromWindow, onDetachedFromWindow");
        this.releaseAnim(mAppearAnimator);
        this.releaseAnim(mSecondAnimator);
    }

    @Override
    protected void onTimeChanged() {
        if (mSecondAnimator == null) {
            super.onTimeChanged();
        } else {
            //手动改变时间，系统秒数会从0开始计时。如果秒针的动画在执行，那么要先停掉，把新秒数设置后再重新启动。
            this.startSecondPointerAnim();
        }
    }

    private void startAppearAnim() {
        final List<PropertyValuesHolder> valuesHolderList = new ArrayList<>();
        float endAnimatorValue;
        if (mHour != mDefaultHour) {
            if (mIsAppearAnimPointer) {
                mHourRatio = 1f * mDefaultHour / (mIsHour24Scale ? 24 : 12);
                endAnimatorValue = (mHour + mMinute / 60f) / (mIsHour24Scale ? 24 : 12);
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_HOUR_POINTER, mHourRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_HOUR_NUMBER, mDefaultHour, mHour));
            }
        }
        if (mMinute != mDefaultMinute) {
            if (mIsAppearAnimPointer) {
                mMinuteRatio = mDefaultMinute / 60f;
                endAnimatorValue = (mMinute + (mIsAppendTime ? mSecond / 60f : 0)) / 60f;
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_MINUTE_POINTER, mMinuteRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_MINUTE_NUMBER, mDefaultMinute, mMinute));
            }
        }
        if (mSecond != mDefaultSecond) {
            if (mIsAppearAnimPointer) {
                mSecondRatio = mDefaultSecond / 60f;
                endAnimatorValue = (mSecond + (mIsAppendTime ? ANIM_DURATION / 1000f : 0)) / 60f;
                valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_SECOND_POINTER, mSecondRatio, endAnimatorValue));
            }

            if (mIsAppearAnimNumber) {
                valuesHolderList.add(PropertyValuesHolder.ofInt(PROPERTY_SECOND_NUMBER, mDefaultSecond, mSecond));
            }
        }

        if (valuesHolderList.size() == 0) {
            mAppearAnimator = null;
            onAppearAnimFinished();
            return;
        }

        final PropertyValuesHolder[] propertyValuesHolders = new PropertyValuesHolder[valuesHolderList.size()];
        valuesHolderList.toArray(propertyValuesHolders);
        mAppearAnimator.setValues(propertyValuesHolders);
        mAppearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue;
                if (mIsAppearAnimPointer) {
                    animatedValue = animation.getAnimatedValue(PROPERTY_HOUR_POINTER);
                    if (animatedValue instanceof Float) {
                        mHourRatio = (float) animatedValue;
                        //Log.i(mTAG, "hour pointer anim: " + mHourRatio);
                    }
                    animatedValue = animation.getAnimatedValue(PROPERTY_MINUTE_POINTER);
                    if (animatedValue instanceof Float) {
                        mMinuteRatio = (float) animatedValue;
                        //Log.i(mTAG, "minute pointer anim: " + mMinuteRatio);
                    }
                    animatedValue = animation.getAnimatedValue(PROPERTY_SECOND_POINTER);
                    if (animatedValue instanceof Float) {
                        mSecondRatio = (float) animatedValue;
                        //Log.i(mTAG, "second pointer anim: " + mSecondRatio);
                    }
                }

                if (mIsAppearAnimNumber) {
                    animatedValue = animation.getAnimatedValue(PROPERTY_HOUR_NUMBER);
                    if (animatedValue instanceof Integer) {
                        mHour = (int) animatedValue;
                        //Log.d(mTAG, "hour number anim: " + mHour);
                    }
                    animatedValue = animation.getAnimatedValue(PROPERTY_MINUTE_NUMBER);
                    if (animatedValue instanceof Integer) {
                        mMinute = (int) animatedValue;
                        //Log.d(mTAG, "minute number anim: " + mMinute);
                    }
                    animatedValue = animation.getAnimatedValue(PROPERTY_SECOND_NUMBER);
                    if (animatedValue instanceof Integer) {
                        mSecond = (int) animatedValue;
                        //Log.d(mTAG, "second number anim: " + mSecond);
                    }
                }

                invalidate();
            }
        });
        mAppearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAppearAnimator = null;
                onAppearAnimFinished();
            }
        });
        mAppearAnimator.setDuration(ANIM_DURATION);
        mAppearAnimator.setInterpolator(new LinearInterpolator());
        mAppearAnimator.start();
    }

    public void needAppearAnimPointer() {
        mIsAppearAnimPointer = true;
    }

    public void needAppearAnimNumber() {
        mIsAppearAnimNumber = true;
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

    public void setAppendTime(boolean appendTime) {
        mIsAppendTime = appendTime;
    }

    /**
     * 出场动画完成时的回调。<p>建议用法：在此方法做所需数据的监听、开启时间的刷新等工作。
     * <p><em>Note：若在此方法进行了注册、监听等状态操作，要记得在表盘不可见状态时进行反注销操作，具体可见其实现类的用法。</em>
     */
    protected abstract void onAppearAnimFinished();

    private void initSecondPointerAnim() {
        mSecondAnimator = ValueAnimator.ofFloat(0, 0);
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (System.currentTimeMillis() - mCurrentTimeMillis < 40) { //控制秒针刷新频率
                    return;
                }
                mCurrentTimeMillis = System.currentTimeMillis();

                if (animation.getAnimatedValue() instanceof Float) {
                    updateTime();
                    mSecondRatio = (Float) animation.getAnimatedValue();
                    //Log.d(mTAG, "pointer anim: " + mHourRatio + ", " + mMinuteRatio + ", " + mSecondRatio);
                    invalidate();
                }
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

        mCurrentTimeMillis = 0;

        Calendar calendar = Calendar.getInstance();
        float offsetValue = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f) / 60f;
        mSecondAnimator.setFloatValues(offsetValue, 1 + offsetValue);
        mSecondAnimator.start();
    }

    protected void stopSecondPointerAnim() {
        this.releaseAnim(mSecondAnimator);
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
