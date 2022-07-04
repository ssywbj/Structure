package com.suheng.structure.view.utils;

import android.app.Activity;
import android.content.Context;
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

public class RealBlur {

    private static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors() <= 3 ?
            1 : Runtime.getRuntime().availableProcessors() / 2;
    private static final ExecutorService ASYNC_BLUR_EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

    private final Context mContext;
    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private BitmapDrawable mBlurBg;
    private View mViewBlurred;
    private View mViewBlur;
    private Canvas mCanvas;
    private Bitmap mViewBlurredBitmap;
    private TaskRunnable mTaskRunnable;

    public RealBlur(Context context) {
        mContext = context;

        this.init();
    }

    private void init() {
        mCanvas = new Canvas();
        mTaskRunnable = new TaskRunnable(mContext);
    }

    public void updateViewBlur(View viewBlur) {
        mViewBlur = viewBlur;

        if (viewBlur == null) {
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

    private ViewTreeObserver.OnDrawListener mOnDrawListener;

    public void updateViewBlurred(View viewBlurred) {
        mViewBlurred = viewBlurred;

        if (viewBlurred == null) {
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

        /*if (mOnDrawListener == null) {
            mOnDrawListener = new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    updateBlurViewBackground();
                }
            };
        }
        mViewBlurred.getViewTreeObserver().addOnDrawListener(mOnDrawListener);*/
    }

    public void closeViewBlurred() {
        if (mViewBlurred != null && mOnDrawListener != null) {
            mViewBlurred.getViewTreeObserver().removeOnDrawListener(mOnDrawListener);
            mOnDrawListener = null;
            mViewBlurred = null;
        }
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    /*public void updateBlurViewBackground() {
        boolean intersects = Rect.intersects(mRectBlur, mRectBlurred);
        boolean contains = mRectBlurred.contains(mRectBlur);
        Log.d("Wbj", "rect, intersects: " + intersects + ", contains:" + contains);
        if (!(intersects || contains)) { //两个View没有相交部分
            Log.w("Wbj", "Hasn't intersect region between two views!");
            return;
        }

        mViewBlurredBitmap = this.loadViewBitmap(mViewBlurred);
        //ASYNC_BLUR_EXECUTOR.submit(mRunnable);

        int width = mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        if (mViewBlurredBitmap == null || width == 0 || height == 0) {
            return;
        }

        Bitmap bitmap = Bitmap.createBitmap(mViewBlurredBitmap, Math.abs(mRectBlur.left - mRectBlurred.left)
                , Math.abs(mRectBlur.top - mRectBlurred.top), width, height);
        Bitmap blurBitmap = Toolkit.INSTANCE.blur(bitmap, 10);
        //Bitmap blurBitmap = bitmap;
        if (mBlurBg == null) {
            mBlurBg = new BitmapDrawable(mContext.getResources(), blurBitmap);
            mViewBlur.setBackground(mBlurBg);
            Log.d("Wbj", "run, 1111111111: " + mBlurBg);
        } else {
            Log.d("Wbj", "run, blurBitmap: " + mBlurBg.getBitmap());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("Wbj", "run, 22222222: " + blurBitmap);
                mBlurBg.setBitmap(blurBitmap);
            } else {
                Bitmap bgBitmap = mBlurBg.getBitmap();
                if (bgBitmap != null) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(blurBitmap.getByteCount());
                    blurBitmap.copyPixelsToBuffer(byteBuffer);
                    Log.d("Wbj", "run, 333333333: " + bgBitmap);
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                }
            }
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }*/

    public void updateBlurViewBackground() {
        boolean intersects = Rect.intersects(mRectBlur, mRectBlurred);
        boolean contains = mRectBlurred.contains(mRectBlur);
        Log.d("Wbj", "rect, intersects: " + intersects + ", contains:" + contains);
        if (!(intersects || contains)) { //两个View没有相交部分
            Log.w("Wbj", "Hasn't intersect region between two views!");
            return;
        }

        //mViewBlurredBitmap = this.loadViewBitmap(mViewBlurred);
        mTaskRunnable.setViewBlurred(mViewBlurred);
        //mTaskRunnable.setBitmap(mViewBlurredBitmap);
        ASYNC_BLUR_EXECUTOR.submit(mTaskRunnable);

        ASYNC_BLUR_EXECUTOR.submit(mTaskRunnable);

        /*int width = mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        if (mViewBlurredBitmap == null || width == 0 || height == 0) {
            return;
        }

        Bitmap bitmap = Bitmap.createBitmap(mViewBlurredBitmap, Math.abs(mRectBlur.left - mRectBlurred.left)
                , Math.abs(mRectBlur.top - mRectBlurred.top), width, height);
        Bitmap blurBitmap = Toolkit.INSTANCE.blur(bitmap, 10);
        //Bitmap blurBitmap = bitmap;
        if (mBlurBg == null) {
            mBlurBg = new BitmapDrawable(mContext.getResources(), blurBitmap);
            mViewBlur.setBackground(mBlurBg);
            Log.d("Wbj", "run, 1111111111: " + mBlurBg);
        } else {
            Log.d("Wbj", "run, blurBitmap: " + mBlurBg.getBitmap());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d("Wbj", "run, 22222222: " + blurBitmap);
                mBlurBg.setBitmap(blurBitmap);
            } else {
                Bitmap bgBitmap = mBlurBg.getBitmap();
                if (bgBitmap != null) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(blurBitmap.getByteCount());
                    blurBitmap.copyPixelsToBuffer(byteBuffer);
                    Log.d("Wbj", "run, 333333333: " + bgBitmap);
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                }
            }
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }*/
    }

    @Nullable
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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(((Activity) mContext).getWindow(), mViewBlurredBitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                @Override
                public void onPixelCopyFinished(int copyResult) {
                    Log.d("Wbj", "onPixelCopyFinished, : " + copyResult + ", :" + Thread.currentThread().getName());
                }
            }, new Handler());
        }*/
        return mViewBlurredBitmap;
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
        //mViewBlurredBitmap.setWidth(view.getWidth());
        //mViewBlurredBitmap.setHeight(view.getHeight());

        view.draw(mCanvas);

        *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(getWindow(), viewBitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                @Override
                public void onPixelCopyFinished(int copyResult) {
                    Log.d("Wbj", "onPixelCopyFinished, : " + copyResult + ", :" + Thread.currentThread().getName());
                }
            }, new Handler());
        }*//*
        return mViewBlurredBitmap;
    }*/

    private class TaskRunnable implements Runnable {
        //private final Canvas mCanvas;
        //private Bitmap mBitmap;
        private View mViewBlurred;
        private final Activity mActivity;

        TaskRunnable(Context context) {
            mActivity = (Activity) context;
            mCanvas = new Canvas();
        }

        public void setViewBlurred(View viewBlurred) {
            mViewBlurred = viewBlurred;
        }

        public void setBitmap(Bitmap bitmap) {
            //mBitmap = bitmap;
        }

        /*@Nullable
        private Bitmap loadViewBitmap() {
            int width = mViewBlurred.getWidth();
            int height = mViewBlurred.getHeight();
            if (width == 0 || height == 0) {
                return null;
            }

            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                mCanvas.setBitmap(mBitmap);
            }
            Log.d("Wbj", "loadViewBitmap, mBitmap: " + mBitmap + ", : " + Thread.currentThread().getName());
            mBitmap.eraseColor(Color.TRANSPARENT);
            //mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mViewBlurred.draw(mCanvas);
            return mBitmap;
        }*/

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
            Bitmap viewBitmap = this.loadViewBitmap();
            int width = mViewBlur.getWidth();
            int height = mViewBlur.getHeight();
            if (viewBitmap == null || width == 0 || height == 0) {
                return;
            }

            Bitmap blurBitmap = Bitmap.createBitmap(viewBitmap, Math.abs(mRectBlur.left - mRectBlurred.left)
                    , Math.abs(mRectBlur.top - mRectBlurred.top), width, height);
            Bitmap blurredBitmap = Toolkit.INSTANCE.blur(blurBitmap, 10);
            if (mBlurBg == null) {
                mBlurBg = new BitmapDrawable(mContext.getResources(), blurredBitmap);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewBlur.setBackground(mBlurBg);
                        Log.d("Wbj", "run, 1111111111: " + mBlurBg);
                    }
                });
            } else {
                Log.d("Wbj", "run, blurredBitmap: " + mBlurBg.getBitmap());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Wbj", "run, 22222222: " + blurredBitmap);
                            mBlurBg.setBitmap(blurredBitmap);
                        }
                    });
                } else {
                    Bitmap bgBitmap = mBlurBg.getBitmap();
                    if (bgBitmap != null) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(blurredBitmap.getByteCount());
                        blurredBitmap.copyPixelsToBuffer(byteBuffer);
                        //Log.d("Wbj", "run, 333333333: " + bgBitmap);
                        //bgBitmap.eraseColor(Color.TRANSPARENT);
                        //bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Wbj", "run, 333333333: " + bgBitmap);
                                bgBitmap.eraseColor(Color.TRANSPARENT);
                                bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                                //mViewBlur.invalidateDrawable(mBlurBg);
                                //mViewBlur.invalidate();
                            }
                        });
                    }
                }
            }

            if (!blurBitmap.isRecycled()) {
                blurBitmap.recycle();
            }
            if (!viewBitmap.isRecycled()) {
                viewBitmap.recycle();
            }
        }
    }

}
