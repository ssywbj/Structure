package com.wiz.watchface.lightspot.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.lightspot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MaskBallView extends View {
    //private static final String TAG = MaskBallView.class.getSimpleName();
    private Context mContext;
    private final Map<Integer, List<Spot>> mMapSpotList = new HashMap<>();
    private final Map<Integer, ValueAnimator> mMapValueAnimator = new ConcurrentHashMap<>();
    private ValueAnimator mWorkAnimator;
    private float mDistance;
    private int mRadiusBound;
    private int mStyle;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            productSpots();
            getHandler().postDelayed(mRunnable, 1000);
        }
    };

    public MaskBallView(Context context) {
        super(context);
        this.init();
    }

    public MaskBallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mContext = getContext();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
        mRadiusBound = DimenUtil.dip2px(mContext, 25);

        mWorkAnimator = ValueAnimator.ofFloat(0f, 1f);
        mWorkAnimator.setDuration(5000);
        mWorkAnimator.setInterpolator(new LinearInterpolator());
        mWorkAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mWorkAnimator.addUpdateListener(animation -> invalidate());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Log.d(TAG, "onAttachedToWindow, w: " + getWidth() + ", h: " + getHeight());
        post(this::productSpots);
        mWorkAnimator.start();
        getHandler().postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHandler().removeCallbacks(mRunnable);
        if (mWorkAnimator.isRunning()) {
            mWorkAnimator.cancel();
        }
        for (Map.Entry<Integer, ValueAnimator> entry : mMapValueAnimator.entrySet()) {
            ValueAnimator valueAnimator = mMapValueAnimator.get(entry.getKey());
            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
        }
        mMapValueAnimator.clear();
        mMapSpotList.clear();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDistance = (float) (Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2)) / 2);
        //mDistance = Math.max(w / 2f, h / 2f);
        //Log.d(TAG, "onSizeChanged, w: " + w + ", h: " + h + ", distance: " + mDistance);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        boolean visible = (visibility == VISIBLE);
        //Log.d(TAG, "visibility changed: " + visibility + ", " + visible);
        this.onVisibilityChanged(visible);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        boolean screenOn = (screenState == SCREEN_STATE_ON);
        //Log.d(TAG, "screen changed: " + screenState + ", " + screenOn + ", visibility: " + getVisibility());
        this.onVisibilityChanged(screenOn);
    }

    private void onVisibilityChanged(boolean visible) {
        if (visible) {
            getHandler().postDelayed(mRunnable, 1000);
            if (mWorkAnimator.isPaused()) {
                mWorkAnimator.resume();
            }
            for (Map.Entry<Integer, ValueAnimator> animatorEntry : mMapValueAnimator.entrySet()) {
                if (animatorEntry.getValue().isPaused()) {
                    animatorEntry.getValue().resume();
                }
            }
        } else {
            getHandler().removeCallbacks(mRunnable);
            if (mWorkAnimator.isRunning()) {
                mWorkAnimator.pause();
            }
            for (Map.Entry<Integer, ValueAnimator> animatorEntry : mMapValueAnimator.entrySet()) {
                if (animatorEntry.getValue().isRunning()) {
                    animatorEntry.getValue().pause();
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        for (Map.Entry<Integer, List<Spot>> entry : mMapSpotList.entrySet()) {
            final List<Spot> spotList = mMapSpotList.get(entry.getKey());
            if (spotList != null) {
                for (Spot spot : spotList) {
                    canvas.drawCircle(spot.getStartX(), spot.getStartY(), spot.getRadius(), spot.getPaint());
                }
            }
        }
    }

    private synchronized void productSpots() {
        Random random = new Random();
        final int key = random.nextInt();
        final List<Spot> spotList = new ArrayList<>();
        Spot spotTmp;
        final int len = Math.max(random.nextInt(10), 5);
        for (int i = 0; i < len; i++) {
            int angle = random.nextInt(360);
            float sin = (float) Math.sin(Math.toRadians(angle));
            float cos = (float) Math.cos(Math.toRadians(angle));
            int startX = random.nextInt(mRadiusBound * 3);
            boolean flag = (random.nextInt(2) == 1);
            if (flag) {
                startX = -startX;
            }
            int startY = random.nextInt(mRadiusBound * 3);
            flag = (random.nextInt(2) == 1);
            if (flag) {
                startY = -startY;
            }
            spotTmp = new Spot(random.nextInt(mRadiusBound), startX, startY, mDistance * sin, -mDistance * cos);
            spotTmp.initPaint(mContext, mStyle);
            spotTmp.setAlpha(Math.max(random.nextInt(178), 84));
            spotList.add(spotTmp);
        }
        mMapSpotList.put(key, spotList);

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(mWorkAnimator.getDuration());
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            Object animatedValue = animation.getAnimatedValue();
            if (animatedValue instanceof Float) {
                float value = (float) animatedValue;
                //Log.d(TAG, "value update: " + value + ", spot number: " + spotList.size());
                for (Spot spot : spotList) {
                    spot.setStartX(spot.getStopX() * value);
                    spot.setStartY(spot.getStopY() * value);
                    spot.getPaint().setAlpha((int) (spot.getAlpha() * (1 - value)));
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMapValueAnimator.remove(key, valueAnimator);

                if (mMapSpotList.containsKey(key)) {
                    List<Spot> list = mMapSpotList.get(key);
                    if (list != null) {
                        list.clear();
                    }
                    mMapSpotList.remove(key, list);
                }
            }
        });

        valueAnimator.start();

        mMapValueAnimator.put(key, valueAnimator);
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    private static class Spot {
        private Paint mPaint;
        private final float mRadius;
        private float mStartX;
        private float mStartY;
        private final float mStopX;
        private final float mStopY;
        private int mAlpha;

        public Spot(float radius, float startX, float startY, float stopX, float stopY) {
            mRadius = radius;
            mStartX = startX;
            mStartY = startY;
            mStopX = stopX;
            mStopY = stopY;
        }

        public void initPaint(Context context, int style) {
            mPaint = new Paint();
            if (style == 1) {
                mPaint.setColor(ContextCompat.getColor(context, R.color.minute_1));
            } else if (style == 2) {
                mPaint.setColor(ContextCompat.getColor(context, R.color.minute_2));
            } else {
                mPaint.setColor(ContextCompat.getColor(context, R.color.minute));

            }
            mPaint.setMaskFilter(new BlurMaskFilter(DimenUtil.dip2px(context, 6), BlurMaskFilter.Blur.NORMAL));
        }

        public float getRadius() {
            return mRadius;
        }

        public float getStartX() {
            return mStartX;
        }

        public void setStartX(float startX) {
            mStartX = startX;
        }

        public float getStopY() {
            return mStopY;
        }

        public float getStartY() {
            return mStartY;
        }

        public void setStartY(float startY) {
            mStartY = startY;
        }

        public float getStopX() {
            return mStopX;
        }

        public Paint getPaint() {
            return mPaint;
        }

        public void setAlpha(int alpha) {
            this.mAlpha = alpha;
        }

        public int getAlpha() {
            return mAlpha;
        }
    }

}
