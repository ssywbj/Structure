package com.suheng.structure.wallpaper.boneblack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suheng.structure.wallpaper.basic.DimenUtil;
import com.suheng.structure.wallpaper.basic.utils.BitmapUtil;

public class BoneBlackWallpaperService extends WallpaperService {
    public static final String TAG = BoneBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final float POINT_RADIUS = 5.0f;//圆点半径
        private static final int SCALES = 12;//12个刻度
        private final double RADIANS = Math.toRadians(1.0f * 360 / SCALES);//弧度值，Math.toRadians：度换算成弧度

        private boolean mVisible = false;

        private float mCenterX;//圆心X坐标
        private float mCenterY;//圆心Y坐标
        private float mCanvasWidth, mCanvasHeight;//屏幕宽高
        private float mMaxRadius;
        private Rect mRect = new Rect();

        private boolean mAmbientMode;
        private Paint mPaintText, mPaintPoint;
        private Context mContext;
        private Paint mPaintScale;
        private Bitmap mBitmapScale12, mBitmapScale3, mBitmapScale6, mBitmapScale9, mBitmapScalePaperclip;
        private int mMargin;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = BoneBlackWallpaperService.this;

            mPaintText = new Paint();
            mPaintText.setColor(Color.WHITE);
            mPaintText.setAntiAlias(true);
            mPaintText.setTextSize(50f);
            mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

            mPaintPoint = new Paint();
            mPaintPoint.setColor(Color.WHITE);
            mPaintPoint.setAntiAlias(true);

            mMargin = DimenUtil.dip2px(mContext, 6);

            mPaintScale = new Paint();
            mBitmapScale12 = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_sacle_number_12);
            mBitmapScale3 = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_battary);
            mBitmapScale6 = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_number_6);
            mBitmapScale9 = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_weather_day_duoyun);
            mBitmapScalePaperclip = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_paperclip);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mCanvasWidth = width;
            mCanvasHeight = height;
            mCenterX = mCanvasWidth / 2;//圆心X坐标
            mCenterY = mCanvasWidth / 2;//圆心Y坐标
            mMaxRadius = Math.min(mCenterX, mCenterY);

            this.invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.i(TAG, "onVisibilityChanged, visible = " + visible);
            mVisible = visible;
            Intent intent = new Intent("com.google.android.wearable.watchfaces.action.REQUEST_STATE");
            sendBroadcast(intent);
            if (visible) {
                this.invalidate();
            } else {
                mHandler.removeMessages(111);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.i(TAG, "onSurfaceDestroyed");
            mVisible = false;
            mHandler.removeMessages(111);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            mVisible = false;
            mHandler.removeMessages(111);
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            super.onCommand(action, x, y, z, extras, resultRequested);
            if (action.matches("com.google.android.wearable.action.BACKGROUND_ACTION")) {
                mAmbientMode = extras.getBoolean("ambient_mode", false);
                if (mAmbientMode) {
                    this.invalidate();
                } else if (mVisible) {//Redraw digital clock in green during non-ambient mode
                    this.invalidate();
                }
            } else if (action.matches("com.google.android.wearable.action.AMBIENT_UPDATE")) {
                this.invalidate();
            }
            Log.i(TAG, "onCommand, action = " + action + ", x = " + x + ", y = " + y + ", ambient_mode = " + mAmbientMode);
            return extras;
        }

        private void onDraw(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            //Log.d(TAG, "onDraw, draw watch face, width = " + width + ", height = " + height);

            canvas.save();

            canvas.drawColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));//画面背景
            canvas.drawBitmap(mBitmapScalePaperclip, 1.0f * (width - mBitmapScalePaperclip.getWidth()) / 2,
                    1.0f * (height - mBitmapScalePaperclip.getHeight()) / 2, mPaintScale);

            this.paintScale(canvas);

            canvas.restore();
        }

        private void paintScale(Canvas canvas) {
            mPaintText.setTextSize(26f);
            mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            String digit = "0";
            mPaintText.getTextBounds(digit, 0, digit.length(), mRect);

            mPaintText.setStyle(Paint.Style.STROKE);
            float radius = mMaxRadius - mMargin;
            float radiusText = radius - mRect.height() - 4;

            mPaintText.setStyle(Paint.Style.FILL);
            float cxPoint, cyPoint, cxText, cyText;
            double sinValue, cosValue;
            for (int index = 0; index < SCALES; index++) {
                sinValue = Math.sin(RADIANS * index);
                cosValue = Math.cos(RADIANS * index);
                cxPoint = (float) (mCenterX + radius * sinValue);
                cyPoint = (float) (mCenterY - radius * cosValue);
                cxText = (float) (mCenterX - radiusText * sinValue);
                cyText = (float) (mCenterY - radiusText * cosValue);

                if (index == 0) {
                    canvas.drawBitmap(mBitmapScale12, (mCanvasWidth - mBitmapScale12.getWidth()) / 2
                            , mMargin, mPaintScale);
                } else if (index == 3) {
                    canvas.drawBitmap(mBitmapScale3, mCanvasWidth - mBitmapScale3.getWidth() - mMargin
                            , (mCanvasHeight - mBitmapScale3.getHeight()) / 2, mPaintScale);
                } else if (index == 6) {
                    canvas.drawBitmap(mBitmapScale6, (mCanvasWidth - mBitmapScale6.getWidth()) / 2,
                            mCanvasHeight - mBitmapScale6.getHeight() - mMargin, mPaintScale);
                } else if (index == 9) {
                    canvas.drawBitmap(mBitmapScale9, mMargin, (mCanvasHeight - mBitmapScale9.getHeight()) / 2, mPaintScale);
                } else {
                    canvas.drawCircle(cxPoint, cyPoint, POINT_RADIUS, mPaintPoint);
                    digit = String.valueOf(12 - index);
                    mRect.setEmpty();
                    mPaintText.getTextBounds(digit, 0, digit.length(), mRect);
                    canvas.drawText(digit, cxText - 1.0f * mRect.width() / 2, cyText + 1.0f * mRect.height() / 2, mPaintText);
                }
            }
        }

        private void invalidate() {
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    this.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            mHandler.removeMessages(111);
            mHandler.sendEmptyMessageDelayed(111, 1000);
        }

        private final Handler mHandler = new Handler() {
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                invalidate();
            }
        };
    }

}
