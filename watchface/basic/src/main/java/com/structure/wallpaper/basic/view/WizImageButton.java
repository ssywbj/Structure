package com.structure.wallpaper.basic.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageButton;

import com.structure.wallpaper.basic.R;

public class WizImageButton extends AppCompatImageButton {
    private PropertyValuesHolder mZoomInXAnimator, mZoomInYAnimator, mZoomOutXAnimator, mZoomOutYAnimator;

    private float mZoomScalePress = 0.95f;
    private int mZoomInDurationPress = 100;
    private int mZoomOutDurationPress = 35;

    public WizImageButton(Context context) {
        this(context, null);
    }

    public WizImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public WizImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setZoomScalePress(float zoomScalePress) {
        mZoomScalePress = zoomScalePress;
    }

    public void setZoomInDurationPress(int zoomInDurationPress) {
        mZoomInDurationPress = zoomInDurationPress;
    }

    public void setZoomOutDurationPress(int zoomOutDurationPress) {
        mZoomOutDurationPress = zoomOutDurationPress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEnabled()) {
                    startZoomOutAnimator();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isEnabled()) {
                    startZoomInAnimator();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startZoomInAnimator() {
        if (null == mZoomInXAnimator) {
            mZoomInXAnimator = PropertyValuesHolder.ofFloat("scaleX", getScaleX(), 1.0f);
        }
        if (null == mZoomInYAnimator) {
            mZoomInYAnimator = PropertyValuesHolder.ofFloat("scaleY", getScaleY(), 1.0f);
        }
        ObjectAnimator.ofPropertyValuesHolder(this, mZoomInXAnimator, mZoomInYAnimator).setDuration(mZoomInDurationPress).start();
    }

    private void startZoomOutAnimator() {
        if (null == mZoomOutXAnimator) {
            mZoomOutXAnimator = PropertyValuesHolder.ofFloat("scaleX", getScaleX(), mZoomScalePress);
        }
        if (null == mZoomOutYAnimator) {
            mZoomOutYAnimator = PropertyValuesHolder.ofFloat("scaleY", getScaleY(), mZoomScalePress);
        }
        ObjectAnimator.ofPropertyValuesHolder(this, mZoomOutXAnimator, mZoomOutYAnimator).setDuration(mZoomOutDurationPress).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseAnimator();
    }

    private void releaseAnimator() {
        mZoomInXAnimator = null;
        mZoomInYAnimator = null;
        mZoomOutXAnimator = null;
        mZoomOutYAnimator = null;
    }

}
