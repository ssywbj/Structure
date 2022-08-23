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
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RealBlur {
    private static final String TAG = RealBlur.class.getSimpleName();
    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private View mViewBlurred;
    private View mViewBlur;
    private ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    private Canvas mViewBlurCanvas;
    private Bitmap mViewBlurBitmap;
    private BitmapDrawable mViewBlurBg;

    private int mRadius = 13;
    private int mScaleFactor = 6;
    private int mEraseColor = Color.WHITE;

    private boolean mIsScrollView;
    private Bitmap mScrollViewBitmap;

    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mScriptIntrinsicBlur;
    private Allocation mInput, mOutput;
    private Bitmap mOutBitmap;

    public RealBlur() {
    }

    public RealBlur(Context context) {
        mRenderScript = RenderScript.create(context);
        mScriptIntrinsicBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    private void initAllocation(Bitmap bitmap) {
        this.recycleAllocation();
        mInput = Allocation.createFromBitmap(mRenderScript, bitmap);
        mOutput = Allocation.createTyped(mRenderScript, mInput.getType());
        mOutBitmap = bitmap.copy(bitmap.getConfig(), true);
    }

    public void updateViewBlur(View viewBlur) {
        if (viewBlur == null) {
            return;
        }

        mViewBlur = viewBlur;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mRadius = 18;
            mScaleFactor = 2;
        } else {
            mRadius = 13;
            mScaleFactor = 6;
        }
    }

    public void updateViewBlurred(View viewBlurred) {
        Log.v(TAG, "setBlurredView, blurredView: " + viewBlurred);
        if (mViewBlurred == viewBlurred) {
            return;
        }
        this.stopBlurred();
        mViewBlurred = viewBlurred;
        if (mViewBlurred == null) {
            return;
        }

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
            mGlobalLayoutListener = null;
            mScrollChangedListener = null;
        }

        mRectBlurred.setEmpty();
        mRectBlur.setEmpty();

        this.recycleViewBlurBg();
    }

    private void recycleViewBlurBg() {
        mViewBlurBg = null;

        if (mViewBlurBitmap != null) {
            if (!mViewBlurBitmap.isRecycled()) {
                mViewBlurBitmap.recycle();
                Log.d(TAG, "recycleViewBlurBg, ViewBlurBitmap");
            }
            mViewBlurBitmap = null;
        }

        if (mScrollViewBitmap != null) {
            if (!mScrollViewBitmap.isRecycled()) {
                mScrollViewBitmap.recycle();
                Log.d(TAG, "recycleViewBlurBg, ScrollViewBitmap");
            }
            mScrollViewBitmap = null;
        }

        this.recycleAllocation();
    }

    public void setViewBlurredAndBlur(View viewBlurred, View viewBlur) {
        this.updateViewBlur(viewBlur);
        this.updateViewBlurred(viewBlurred);
    }

    public void updateBlurViewBackground() {
        if (mViewBlur == null || mViewBlurred == null) {
            return;
        }

        Bitmap viewBlurBitmap;
        try {
            viewBlurBitmap = this.loadViewBlurBitmap();
        } catch (Exception e) {
            viewBlurBitmap = null;
            Log.e(TAG, "load ViewBlur Bitmap fail!", e);
        }
        if (viewBlurBitmap == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //12
            //RenderEffect bitmapEffect = RenderEffect.createBitmapEffect(viewBlurBitmap);
            //mViewBlur.setRenderEffect(bitmapEffect);

            //mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, Shader.TileMode.REPEAT));

            RenderEffect bitmapEffect = RenderEffect.createBitmapEffect(viewBlurBitmap, new Rect(0, 0, viewBlurBitmap.getWidth(), viewBlurBitmap.getHeight())
                    , new Rect(0, 0, mRectBlur.width(), mRectBlur.height()));
            mViewBlur.setRenderEffect(RenderEffect.createBlurEffect(mRadius, mRadius, bitmapEffect, Shader.TileMode.REPEAT));
            if (mViewBlur instanceof ImageView) {
                ((ImageView) mViewBlur).setImageBitmap(viewBlurBitmap);
                //updateBlurViewBackground(viewBlurBitmap, true);
            } else {
                updateBlurViewBackground(viewBlurBitmap, true);
            }
        } else {
            //this.updateBlurViewBackground(Toolkit.INSTANCE.blur(viewBlurBitmap, mRadius));
            this.blur();
            this.updateBlurViewBackground(mOutBitmap);
        }
    }

    private void blur() { //生成模糊的bitmap
        mScriptIntrinsicBlur.setRadius(mRadius);
        mScriptIntrinsicBlur.setInput(mInput);
        mScriptIntrinsicBlur.forEach(mOutput);
        mOutput.copyTo(mOutBitmap);
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
                //mViewBlur.invalidateDrawable(mViewBlurBg);
                if (!isRenderEffect && !bgBitmap.isRecycled()) {
                    Log.d(TAG, "updateBlurViewBackground, 22222 recycle bgBitmap");
                    bgBitmap.recycle();
                }
            } else {
                Log.d(TAG, "updateBlurViewBackground, 33333");
                ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
                try {
                    bitmap.copyPixelsToBuffer(byteBuffer);
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                } catch (Exception e) {
                    Log.e(TAG, "copy bitmap to buffer fail!", e);
                }
                byteBuffer.clear();
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
        Log.d("Wbj", "width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);

        int dx = mRectBlurred.left - mRectBlur.left;
        int dy = mRectBlurred.top - mRectBlur.top;
        float scale = 1f / mScaleFactor;
        Log.d(TAG, "scale: " + scale + ", dx: " + dx + ", dy: " + dy + ", isViewBlurredLocChange: " + isViewBlurredLocChange
                + ", isViewBlurLocChange: " + isViewBlurLocChange);

        if ((isViewBlurLocChange || isViewBlurredLocChange)) {
            this.recycleViewBlurBg();
        }

        if (mIsScrollView) {
            boolean initAllocation = false;
            if (mScrollViewBitmap == null) {
                Rect hitRect = new Rect();
                mViewBlurred.getHitRect(hitRect);
                Rect visRect = new Rect();
                mViewBlurred.getGlobalVisibleRect(visRect);
                Log.d(TAG, "hitRect: " + hitRect + ", visRect: " + visRect);

                int scrollRange = 0;
                try {
                    Method method = View.class.getDeclaredMethod("computeVerticalScrollRange");
                    method.setAccessible(true);
                    Object invoke = method.invoke(mViewBlurred);
                    if (invoke instanceof Integer) {
                        scrollRange = (int) invoke;
                        Log.i(TAG, "reflect invoke scrollRange: " + scrollRange);
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    Log.e(TAG, "reflect invoke computeVerticalScrollRange() fail!", e);
                }

                bitmapHeight = (int) Math.ceil(1f * scrollRange / mScaleFactor);
                Log.d(TAG, "scrollRange: " + scrollRange + ", getTop: " + mViewBlurred.getTop() + ", getBottom: " + mViewBlurred.getBottom());
                if (bitmapHeight == 0) {
                    return null;
                }

                int[] colors = new int[bitmapWidth * bitmapHeight];
                Arrays.fill(colors, mEraseColor);
                Bitmap bitmap = Bitmap.createBitmap(colors, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                mScrollViewBitmap = bitmap.copy(bitmap.getConfig(), true);
                Canvas canvas = new Canvas(mScrollViewBitmap);
                canvas.scale(scale, scale);
                mViewBlurred.draw(canvas);

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }

                initAllocation = true;
            }

            int scrollY = mViewBlurred.getScrollY();

            int x = -dx / mScaleFactor;
            int y = (scrollY - dy) / mScaleFactor;
            int dstWidth = width / mScaleFactor;
            int dstHeight = height / mScaleFactor;
            if (x < 0 || y < 0 || dstWidth <= 0 || dstHeight <= 0) {
                Log.w(TAG, "pivot out source bitmap");
                return null;
            }
            if (x + dstWidth > mScrollViewBitmap.getWidth() || y + dstHeight > mScrollViewBitmap.getHeight()) {
                Log.w(TAG, "need dst bitmap dimen over source bitmap");
                return null;
            }
            Bitmap bitmap = mViewBlurBitmap;
            mViewBlurBitmap = Bitmap.createBitmap(mScrollViewBitmap, x, y, dstWidth, dstHeight); //截取片断
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (initAllocation) {
                this.initAllocation(mViewBlurBitmap);
            } /*else {
                mInput.copyTo(mViewBlurBitmap);
            }*/
        } else {
            if (mViewBlurBitmap == null) {
                mViewBlurBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                mViewBlurCanvas = new Canvas(mViewBlurBitmap);
                mViewBlurCanvas.scale(scale, scale);
                mViewBlurCanvas.translate(dx, dy);

                this.initAllocation(mViewBlurBitmap);
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

    private void recycleAllocation() {
        if (mInput != null) {
            mInput.destroy();
            mInput = null;
        }

        if (mOutput != null) {
            mOutput.destroy();
            mOutput = null;
        }

        if (mOutBitmap != null) {
            if (!mOutBitmap.isRecycled()) {
                mOutBitmap.recycle();
            }
            mOutBitmap = null;
        }
    }
}
