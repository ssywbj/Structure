/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wiz.watch.dreamservice.tmp;

import android.animation.Animator;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.wiz.watch.dreamservice.WatchFaceView;

public final class MoveScreensaverRunnable implements Runnable {

    private static final LogUtils.Logger LOGGER = new LogUtils.Logger("Screensaver");

    /**
     * The duration over which the fade in/out animations occur.
     */
    //private static final long FADE_TIME = 3000L;
    private static final long FADE_TIME = 0L;

    /**
     * Accelerate the hide animation.
     */
    private final Interpolator mAcceleration = new AccelerateInterpolator();

    /**
     * Decelerate the show animation.
     */
    private final Interpolator mDeceleration = new DecelerateInterpolator();

    /**
     * Tracks the currently executing animation if any; used to gracefully stop the animation.
     */
    private Animator mActiveAnimator;
    private WatchFaceView mWatchFaceView;

    public MoveScreensaverRunnable() {
    }

    public MoveScreensaverRunnable(WatchFaceView watchFaceView) {
        mWatchFaceView = watchFaceView;
    }

    /**
     * Start or restart the random movement of the saver view within the content view.
     */
    public void start() {
        // Stop any existing animations or callbacks.
        stop();

        // Reset the alpha to 0 so saver view will be randomly positioned within the new bounds.

        // Execute the position updater runnable to choose the first random position of saver view.
        run();

        // Schedule callbacks every minute to adjust the position of mSaverView.
        UiDataModel.getUiDataModel().addMinuteCallback(this, -FADE_TIME);
    }

    /**
     * Stop the random movement of the saver view within the content view.
     */
    public void stop() {
        UiDataModel.getUiDataModel().removePeriodicCallback(this);

        // End any animation currently running.
        if (mActiveAnimator != null) {
            mActiveAnimator.end();
            mActiveAnimator = null;
        }
    }

    @Override
    public void run() {
        Utils.enforceMainLooper();

        //final boolean selectInitialPosition = mSaverView.getAlpha() == 0f;
        final boolean selectInitialPosition = false;
        Log.v("ClassicPointerDream", "zenggz  MoveScreensaverRunnable run()  +++++ , selectInitialPosition = " + selectInitialPosition);

        if (mWatchFaceView != null) {
            mWatchFaceView.updateTime();
            mWatchFaceView.invalidate();
        }

        if (selectInitialPosition) {
            // When selecting an initial position for the saver view the width and height of
            // mContentView are untrustworthy if this was caused by a configuration change. To
            // combat this, we position the mSaverView randomly within the smallest box that is
            // guaranteed to work.
            /*final int smallestDim = Math.min(mContentView.getWidth(), mContentView.getHeight());
            final float newX = getRandomPoint(smallestDim - mSaverView.getWidth());
            final float newY = getRandomPoint(smallestDim - mSaverView.getHeight());

            mSaverView.setX(newX);
            mSaverView.setY(newY);
            mActiveAnimator = getAlphaAnimator(mSaverView, 0f, 1f);
            mActiveAnimator.setDuration(FADE_TIME);
            mActiveAnimator.setInterpolator(mDeceleration);
            mActiveAnimator.start();*/
        } else {
            // Select a new random position anywhere in mContentView that will fit mSaverView.
            /*final float newX = getRandomPoint(mContentView.getWidth() - mSaverView.getWidth());
            final float newY = getRandomPoint(mContentView.getHeight() - mSaverView.getHeight());

            // Fade out and shrink the saver view.
            final AnimatorSet hide = new AnimatorSet();
            hide.setDuration(FADE_TIME);
            hide.setInterpolator(mAcceleration);
            hide.play(getAlphaAnimator(mSaverView, 1f, 0f))
                    .with(getScaleAnimator(mSaverView, 1f, 0.85f));

            // Fade in and grow the saver view after altering its position.
            final AnimatorSet show = new AnimatorSet();
            show.setDuration(FADE_TIME);
            show.setInterpolator(mDeceleration);
            show.play(getAlphaAnimator(mSaverView, 0f, 1f))
                    .with(getScaleAnimator(mSaverView, 0.85f, 1f));
            show.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mSaverView.setX(newX);
                    mSaverView.setY(newY);
                }
            });

            // Execute hide followed by show.
            final AnimatorSet all = new AnimatorSet();
            all.play(show).after(hide);
            mActiveAnimator = all;
            mActiveAnimator.start();*/
        }
    }

    /**
     * @return a random integer between 0 and the {@code maximum} exclusive.
     */
    private static float getRandomPoint(float maximum) {
        return (int) (Math.random() * maximum);
    }
}
