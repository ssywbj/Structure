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
    private static final int MSG_UPDATE_TIME = 52;
    private int mDefaultHour, mDefaultMinute, mDefaultSecond; //08:36:55
    protected boolean mIsPlayingAppearAnim, mIsInitView = true;
    protected boolean mIsAppearAnimPointer, mIsAppearAnimNumber;

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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIsInitView) {//只在创建实例时执行动画
            mIsInitView = false;
            mIsPlayingAppearAnim = true;

            if (mIsAppearAnimPointer) {
                this.startAppearAnim();
            }

            /*if (mIsAppearAnimNumber) {
                mHour = mDefaultHour;
                mMinute = mDefaultMinute;
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 200);
            }*/
        }
    }

    /*@Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            if (mIsAppearAnimPointer) {
                this.startSecondPointerAnim();
            }
        } else {
            if (mIsAppearAnimPointer) {
                releaseAnim(mSecondAnimator);
            } else {
                this.onAppearAnimFinished();
            }
        }
    }*/

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.releaseAnim(mAppearAnimator);
        this.releaseAnim(mSecondAnimator);
    }

    /*@Override
    protected void dispatchMsg(Message msg) {
        if (msg.what == MSG_UPDATE_TIME) {
            if (mHour != mCurrentHour) {
                if (mDefaultHour < mCurrentHour) {
                    mHour++;
                } else if (mDefaultHour > mCurrentHour) {
                    mHour--;
                }
            }

            if (mMinute != mCurrentMinute) {
                if (mDefaultMinute < mCurrentMinute) {
                    mMinute++;
                } else if (mDefaultMinute > mCurrentMinute) {
                    mMinute--;
                }
            }

            if (mSecond != mCurrentSecond) {
                if (mDefaultSecond < mCurrentSecond) {
                    mSecond++;
                } else if (mDefaultSecond > mCurrentSecond) {
                    mSecond--;
                }
            }

            if ((mHour == mCurrentHour) && (mMinute == mCurrentMinute) && (mSecond == mCurrentSecond)) {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                this.onAppearanceAnimFinished();
            } else {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 25);
            }

            *//*int hour = getHour();
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int second = Calendar.getInstance().get(Calendar.SECOND);
            if (hour < mHour) {
                mHour--;
            } else if (hour > mHour) {
                mHour++;
            }
            if (minute < mMinute) {
                mMinute--;
            } else if (minute > mMinute) {
                mMinute++;
            }
            if (second < mSecond) {
                mSecond--;
            } else if (second > mSecond) {
                mSecond++;
            }
            if (hour == mHour && minute == mMinute && second == mSecond) {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                this.onAppearanceAnimFinished();
            } else {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 50);
            }*//*

            invalidate();
        }
    }*/

    protected void onAppearAnimFinished() {
        getHandler().removeMessages(MSG_UPDATE_TIME);
        mIsPlayingAppearAnim = false;
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

    private static final String PROPERTY_HOUR = "property_hour";
    private static final String PROPERTY_MINUTE = "property_minute";
    private static final String PROPERTY_SECOND = "property_second";
    private ValueAnimator mAppearAnimator, mSecondAnimator;

    private void initAppearAnim() {
        mAppearAnimator = ValueAnimator.ofPropertyValuesHolder();
        mAppearAnimator.addUpdateListener(animation -> {
            Object animatedValue = animation.getAnimatedValue(PROPERTY_HOUR);
            if (animatedValue instanceof Float) {
                mHourRatio = (Float) animatedValue;
                Log.d(mTAG, "hour pointer anim: " + mHourRatio);
            }
            animatedValue = animation.getAnimatedValue(PROPERTY_MINUTE);
            if (animatedValue instanceof Float) {
                mMinuteRatio = (Float) animatedValue;
                Log.d(mTAG, "minute pointer anim: " + mMinuteRatio);
            }
            animatedValue = animation.getAnimatedValue(PROPERTY_SECOND);
            if (animatedValue instanceof Float) {
                mSecondRatio = (Float) animatedValue;
                Log.d(mTAG, "second pointer anim: " + mSecondRatio);
            }
            //invalidate();
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
            mHourRatio = mDefaultHour / 12f;
            endAnimatorValue = (mHour + mMinute / 60f) / 12;
            valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_HOUR, mHourRatio, endAnimatorValue));
        }
        if (mMinute != mDefaultMinute) {
            mMinuteRatio = mDefaultMinute / 60f;
            endAnimatorValue = (mMinute + mSecond / 60f) / 60;
            valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_MINUTE, mMinuteRatio, endAnimatorValue));
        }
        if (mSecond != mDefaultSecond) {
            mSecondRatio = mDefaultSecond / 60f;
            endAnimatorValue = (mSecond + ANIM_DURATION / 1000f) / 60;
            valuesHolderList.add(PropertyValuesHolder.ofFloat(PROPERTY_SECOND, mSecondRatio, endAnimatorValue));
        }

        if (valuesHolderList.size() == 0) {
            return;
        }
        final PropertyValuesHolder[] propertyValuesHolders = new PropertyValuesHolder[valuesHolderList.size()];
        valuesHolderList.toArray(propertyValuesHolders);
        mAppearAnimator.setValues(propertyValuesHolders);
        mAppearAnimator.start();
    }

    private void initSecondPointerAnim() {
        mSecondAnimator = ValueAnimator.ofFloat(0, 0);//属性动画
        mSecondAnimator.addUpdateListener(animation -> {
            if (animation.getAnimatedValue() instanceof Float) {
                updateTime();
                mSecondRatio = (Float) animation.getAnimatedValue();
                //Log.d(mTAG, "pointer anim: " + mHourAnimatorValue + ", " + mMinuteAnimatorValue + ", " + mSecondAnimatorValue);

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
