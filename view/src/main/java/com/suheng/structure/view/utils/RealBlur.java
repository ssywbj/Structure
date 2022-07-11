package com.suheng.structure.view.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.google.android.renderscript.Toolkit;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RealBlur implements Runnable {

    private ExecutorService mThreadPool;
    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private View mViewBlurred;
    private View mViewBlur;
    private ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private ViewTreeObserver.OnDrawListener mDrawListener;

    private Canvas mViewBlurCanvas;
    private Bitmap mViewBlurBitmap;
    private BitmapDrawable mViewBlurBg;
    private final UIRunnable mUIRunnable = new UIRunnable(this);

    private int mRadius = 15;
    private int mScaleFactor = 6;
    private int mEraseColor = Color.WHITE;

    public void updateViewBlur(View viewBlur) {
        if (viewBlur == null) {
            return;
        }
        viewBlur.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mViewBlur.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int width = mViewBlur.getWidth();
                int height = mViewBlur.getHeight();
                Log.d("Wbj", "view blur, x: " + x + ", y:" + y + ", width: " + width + ", height: " + height);
                mRectBlur.left = x;
                mRectBlur.top = y;
                mRectBlur.right = mRectBlur.left + width;
                mRectBlur.bottom = mRectBlur.top + height;
            }
        });

        mViewBlur = viewBlur;
    }

    private boolean mIsScrollView;
    private Bitmap mScrollViewBitmap;

    public void updateViewBlurred(View viewBlurred) {
        if (viewBlurred == null) {
            this.stopBlurred();
            return;
        }

        viewBlurred.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mViewBlurred.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int width = mViewBlurred.getWidth();
                int height = mViewBlurred.getHeight();
                int paddingBottom = mViewBlurred.getPaddingBottom();
                Log.d("Wbj", "view blurred, x: " + x + ", y:" + y + ", width: " + width + ", height: " + height + ", paddingBottom: " + paddingBottom);
                mRectBlurred.left = x;
                mRectBlurred.top = y;
                mRectBlurred.right = mRectBlurred.left + width;
                mRectBlurred.bottom = mRectBlurred.top + height;
            }
        });

        this.stopBlurred();

        mViewBlurred = viewBlurred;
        mIsScrollView = ((mViewBlurred instanceof NestedScrollView) || (mViewBlurred instanceof ScrollView));

        ViewTreeObserver viewTreeObserver = mViewBlurred.getViewTreeObserver();
        if (mGlobalLayoutListener == null) {
            mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    boolean alive = viewTreeObserver.isAlive();
                    Log.i("Wbj", "viewTreeObserver, onGlobalLayout, alive: " + alive + ", getWidth: " + mViewBlurred.getWidth() + ", getHeight: " + mViewBlurred.getHeight());

                    updateBlurViewBackground();
                    /*if (alive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener);
                    }*/
                }
            };
            viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        if (mScrollChangedListener == null) {
            mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d("Wbj", "viewTreeObserver, onScrollChanged: " + mViewBlurred);
                    updateBlurViewBackground();
                }
            };
            viewTreeObserver.addOnScrollChangedListener(mScrollChangedListener);
        }

        /*if (mDrawListener == null) {
            mDrawListener = new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    Log.d("Wbj", "viewTreeObserver, onDraw: " + mViewBlurred);
                    updateBlurViewBackground();
                }
            };
            viewTreeObserver.addOnDrawListener(mDrawListener);
        }*/
    }

    public void stopBlurred() {
        if (mViewBlurred != null && mGlobalLayoutListener != null) {
            Log.v("Wbj", "stopBlurred, removeTreeObserver: " + mViewBlurred);
            mViewBlurred.getViewTreeObserver().removeOnScrollChangedListener(mScrollChangedListener);
            mViewBlurred.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
            mViewBlurred.getViewTreeObserver().removeOnDrawListener(mDrawListener);

            /*if (mThreadPool != null && !mThreadPool.isShutdown()) {
                mThreadPool.shutdownNow();
            }*/

            mViewBlurred = null;
            mGlobalLayoutListener = null;
            mScrollChangedListener = null;
        }

        if (mViewBlurBg != null) {
            Bitmap bitmap = mViewBlurBg.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            mViewBlurBg = null;
        }
        mRectBlurred.setEmpty();
        mRectBlur.setEmpty();
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    public void updateBlurViewBackground() {
        Bitmap viewBlurBitmap = this.loadViewBlurBitmap();
        if (viewBlurBitmap == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP));
            mUIRunnable.setBitmap(viewBlurBitmap);
            mUIRunnable.updateBlurViewBackground(mViewBlur, mViewBlurBg);
        } else {
            if (mThreadPool == null) {
                int processors = Runtime.getRuntime().availableProcessors();
                int executorThreads = (processors <= 3 ? 1 : processors / 2);
                mThreadPool = Executors.newFixedThreadPool(executorThreads);
            }

            mThreadPool.execute(this);
        }
    }

    @Override
    public void run() {
        if (mViewBlurBitmap == null) {
            return;
        }

        Bitmap blurredBitmap = Toolkit.INSTANCE.blur(mViewBlurBitmap, mRadius);
        if (mViewBlur != null && mViewBlur.getHandler() != null) {
            mUIRunnable.setBitmap(blurredBitmap);
            mViewBlur.getHandler().post(mUIRunnable);
        }
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    public Bitmap loadViewBlurBitmap() {
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

        int width = mIsScrollView ? mViewBlurred.getWidth() : mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        int bitmapWidth = (int) Math.ceil(1f * width / mScaleFactor);
        int bitmapHeight = (int) Math.ceil(1f * height / mScaleFactor);
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            return null;
        }
        Log.d("Wbj", "width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);

        int dx = x1 - x;
        int dy = y1 - y;
        float scale = 1f / mScaleFactor;
        Log.d("Wbj", "x: " + x + ", y: " + y + ", x1: " + x1 + ", y1: " + y1 + ", scale: " + scale + ", dx: " + dx + ", dy: " + dy);

        if (mIsScrollView) {
            if (mScrollViewBitmap == null) {
                int scrollRange = 0;
                if (mViewBlurred instanceof NestedScrollView) {
                    NestedScrollView scrollView = (NestedScrollView) mViewBlurred;
                    scrollRange = scrollView.computeVerticalScrollRange();
                }
                bitmapHeight = (int) Math.ceil(1f * scrollRange / mScaleFactor);
                if (bitmapHeight == 0) {
                    return null;
                }

                //mScrollViewBitmap = Bitmap.createBitmap(width, scrollRange, Bitmap.Config.ARGB_4444);
                mScrollViewBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(mScrollViewBitmap);
                canvas.scale(scale, scale);
                mViewBlurred.draw(canvas);
            }

            if (mScrollViewBitmap != null) {
                Bitmap bitmap = mViewBlurBitmap;
                int scrollY = mViewBlurred.getScrollY();
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                mViewBlurBitmap = Bitmap.createBitmap(mScrollViewBitmap, -dx / mScaleFactor, (scrollY - dy) / mScaleFactor
                        , width / mScaleFactor, height / mScaleFactor); //截取片断
                //mViewBlurBitmap = Bitmap.createBitmap(mScrollViewBitmap, -dx, scrollY - dy, width, height); //截取片断
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } else {
            if (mViewBlurBitmap == null) {
                mViewBlurBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                mViewBlurCanvas = new Canvas(mViewBlurBitmap);
                mViewBlurCanvas.scale(scale, scale);
                mViewBlurCanvas.translate(dx, dy);
            }

            mViewBlurBitmap.eraseColor(mEraseColor);
            mViewBlurred.draw(mViewBlurCanvas);
        }

        return mViewBlurBitmap;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public void setScaleFactor(int scaleFactor) {
        mScaleFactor = scaleFactor;
    }

    public void setEraseColor(int eraseColor) {
        mEraseColor = eraseColor;
    }

    private static final class UIRunnable implements Runnable {

        private final WeakReference<RealBlur> mWeakReference;
        private Bitmap mBitmap;

        public UIRunnable(RealBlur realBlur) {
            mWeakReference = new WeakReference<>(realBlur);
        }

        public void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        @Override
        public void run() {
            RealBlur realBlur = mWeakReference.get();
            if (realBlur == null || mBitmap == null) {
                return;
            }
            this.updateBlurViewBackground(realBlur.mViewBlur, realBlur.mViewBlurBg);
        }

        public void updateBlurViewBackground(View viewBlur, BitmapDrawable blurBg) {
            if (blurBg == null) {
                blurBg = new BitmapDrawable(viewBlur.getResources(), mBitmap);
                viewBlur.setBackground(blurBg);
            } else {
                Bitmap bgBitmap = blurBg.getBitmap();
                if (bgBitmap == null) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.d("Wbj", "run, 222222222: " + mBitmap + ", thread: " + Thread.currentThread().getName());
                    blurBg.setBitmap(mBitmap);
                    if (!bgBitmap.isRecycled()) {
                        Log.d("Wbj", "recycle, bgBitmap: " + bgBitmap);
                        bgBitmap.recycle();
                    }
                } else {
                    ByteBuffer byteBuffer = null;
                    try {
                        byteBuffer = ByteBuffer.allocate(mBitmap.getByteCount());
                        mBitmap.copyPixelsToBuffer(byteBuffer);
                        Log.d("Wbj", "run, 333333333: " + mBitmap + ", thread: " + Thread.currentThread().getName());
                        bgBitmap.eraseColor(Color.TRANSPARENT);
                        bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                    } catch (Exception e) {
                        Log.e("Wbj", "copy bitmap to buffer fail!", e);
                    } finally {
                        if (!mBitmap.isRecycled()) {
                            mBitmap.recycle();
                        }
                        if (byteBuffer != null) {
                            byteBuffer.clear();
                        }
                    }
                }
            }
        }
    }

}
