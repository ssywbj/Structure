package com.suheng.structure.view.utils;

import android.annotation.SuppressLint;
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
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.google.android.renderscript.Toolkit;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RealBlur2 {
    private static final String TAG = RealBlur2.class.getSimpleName();
    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private View mViewBlurred;
    private View mViewBlur;
    private ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    private Canvas mViewBlurCanvas;
    private Bitmap mViewBlurBitmap;
    private BitmapDrawable mViewBlurBg;

    private int mRadius = 15;
    private int mScaleFactor = 6;
    private int mEraseColor = Color.WHITE;

    private boolean mIsScrollView;
    private Bitmap mScrollViewBitmap;

    public void updateViewBlur(View viewBlur) {
        if (viewBlur == null) {
            return;
        }

        mViewBlur = viewBlur;
    }

    public void updateViewBlurred(View viewBlurred) {
        this.stopBlurred();
        if (viewBlurred == null) {
            Log.w(TAG, "view blurred is null, return!");
            return;
        }

        mViewBlurred = viewBlurred;
        mIsScrollView = ((mViewBlurred instanceof NestedScrollView) || (mViewBlurred instanceof ScrollView));

        ViewTreeObserver viewTreeObserver = mViewBlurred.getViewTreeObserver();
        if (mGlobalLayoutListener == null) {
            mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    boolean alive = viewTreeObserver.isAlive();
                    Log.i(TAG, "viewTreeObserver, onGlobalLayout, alive: " + alive);

                    updateBlurViewBackground();
                }
            };
            viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        if (mScrollChangedListener == null) {
            mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d(TAG, "viewTreeObserver, onScrollChanged: " + mViewBlurred);
                    updateBlurViewBackground();
                }
            };
            viewTreeObserver.addOnScrollChangedListener(mScrollChangedListener);
        }
    }

    public void stopBlurred() {
        if (mViewBlurred != null && mGlobalLayoutListener != null) {
            mViewBlurred.getViewTreeObserver().removeOnScrollChangedListener(mScrollChangedListener);
            mViewBlurred.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
            mViewBlurred = null;
            mGlobalLayoutListener = null;
            mScrollChangedListener = null;
        }

        mRectBlurred.setEmpty();
        mRectBlur.setEmpty();

        this.recycleViewBlurBg();
    }

    private void recycleViewBlurBg() {
        if (mViewBlurBg != null) {
            Bitmap bitmap = mViewBlurBg.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            mViewBlurBg = null;
        }

        if (mViewBlurBitmap != null) {
            if (!mViewBlurBitmap.isRecycled()) {
                mViewBlurBitmap.recycle();
            }
            mViewBlurBitmap = null;
        }

        if (mScrollViewBitmap != null) {
            if (!mScrollViewBitmap.isRecycled()) {
                mScrollViewBitmap.recycle();
            }
            mScrollViewBitmap = null;
        }
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    public void updateBlurViewBackground() {
        if (mViewBlur == null || mViewBlurred == null) {
            return;
        }

        Bitmap viewBlurBitmap = this.loadViewBlurBitmap();
        if (viewBlurBitmap == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.CLAMP));
            updateBlurViewBackground(viewBlurBitmap, true);
        } else {
            updateBlurViewBackground(Toolkit.INSTANCE.blur(viewBlurBitmap, mRadius));
        }

        //updateBlurViewBackground(Toolkit.INSTANCE.blur(viewBlurBitmap, mRadius));
    }

    private void updateBlurViewBackground(Bitmap bitmap, boolean isRenderEffect) {
        if (mViewBlurBg == null) {
            mViewBlurBg = new BitmapDrawable(mViewBlur.getResources(), bitmap);
            mViewBlur.setBackground(mViewBlurBg);
            Log.d(TAG, "updateBlurViewBackground, 11111: " + mViewBlurBg);
        } else {
            Bitmap bgBitmap = mViewBlurBg.getBitmap();
            if (bgBitmap == null || bitmap == null) {
                Log.d(TAG, "updateBlurViewBackground, 22222, bgBitmap or bitmap is null");
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG, "updateBlurViewBackground, 22222");
                mViewBlurBg.setBitmap(bitmap);
                mViewBlur.invalidateDrawable(mViewBlurBg);
                if (!isRenderEffect && !bgBitmap.isRecycled()) {
                    Log.d(TAG, "updateBlurViewBackground, 22222 recycle bgBitmap");
                    bgBitmap.recycle();
                }
            } else {
                ByteBuffer byteBuffer = null;
                try {
                    Log.d(TAG, "updateBlurViewBackground, 33333");
                    byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
                    bitmap.copyPixelsToBuffer(byteBuffer);
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                } catch (Exception e) {
                    Log.e(TAG, "copy bitmap to buffer fail!", e);
                } finally {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    if (byteBuffer != null) {
                        byteBuffer.clear();
                    }
                }
            }
        }
    }

    private void updateBlurViewBackground(Bitmap bitmap) {
        this.updateBlurViewBackground(bitmap, false);
    }

    private boolean getViewLocation(View view, Rect rect) {
        int left = rect.left;
        int top = rect.top;
        int right = rect.right;
        int bottom = rect.bottom;

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view.getWidth();
        int height = view.getHeight();
        //Log.d(TAG, "view location, x: " + x + ", y:" + y + ", width: " + width + ", height: " + height + ", view: " + view);
        rect.left = x;
        rect.top = y;
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;

        return !((left == rect.left) && (top == rect.top) && (right == rect.right) && (bottom == rect.bottom));
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    private Bitmap loadViewBlurBitmap() {
        boolean isViewBlurLocChange = this.getViewLocation(mViewBlur, mRectBlur);
        boolean isViewBlurredLocChange = this.getViewLocation(mViewBlurred, mRectBlurred);
        boolean intersect = mRectBlur.intersect(mRectBlurred);
        if (!intersect) { //两个View没有相交部分
            Log.w(TAG, "Hasn't intersect region between two views!");
            return null;
        }

        int width = mIsScrollView ? mViewBlurred.getWidth() : mViewBlur.getWidth();
        int height = mViewBlur.getHeight();
        int bitmapWidth = (int) Math.ceil(1f * width / mScaleFactor);
        int bitmapHeight = (int) Math.ceil(1f * height / mScaleFactor);
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            return null;
        }
        //Log.d("Wbj", "width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);

        int dx = mRectBlurred.left - mRectBlur.left;
        int dy = mRectBlurred.top - mRectBlur.top;
        float scale = 1f / mScaleFactor;
        Log.d(TAG, "scale: " + scale + ", dx: " + dx + ", dy: " + dy + ", isViewBlurredLocChange: " + isViewBlurredLocChange
                + ", isViewBlurLocChange: " + isViewBlurLocChange);

        if ((isViewBlurLocChange || isViewBlurredLocChange)) {
            this.recycleViewBlurBg();
        }

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

                int[] colors = new int[bitmapWidth * bitmapHeight];
                Arrays.fill(colors, mEraseColor);
                Bitmap bitmap = Bitmap.createBitmap(colors, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                mScrollViewBitmap = bitmap.copy(bitmap.getConfig(), true);
                //mScrollViewBitmap = Bitmap.createBitmap(width, scrollRange, Bitmap.Config.ARGB_4444);
                //mScrollViewBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(mScrollViewBitmap);
                canvas.scale(scale, scale);
                mViewBlurred.draw(canvas);

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

            Bitmap bitmap = mViewBlurBitmap;
            int scrollY = mViewBlurred.getScrollY();
            mViewBlurBitmap = Bitmap.createBitmap(mScrollViewBitmap, -dx / mScaleFactor, (scrollY - dy) / mScaleFactor
                    , width / mScaleFactor, height / mScaleFactor); //截取片断
            //mViewBlurBitmap = Bitmap.createBitmap(mScrollViewBitmap, -dx, scrollY - dy, width, height); //截取片断
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
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
}
