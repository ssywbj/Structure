package com.suheng.structure.view.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.google.android.renderscript.Toolkit;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RealBlur implements Runnable {

    private ExecutorService mThreadPool;
    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private BitmapDrawable mBlurBg;
    private View mViewBlurred;
    private View mViewBlur;
    private int mRadius = 20;
    private ViewTreeObserver.OnDrawListener mOnDrawListener;

    public RealBlur(Context context) {
        this.init();
    }

    private void init() {
    }

    public void updateViewBlur(View viewBlur) {
        mViewBlur = viewBlur;

        if (viewBlur == null) {
            mRectBlur.setEmpty();
            return;
        }

        mViewBlur.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mViewBlur.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int width = mViewBlur.getWidth();
                int height = mViewBlur.getHeight();
                Log.d("Wbj", "setViewBlur, x: " + x + ", y:" + y + ", width: " + width + ", height: " + height);
                mRectBlur.left = x;
                mRectBlur.top = y;
                mRectBlur.right = mRectBlur.left + width;
                mRectBlur.bottom = mRectBlur.top + height;
            }
        });
    }

    public void updateViewBlurred(View viewBlurred) {
        mViewBlurred = viewBlurred;

        if (viewBlurred == null) {
            mRectBlurred.setEmpty();
            return;
        }

        mViewBlurred.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mViewBlurred.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int width = mViewBlurred.getWidth();
                int height = mViewBlurred.getHeight();
                int paddingBottom = mViewBlurred.getPaddingBottom();
                Log.d("Wbj", "setViewBlur, x: " + x + ", y:" + y + ", width: " + width
                        + ", height: " + height + ", paddingBottom: " + paddingBottom);
                mRectBlurred.left = x;
                mRectBlurred.top = y;
                mRectBlurred.right = mRectBlurred.left + width;
                mRectBlurred.bottom = mRectBlurred.top + height;
            }
        });

        if (mOnDrawListener == null) {
            mOnDrawListener = new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    //updateBlurViewBackground();
                }
            };
        }
        mViewBlurred.getViewTreeObserver().addOnDrawListener(mOnDrawListener);
    }

    public void stopBlurred() {
        if (mViewBlurred != null && mOnDrawListener != null) {
            mViewBlurred.getViewTreeObserver().removeOnDrawListener(mOnDrawListener);
            if (mBlurBg != null) {
                Bitmap bitmap = mBlurBg.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

            if (mThreadPool != null && !mThreadPool.isShutdown()) {
                mThreadPool.shutdownNow();
            }

            mBlurBg = null;
            mViewBlurred = null;
            mOnDrawListener = null;
        }
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    public void updateBlurViewBackground(boolean isAsync) {
        if (isAsync) {
            if (mThreadPool == null) {
                int processors = Runtime.getRuntime().availableProcessors();
                int executorThreads = processors <= 3 ? 1 : processors / 2;
                Log.d("Wbj", "async, executorThreads: " + executorThreads);
                mThreadPool = Executors.newFixedThreadPool(executorThreads);
            }

            mThreadPool.submit(this);
        } else {
            this.run();
        }
    }

    @Nullable
    private Bitmap loadViewBitmap() {
        int width = mViewBlurred.getWidth();
        int height = mViewBlurred.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Log.d("Wbj", "loadViewBitmap, bitmap: " + bitmap + ", : " + Thread.currentThread().getName());
        mViewBlurred.draw(canvas);
        return bitmap;
    }

    @Override
    public void run() {
        boolean intersects = Rect.intersects(mRectBlur, mRectBlurred);
        boolean contains = mRectBlurred.contains(mRectBlur);
        Log.d("Wbj", "rect, intersects: " + intersects + ", contains:" + contains);
        if (!(intersects || contains)) { //两个View没有相交部分
            Log.w("Wbj", "Hasn't intersect region between two views!");
            return;
        }

        Bitmap viewBitmap = this.loadViewBitmap();
        int width = mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        if (viewBitmap == null || width == 0 || height == 0) {
            return;
        }

        Bitmap blurBitmap = Bitmap.createBitmap(viewBitmap, Math.abs(mRectBlur.left - mRectBlurred.left)
                , Math.abs(mRectBlur.top - mRectBlurred.top), width, height);
        if (!viewBitmap.isRecycled()) {
            viewBitmap.recycle();
        }

        if (mBlurBg == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP));
                mBlurBg = new BitmapDrawable(mViewBlur.getResources(), blurBitmap);
            } else {
                Bitmap blurredBitmap = Toolkit.INSTANCE.blur(blurBitmap, mRadius);
                if (!blurBitmap.isRecycled()) {
                    blurBitmap.recycle();
                }
                mBlurBg = new BitmapDrawable(mViewBlur.getResources(), blurredBitmap);
            }

            mViewBlur.setBackground(mBlurBg);
            Log.d("Wbj", "run, 1111111111: " + mBlurBg + ", thread: " + Thread.currentThread().getName());
            /*mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewBlur.setBackground(mBlurBg);
                    Log.d("Wbj", "run, 1111111111: " + mBlurBg+ ", thread: " + Thread.currentThread().getName());
                }
            });*/
        } else {
            Bitmap bgBitmap = mBlurBg.getBitmap();
            if (bgBitmap == null || bgBitmap.isRecycled()) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("Wbj", "run, 22222222: " + blurBitmap + ", thread: " + Thread.currentThread().getName());
                mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP));
                mBlurBg.setBitmap(blurBitmap);
                if (!bgBitmap.isRecycled()) {
                    bgBitmap.recycle();
                }

                /*mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Wbj", "run, 22222222: " + blurBitmap + ", thread: " + Thread.currentThread().getName());
                        mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP));
                        //mViewBlur.setRenderEffect(blurEffect);
                        mBlurBg.setBitmap(blurBitmap);
                        //RenderEffect blurEffect = RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP);
                        //mViewBlur.setBackground(new BitmapDrawable(mViewBlur.getResources(), blurBitmap));
                        if (!bgBitmap.isRecycled()) {
                            bgBitmap.recycle();
                        }
                    }
                });*/
            } else {
                Bitmap blurredBitmap = Toolkit.INSTANCE.blur(blurBitmap, mRadius);
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(blurredBitmap.getByteCount());
                    blurredBitmap.copyPixelsToBuffer(byteBuffer);
                    Log.d("Wbj", "run, 333333333: " + bgBitmap + ", thread: " + Thread.currentThread().getName());
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                } catch (Exception e) {
                    Log.e("Wbj", "copy blur bitmap fail!", e);
                } finally {
                    if (!blurBitmap.isRecycled()) {
                        blurBitmap.recycle();
                    }
                    if (!blurredBitmap.isRecycled()) {
                        blurredBitmap.recycle();
                    }
                }

                /*mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Wbj", "run, 333333333: " + bgBitmap + ", thread: " + Thread.currentThread().getName());
                        bgBitmap.eraseColor(Color.TRANSPARENT);
                        bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));

                        if (!blurBitmap.isRecycled()) {
                            blurBitmap.recycle();
                        }
                    }
                });*/
            }

        }

    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    /*@Nullable
    private Bitmap loadViewBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }

        if (mViewBlurredBitmap == null) {
            mViewBlurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas.setBitmap(mViewBlurredBitmap);
        }
        Log.d("Wbj", "loadViewBitmap, : " + mViewBlurredBitmap);
        mViewBlurredBitmap.eraseColor(Color.TRANSPARENT);
        //mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //mCanvas.clipRect(mRectBlur);
        view.draw(mCanvas);
        return mViewBlurredBitmap;
    }*/

}
