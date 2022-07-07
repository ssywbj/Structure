package com.suheng.structure.view.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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
    private ViewTreeObserver.OnDrawListener mOnDrawListener;

    private Canvas mViewBlurCanvas;
    private Bitmap mViewBlurBitmap;
    private BitmapDrawable mViewBlurBg;

    private int mRadius = 15;
    private int mScaleFactor = 6;

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

            /*if (mThreadPool != null && !mThreadPool.isShutdown()) {
                mThreadPool.shutdownNow();
            }*/

            mBlurBg = null;
            mViewBlurred = null;
            mOnDrawListener = null;
        }
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    public void updateBlurViewBackground() {
        if (mThreadPool == null) {
            int processors = Runtime.getRuntime().availableProcessors();
            int executorThreads = processors <= 3 ? 1 : processors / 2;
            Log.d("Wbj", "async, executorThreads: " + executorThreads);
            mThreadPool = Executors.newFixedThreadPool(executorThreads);
        }

        Bitmap viewBlurBitmap = this.loadViewBlurBitmap();
        if (viewBlurBitmap != null) {
            mThreadPool.execute(this);
        }
    }

    @Override
    public void run() {
        this.updateBlurViewBackground(mViewBlur);
    }

    private void updateBlurViewBackground(View viewBlur) {
        Bitmap blurredBitmap = Toolkit.INSTANCE.blur(mViewBlurBitmap, mRadius);

        if (mViewBlurBg == null) {
            mViewBlurBg = new BitmapDrawable(viewBlur.getResources(), blurredBitmap);
            if (viewBlur.getHandler() != null) {
                viewBlur.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        viewBlur.setBackground(mViewBlurBg);
                    }
                });
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("Wbj", "run, 222222222: " + blurredBitmap + ", thread: " + Thread.currentThread().getName());
                Bitmap bgBitmap = mViewBlurBg.getBitmap();
                mViewBlurBg.setBitmap(blurredBitmap);
                if (bgBitmap != null && !bgBitmap.isRecycled()) {
                    Log.d("Wbj", "recycle, bgBitmap: " + bgBitmap);
                    bgBitmap.recycle();
                }
                /*if (viewBlur.getHandler() != null) {
                    mUIRunnable.setBitmapDrawable(mViewBlurBg);
                    mUIRunnable.setBitmap(blurredBitmap);
                    viewBlur.getHandler().post(mUIRunnable);
                }*/
            } else {
                ByteBuffer byteBuffer = null;
                try {
                    byteBuffer = ByteBuffer.allocate(blurredBitmap.getByteCount());
                    blurredBitmap.copyPixelsToBuffer(byteBuffer);
                    if (viewBlur.getHandler() != null) {
                        mUIRunnable.setByteBuffer(byteBuffer);
                        mUIRunnable.setBitmap(mViewBlurBg.getBitmap());
                        viewBlur.getHandler().post(mUIRunnable);
                    }
                } catch (Exception e) {
                    Log.e("Wbj", "copy bitmap to buffer fail!", e);
                } finally {
                    if (!blurredBitmap.isRecycled()) {
                        blurredBitmap.recycle();
                    }
                    if (byteBuffer != null) {
                        byteBuffer.clear();
                    }
                }
            }
        }
    }

    private final UIRunnable mUIRunnable = new UIRunnable();

    private static final class UIRunnable implements Runnable {
        private BitmapDrawable mBitmapDrawable;

        private ByteBuffer mByteBuffer;
        private Bitmap mBitmap;

        public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
            if (mBitmapDrawable == bitmapDrawable) {
                return;
            }

            mBitmapDrawable = bitmapDrawable;
        }

        public void setByteBuffer(ByteBuffer byteBuffer) {
            mByteBuffer = byteBuffer;
        }

        public void setBitmap(Bitmap bitmap) {
            if (bitmap == null || mBitmap == bitmap) {
                return;
            }
            /*if (!mBitmap.isRecycled()) {
                mBitmap.recycle();
            }*/
            mBitmap = bitmap;
        }

        @Override
        public void run() {
            if (mBitmap == null || mBitmap.isRecycled()) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("Wbj", "run, 222222222: " + mBitmap + ", thread: " + Thread.currentThread().getName());
                if (mBitmapDrawable != null) {
                    Bitmap bgBitmap = mBitmapDrawable.getBitmap();
                    if (bgBitmap != null && !bgBitmap.isRecycled()) {
                        Log.d("Wbj", "recycle, bgBitmap: " + bgBitmap);
                        bgBitmap.recycle();
                    }
                    mBitmapDrawable.setBitmap(mBitmap);
                }
            } else {
                if (mByteBuffer == null) {
                    return;
                }
                try {
                    Log.d("Wbj", "run, 333333333: " + mBitmap + ", thread: " + Thread.currentThread().getName());
                    mBitmap.eraseColor(Color.TRANSPARENT);
                    mBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(mByteBuffer.array()));
                } catch (Exception e) {
                    Log.e("Wbj", "copy bitmap from buffer  fail!", e);
                } finally {
                    if (mByteBuffer != null) {
                        mByteBuffer.clear();
                    }
                }
            }
        }
    }

    @Nullable
    private Bitmap loadViewBlurBitmap() {
        boolean intersects = Rect.intersects(mRectBlur, mRectBlurred);
        boolean contains = mRectBlurred.contains(mRectBlur);
        Log.d("Wbj", "rect, intersects: " + intersects + ", contains:" + contains);
        if (!(intersects || contains)) { //两个View没有相交部分
            Log.w("Wbj", "Hasn't intersect region between two views!");
            return null;
        }

        int[] location = new int[2];
        mViewBlur.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        mViewBlurred.getLocationOnScreen(location);
        int x1 = location[0];
        int y1 = location[1];

        int width = mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        int bitmapWidth = (int) Math.ceil(1f * width / mScaleFactor);
        int bitmapHeight = (int) Math.ceil(1f * height / mScaleFactor);
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            return null;
        }
        Log.d("Wbj", "width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);
        if (mViewBlurBitmap == null) {
            mViewBlurBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
            mViewBlurCanvas = new Canvas(mViewBlurBitmap);
            float scale = 1f / mScaleFactor;
            mViewBlurCanvas.scale(scale, scale);
            float dx = x1 - x;
            float dy = y1 - y;
            mViewBlurCanvas.translate(dx, dy);
            Log.d("Wbj", "x: " + x + ", y: " + y + ", x1: " + x1 + ", y1: " + y1 + ", scale: " + scale + ", dx: " + dx + ", dy: " + dy);
        }
        mViewBlurred.draw(mViewBlurCanvas);

        return mViewBlurBitmap;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public void setScaleFactor(int scaleFactor) {
        mScaleFactor = scaleFactor;
    }

}
