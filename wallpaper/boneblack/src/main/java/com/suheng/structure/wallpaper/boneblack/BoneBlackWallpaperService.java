package com.suheng.structure.wallpaper.boneblack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suheng.structure.wallpaper.basic.DimenUtil;
import com.suheng.structure.wallpaper.basic.utils.BitmapUtil;

import java.util.Calendar;

public class BoneBlackWallpaperService extends WallpaperService {
    public static final String TAG = BoneBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final int SCALES = 12;//12个刻度

        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter;//刻度外半径长度
        private float mRadiusInner;//刻度内半径长度
        private int mMarginRadiusOuter;//屏幕边缘到刻度外半径的留白
        private int mMarginIconText;//信息图标与其下方文案的留白

        private Paint mPaintScale;
        private SparseArray<Bitmap> mArrayBitmapScale = new SparseArray<>();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = BoneBlackWallpaperService.this;

            this.initBitmap();

            mPaintScale = new Paint();
            mPaintScale.setAntiAlias(true);
            mPaintScale.setColor(Color.RED);
            mPaintScale.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            float screenRadius = Math.min(mPointScreenCenter.x, mPointScreenCenter.y);//屏幕半径

            mMarginIconText = DimenUtil.dip2px(mContext, 2);
            mMarginRadiusOuter = DimenUtil.dip2px(mContext, 4);
            mRadiusOuter = screenRadius - mMarginRadiusOuter;
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

            this.releaseBitmap();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            mVisible = false;
            mHandler.removeMessages(111);
            this.releaseBitmap();
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

        private void initBitmap() {
            mArrayBitmapScale.put(R.drawable.boneblack_sacle_number_12
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_sacle_number_12));
            mArrayBitmapScale.put(R.drawable.boneblack_icon_battary
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_battary));
            mArrayBitmapScale.put(R.drawable.boneblack_scale_number_6
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_number_6));
            mArrayBitmapScale.put(R.drawable.boneblack_icon_weather_day_duoyun
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_weather_day_duoyun));
            mArrayBitmapScale.put(R.drawable.boneblack_scale_paperclip
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_paperclip));

            mArrayBitmapScale.put(R.drawable.basic_percentage_sign
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.basic_percentage_sign));
            mArrayBitmapScale.put(R.drawable.basic_temperature_unit
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.basic_temperature_unit));
            mArrayBitmapScale.put(R.drawable.boneblack_hand_hour
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_hand_hour));
            mArrayBitmapScale.put(R.drawable.boneblack_hand_minute
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_hand_minute));
            mArrayBitmapScale.put(R.drawable.boneblack_hand_second
                    , BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_hand_second));
        }

        private void releaseBitmap() {
            for (int index = 0; index < mArrayBitmapScale.size(); index++) {
                Bitmap bitmap = mArrayBitmapScale.get(index);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            mArrayBitmapScale.clear();
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

        private void onDraw(Canvas canvas) {
            //Log.d(TAG, "onDraw, draw watch face, width = " + mScreenWidth + ", height = " + mScreenHeight);
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));//画面背景
            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusOuter, mPaintScale);
            this.paintScale(canvas);
            this.paintIconInfo(canvas);
            this.paintDate(canvas);
        }

        private void paintScale(Canvas canvas) {
            Bitmap bitmap;
            float left, top, degrees;
            final float scaleDegree = 1.0f * 360 / SCALES;//刻度角

            for (int index = 0; index < SCALES; index++) {
                degrees = scaleDegree * index;

                canvas.save();
                canvas.rotate(degrees, mPointScreenCenter.x, mPointScreenCenter.y);

                switch (index) {
                    case 3:
                        bitmap = BitmapUtil.rotate(mArrayBitmapScale.get(R.drawable.boneblack_icon_battary), -degrees);
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() - mMarginIconText;
                        top = mMarginRadiusOuter + mMarginRadiusOuter * 3.5f;
                        break;
                    case 6:
                        bitmap = BitmapUtil.rotate(mArrayBitmapScale.get(R.drawable.boneblack_scale_number_6), -degrees);
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
                        top = mMarginRadiusOuter;

                        mRadiusInner = mRadiusOuter - 1.0f * bitmap.getHeight();
                        break;
                    case 9:
                        bitmap = BitmapUtil.rotate(mArrayBitmapScale.get(R.drawable.boneblack_icon_weather_day_duoyun), -degrees);
                        left = mPointScreenCenter.x + mMarginIconText;
                        top = mMarginRadiusOuter + mMarginRadiusOuter * 3.5f;
                        break;
                    default:
                        if (index == 0) {
                            bitmap = mArrayBitmapScale.get(R.drawable.boneblack_sacle_number_12);
                        } else {
                            bitmap = mArrayBitmapScale.get(R.drawable.boneblack_scale_paperclip);
                        }
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
                        top = mMarginRadiusOuter;
                        break;
                }

                canvas.drawBitmap(bitmap, left, top, mPaintScale);
                //canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y, mPointScreenCenter.x, mMarginRadiusOuter, mPaintScale);
                canvas.restore();
            }

            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusInner, mPaintScale);
        }

        private void paintIconInfo(Canvas canvas) {
            int offset = mMarginRadiusOuter * 3;
            float top = mPointScreenCenter.y + mMarginIconText;

            Bitmap bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_2);
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);
            offset += bitmap.getWidth();
            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_5);
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);
            offset += bitmap.getWidth();
            bitmap = mArrayBitmapScale.get(R.drawable.basic_temperature_unit);
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            bitmap = mArrayBitmapScale.get(R.drawable.basic_percentage_sign);
            offset = bitmap.getWidth() + mMarginRadiusOuter * 3;
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintScale);
            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_0);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintScale);
            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_7);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintScale);
        }

        private void paintDate(Canvas canvas) {
            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);
            int week = instance.get(Calendar.DAY_OF_WEEK);
            Log.i(TAG, "date, month = " + month + ", day = " + day + ", week = " + week);

            float offset = mPointScreenCenter.x;
            Bitmap bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_text_day);
            float top = mPointScreenCenter.y + (mRadiusInner - bitmap.getHeight()) / 2 + 8;
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_text_week);
            offset += bitmap.getWidth() + DimenUtil.dip2px(mContext, 4);
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            bitmap = BitmapUtil.getWeekBitmap(mContext);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            offset = mPointScreenCenter.x;
            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_9);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_text_month);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);

            bitmap = BitmapUtil.getFromDrawable(mContext, R.drawable.basic_number_5);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintScale);
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
