package com.structure.wallpaper.basic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated Prefer {@link com.structure.wallpaper.basic.view.FaceAnimView}.
 */
@Deprecated
public class NumberBeatView extends WatchFaceView {
    /**
     * 出场动画执行时间
     */
    public static final int ANIM_DURATION = 800;
    /**
     * 出场动画启动延时时间，目的是为了确保用初始值画完一次界面再启动动画
     */
    public static final int ANIM_DELAY = 100;
    private static final int MSG_UPDATE_TIME = 52;
    private int mDefaultHour, mDefaultMinute, mDefaultSecond;//08:36:55
    private int mCurrentHour;
    private int mCurrentMinute;
    private int mCurrentSecond;
    protected boolean mIsPlayingAppearanceAnim, mIsNeedSecond, mIsInitView = true;
    protected boolean mIsNeedPropertyAnim;
    private static final float REFRESH_RATE = 1 / 1500f;//The time interval for the surface to refresh

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

        updateTime();
        mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
        mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
        mSecondAnimatorValue = mSecond / 60f;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIsEditMode || mIsDimMode) {//编辑和微光模式下不用执行动画
            mHour = mCurrentHour;
            mMinute = mCurrentMinute;
            mSecond = mCurrentSecond;
        } else {
            if (mIsInitView) {//只在创建实例时执行动画
                mIsInitView = false;
                mIsPlayingAppearanceAnim = true;

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
                    getHandler().removeMessages(MSG_UPDATE_TIME);
                    getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 200);
                }
            }
        }
    }

    @Override
    protected void onTimeTick() {
        if (!mIsPlayingAppearanceAnim) {
            //Log.d(mTAG, "1111111: onTimeTick", new Exception());
            super.onTimeTick();
        }
    }

    @Override
    public void updateTime() {
        if (!mIsPlayingAppearanceAnim) {
            super.updateTime();
        }
        if (!mIsNeedPropertyAnim) {
            mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
            mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
            mSecondAnimatorValue = mSecond / 60f;
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (mIsDimMode) {
            return;
        }

        if (visible) {
            if (mIsNeedPropertyAnim) {
                this.startSecondPointerAnim();
            }
        } else {
            if (mIsNeedPropertyAnim) {
                releaseAnim(mSecondAnimator);
            } else {
                this.onAppearanceAnimFinished();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
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

            if ((mHour == mCurrentHour) && (mMinute == mCurrentMinute) && (mSecond == mCurrentSecond)) {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                this.onAppearanceAnimFinished();
            } else {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 25);
            }

            /*int hour = getHour();
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
            }*/

            invalidate();
        }
    }

    protected void onAppearanceAnimFinished() {
        getHandler().removeMessages(MSG_UPDATE_TIME);
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

    private ValueAnimator mAppearanceAnimMinute, mAppearanceAnimHour, mAppearanceAnimSecond;
    private final LinearInterpolator mInterpolator = new LinearInterpolator();
    protected ValueAnimator mSecondAnimator;
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
        mAppearanceAnimHour.setFloatValues(mHourAnimatorValue, endAnimatorValue);
        mAppearanceAnimHour.start();

        mMinuteAnimatorValue = mDefaultMinute / 60f;
        endAnimatorValue = (mMinute + mSecond / 60f) / 60;
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
                    float temp = (float) animation.getAnimatedValue();
                    if (temp < mSecondAnimatorValue){//如果mSecondAnimatorValue 比 temp还大，说明出问题了，需要吧如果mSecondAnimatorValue重置
                        mSecondAnimatorValue = 0;
                    }
                    if (temp - mSecondAnimatorValue > REFRESH_RATE) {//control the refresh rate of the surface
                        updateTime();
                        mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
                        mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
                        mSecondAnimatorValue = (Float) animation.getAnimatedValue();
                        //Log.d(mTAG, "pointer anim: " + mHourAnimatorValue + ", " + mMinuteAnimatorValue + ", " + mSecondAnimatorValue);

                        invalidate();
                    }
                }
            }
        });

        mSecondAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSecondAnimatorValue = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mSecondAnimatorValue = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mSecondAnimatorValue = 0;
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
        float offsetValue = (Calendar.getInstance().get(Calendar.SECOND) +  Calendar.getInstance().get(Calendar.MILLISECOND) / 1000f)/ 60f;
        mSecondAnimator.setFloatValues(offsetValue, 1 + offsetValue);
        mSecondAnimator.start();
    }

}
