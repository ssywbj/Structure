package com.suheng.structure.wallpaper.parisblack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suheng.structure.wallpaper.basic.DimenUtil;
import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

import java.util.Calendar;

public class ParisBlackWallpaperService extends WallpaperService {
    public static final String TAG = ParisBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private int mMarginRadiusOuter;//屏幕边缘到刻度外半径的留白
        private int mMarginIconText;//信息图标与其下方文案的留白

        private Paint mPaintBitmap;
        private int mCenterLineLen;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private BitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = ParisBlackWallpaperService.this;

            mBitmapManager = new BitmapManager(mContext);

            mPaintBitmap = new Paint();
            mPaintBitmap.setAntiAlias(true);
            mPaintBitmap.setColor(Color.RED);
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

            mBitmapManager.clear();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            mVisible = false;
            mHandler.removeMessages(111);

            mBitmapManager.clear();
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
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));//画面背景

            float lineWidth = DimenUtil.dip2px(mContext, 6), lineLen = DimenUtil.dip2px(mContext, 160);

            mRectF.set(mPointScreenCenter.x - lineLen / 2, mPointScreenCenter.y - lineWidth / 2,
                    mPointScreenCenter.x + lineLen / 2, mPointScreenCenter.y + lineWidth / 2);
            canvas.drawRoundRect(mRectF, lineWidth, lineWidth, mPaintBitmap);

            this.paintDate(canvas);
            this.paintIconInfo(canvas);
        }

        private void paintIconInfo(Canvas canvas) {
            int offset = mMarginRadiusOuter * 3;
            float top = mPointScreenCenter.y + mMarginIconText;

            Bitmap bitmap = mBitmapManager.get(R.drawable.basic_number_2);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            offset += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.basic_number_5);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            offset += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.basic_temperature_unit);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            bitmap = mBitmapManager.get(R.drawable.basic_sign_percentage);
            offset = bitmap.getWidth() + mMarginRadiusOuter * 3;
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintBitmap);
            bitmap = mBitmapManager.get(R.drawable.basic_number_0);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintBitmap);
            bitmap = mBitmapManager.get(R.drawable.basic_number_7);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, 2 * mPointScreenCenter.x - offset, top, mPaintBitmap);
        }

        private void paintDate(Canvas canvas) {
            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);
            int week = instance.get(Calendar.DAY_OF_WEEK);

            float offset = mPointScreenCenter.x;
            Bitmap bitmap = mBitmapManager.get(R.drawable.basic_text_day);
            int margin = DimenUtil.dip2px(mContext, 14);
            float top = mPointScreenCenter.y + margin;
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            //星期
            bitmap = mBitmapManager.get(R.drawable.basic_text_week);//星期
            offset += bitmap.getWidth() + DimenUtil.dip2px(mContext, 4);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            bitmap = mBitmapManager.getWeekBitmap();
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            //号数
            int units = day % 10;//个位
            int tens = day / 10;//十位
            offset = mPointScreenCenter.x;
            bitmap = mBitmapManager.getNumberBitmap(units);
            offset -= bitmap.getWidth();
            if (tens > 0) {
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
                bitmap = mBitmapManager.getNumberBitmap(tens);
                offset -= bitmap.getWidth();
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            }

            //月份
            bitmap = mBitmapManager.get(R.drawable.basic_text_month);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.getNumberBitmap(units);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            if (tens > 0) {
                bitmap = mBitmapManager.getNumberBitmap(tens);
                offset -= bitmap.getWidth();
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            }

            Log.d(TAG, "date, month = " + month + ", day = " + day + ", week = " + week
                    + ", tens = " + tens + ", units = " + units);

            int hour = instance.get(Calendar.HOUR);
            int minute = instance.get(Calendar.MINUTE);

            bitmap = mBitmapManager.get(R.drawable.basic_sign_colon);
            canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2,
                    mPointScreenCenter.y - (bitmap.getHeight() + margin), mPaintBitmap);
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
