package com.wiz.watch.dreamservice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NumberBeatView extends WatchFaceView {
    private static final int MSG_UPDATE_TIME = 52;
    private int mDefaultHour, mDefaultMinute, mDefaultSecond;//08:36:55
    private int mCurrentHour;
    private int mCurrentMinute;
    private int mCurrentSecond;
    protected boolean mIsPlayingAppearanceAnim, mIsNeedSecond, mIsInitView = true;
    protected boolean mIsNeedPropertyAnim;

    public NumberBeatView(Context context) {
        super(context);
        this.initView();
    }

    public NumberBeatView(Context context, boolean isEditMode) {
        super(context);
        mIsEditMode = isEditMode;
        this.initView();
    }

    public NumberBeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mCurrentHour = getHour();
        mCurrentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mCurrentSecond = Calendar.getInstance().get(Calendar.SECOND);
        Log.d(mTAG, "current hour: " + mCurrentHour + ", current minute: "
                + mCurrentMinute + ", current second: " + mCurrentSecond);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Log.d(mTAG, "mIsEditMode: " + mIsEditMode + ", mIsInitView: " + mIsInitView + ", mIsPropertyAnim: " + mIsNeedPropertyAnim);
        if (mIsEditMode) {//编辑模式下不用执行动画
            mHour = mCurrentHour;
            mMinute = mCurrentMinute;
            mSecond = mCurrentSecond;
        } else {
            if (mIsInitView) {//只在创建实例时执行动画
                mIsInitView = false;

                if (mIsNeedPropertyAnim) {
                    this.startAppearanceAnim();
                } else {
                    mHour = mDefaultHour;
                    mMinute = mDefaultMinute;
                    if (mIsNeedSecond) {
                        mSecond = mDefaultSecond;
                    } else {
                        mSecond = mCurrentSecond;
                    }
                    //目的是为了让第一帧停留的时间久一些，也就是让用户看到初始值的显示
                    getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 200);
                    //Log.d(mTAG, "mHour: " + mHour + ", mMinute: " + mMinute + ", mSecond: " + mSecond);
                }
            }
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            if (mIsNeedPropertyAnim) {
                this.startSecondPointerAnim();
            }
        } else {
            if (mIsNeedPropertyAnim) {
                releaseAnim(mSecondAnimator);
            } else {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                mIsPlayingAppearanceAnim = false;
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        getHandler().removeMessages(MSG_UPDATE_TIME);
        releaseAnim(mAppearanceAnimHour);
        releaseAnim(mAppearanceAnimMinute);
        releaseAnim(mAppearanceAnimSecond);
        releaseAnim(mSecondAnimator);
    }

    @Override
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

            //Log.d(mTAG, "msg, mHour: " + mHour + ", mMinute: " + mMinute + ", mSecond: " + mSecond);

            if ((mHour == mCurrentHour) && (mMinute == mCurrentMinute) && (mSecond == mCurrentSecond)) {
                //Log.d(mTAG, "------- msg finish -------");
                getHandler().removeMessages(MSG_UPDATE_TIME);
                this.onAppearanceAnimFinished();
            } else {
                mIsPlayingAppearanceAnim = true;
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 25);
            }

            invalidate();
        }
    }

    protected void onAppearanceAnimFinished() {
        mIsPlayingAppearanceAnim = false;
    }

    public void setDefaultTime(int defaultHour, int defaultMinute) {
        mDefaultHour = defaultHour;
        mDefaultMinute = defaultMinute;
    }

    public void setDefaultTime(int defaultHour, int defaultMinute, int defaultSecond) {
        mDefaultHour = defaultHour;
        mDefaultMinute = defaultMinute;
        mDefaultSecond = defaultSecond;
        mIsNeedSecond = true;
    }

    public void setNeedPropertyAnim(boolean needPropertyAnim) {
        mIsNeedPropertyAnim = needPropertyAnim;

        if (mIsNeedPropertyAnim && mAppearanceAnimSecond == null) {
            this.initAppearanceAnim();
        }
    }

    @Override
    public void updateTime() {
        super.updateTime();
        mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
        mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
        mSecondAnimatorValue = mSecond / 60f;
    }

    private ValueAnimator mAppearanceAnimMinute, mAppearanceAnimHour, mAppearanceAnimSecond;
    private final LinearInterpolator mInterpolator = new LinearInterpolator();
    private ValueAnimator mSecondAnimator;
    protected float mHourAnimatorValue, mMinuteAnimatorValue, mSecondAnimatorValue;

    private void initAppearanceAnim() {
        mAppearanceAnimHour = ValueAnimator.ofFloat(0, 0);
        mAppearanceAnimHour.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mHourAnimatorValue = (Float) animation.getAnimatedValue();
                }
            }
        });
        mAppearanceAnimHour.setDuration(ANIM_DURATION);
        mAppearanceAnimHour.setStartDelay(ANIM_DELAY);
        mAppearanceAnimHour.setInterpolator(mInterpolator);

        mAppearanceAnimMinute = ValueAnimator.ofFloat(0, 0);//属性动画
        mAppearanceAnimMinute.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mMinuteAnimatorValue = (Float) animation.getAnimatedValue();
                }
            }
        });
        mAppearanceAnimMinute.setDuration(ANIM_DURATION);
        mAppearanceAnimMinute.setStartDelay(ANIM_DELAY);
        mAppearanceAnimMinute.setInterpolator(mInterpolator);

        mAppearanceAnimSecond = ValueAnimator.ofFloat(0, 0);//属性动画
        mAppearanceAnimSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mSecondAnimatorValue = (Float) animation.getAnimatedValue();
                    //Log.i(mTAG, "appearance anim: " + mSecondAnimatorValue);

                    mIsPlayingAppearanceAnim = true;
                    invalidate();
                }
            }
        });
        mAppearanceAnimSecond.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAppearanceAnimSecond = null;
                onAppearanceAnimFinished();
                //startSecondPointerAnim();
            }
        });
        mAppearanceAnimSecond.setDuration(ANIM_DURATION);
        mAppearanceAnimSecond.setStartDelay(ANIM_DELAY);
        mAppearanceAnimSecond.setInterpolator(mInterpolator);
    }

    private void startAppearanceAnim() {
        if (mAppearanceAnimSecond == null || mAppearanceAnimSecond.isRunning()) {
            return;
        }

        updateTime();

        mHourAnimatorValue = mDefaultHour / 12f;
        float endAnimatorValue = (mHour + mMinute / 60f) / 12;
        //Log.d(mTAG, "hour start anim value: " + mHourAnimatorValue + ", end anim value: " + endAnimatorValue);
        mAppearanceAnimHour.setFloatValues(mHourAnimatorValue, endAnimatorValue);
        mAppearanceAnimHour.start();

        mMinuteAnimatorValue = mDefaultMinute / 60f;
        endAnimatorValue = (mMinute + mSecond / 60f) / 60;
        //Log.d(mTAG, "minute start anim value: " + mMinuteAnimatorValue + ", end anim value: " + endAnimatorValue);
        mAppearanceAnimMinute.setFloatValues(mMinuteAnimatorValue, endAnimatorValue);
        mAppearanceAnimMinute.start();

        mSecondAnimatorValue = mDefaultSecond / 60f;
        endAnimatorValue = (mSecond + (ANIM_DURATION + ANIM_DELAY) / 1000f) / 60;
        //Log.d(mTAG, "second start anim value: " + mSecondAnimatorValue + ", end anim value: " + endAnimatorValue);
        mAppearanceAnimSecond.setFloatValues(mSecondAnimatorValue, endAnimatorValue);
        mAppearanceAnimSecond.start();
    }

    private void initSecondPointerAnim() {
        mSecondAnimator = ValueAnimator.ofFloat(0, 0);//属性动画
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    updateTime();
                    mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
                    mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
                    mSecondAnimatorValue = (Float) animation.getAnimatedValue();
                    //Log.d(mTAG, "pointer anim: " + mHourAnimatorValue + ", " + mMinuteAnimatorValue + ", " + mSecondAnimatorValue);

                    invalidate();
                }
            }
        });
        mSecondAnimator.setDuration(TimeUnit.MINUTES.toMillis(1L));
        mSecondAnimator.setInterpolator(mInterpolator);
        mSecondAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    protected void startSecondPointerAnim() {
        if (mAppearanceAnimSecond != null) {
            return;
        }

        if (mSecondAnimator == null) {
            this.initSecondPointerAnim();
        }
        if (mSecondAnimator.isRunning()) {
            mSecondAnimator.cancel();
        }
        float offsetValue = Calendar.getInstance().get(Calendar.SECOND) / 60f;
        mSecondAnimator.setFloatValues(offsetValue, 1 + offsetValue);
        mSecondAnimator.start();
    }

}
