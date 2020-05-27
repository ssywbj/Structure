package com.suheng.structure.wallpaper.parisblack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
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
        private float mRadiusOuter;//刻度外半径长度

        private Paint mPaintBitmap;
        private int mCenterLineLen;
        private RectF mRectF = new RectF();
        private Rect mRect = new Rect();

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
            mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y);//屏幕半径

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

            mPaintBitmap.setStyle(Paint.Style.FILL);
            mRectF.set(mPointScreenCenter.x - lineLen / 2, mPointScreenCenter.y - lineWidth / 2,
                    mPointScreenCenter.x + lineLen / 2, mPointScreenCenter.y + lineWidth / 2);
            canvas.drawRoundRect(mRectF, lineWidth, lineWidth, mPaintBitmap);

            this.paintDate(canvas);
            this.paintIconInfo(canvas);
            this.paintBattery(canvas);
        }

        private void paintIconInfo(Canvas canvas) {
            //canvas.drawLine(mPointScreenCenter.x, 0, mPointScreenCenter.x, 2 * mRadiusOuter, mPaintBitmap);

            Bitmap bitmap = mBitmapManager.get(R.drawable.basic_icon_weather_day_duoyun);
            float top = 2 * mRadiusOuter - bitmap.getHeight() - DimenUtil.dip2px(mContext, 70);
            float v = 1.0f * bitmap.getWidth() / 2;
            canvas.drawBitmap(bitmap, mPointScreenCenter.x - v, top, mPaintBitmap);

            int margin = DimenUtil.dip2px(mContext, 1.5f);
            float offset = mPointScreenCenter.x + margin;
            top += bitmap.getHeight() + DimenUtil.dip2px(mContext, 4);

            bitmap = mBitmapManager.get(R.drawable.basic_temperature_unit);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            offset = mPointScreenCenter.x - margin;

            bitmap = mBitmapManager.get(R.drawable.basic_number_5);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            bitmap = mBitmapManager.get(R.drawable.basic_number_2);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
        }

        private void paintBattery(Canvas canvas) {
            mPaintBitmap.setStrokeWidth(DimenUtil.dip2px(mContext, 4));
            mPaintBitmap.setStyle(Paint.Style.STROKE);
            int margin = DimenUtil.dip2px(mContext, 8);
            float percent = 1.0f * 51 / 360;
            int percentage = (int) (percent * 100);
            float sweepAngle = percent * 360;
            canvas.drawArc(margin, margin, 2 * mPointScreenCenter.x - margin, 2 * mPointScreenCenter.y - margin
                    , -90, sweepAngle, false, mPaintBitmap);//-90：最顶部，0：最右侧，90：最底部，180：最左侧

            /*Bitmap leftBitmap = mBitmapManager.get(mContext, R.drawable.basic_number_5, R.color.basic_number_color);
            Bitmap rightBitmap = mBitmapManager.get(mContext, R.drawable.basic_number_9, R.color.basic_number_color);
            Bitmap dst = BitmapManager.mergeLeftRight(leftBitmap, rightBitmap);
            if (dst != null) {
                dst = BitmapManager.mergeLeftRight(dst, mBitmapManager.get(mContext, R.drawable.basic_sign_percentage, R.color.basic_number_color));
                if (dst != null) {
                    canvas.drawBitmap(dst, mPointScreenCenter.x - 1.0f * dst.getWidth() / 2,
                            (mPointScreenCenter.y - 1.0f * dst.getHeight()) / 2, mPaintBitmap);
                }
            }*/

            mPaintBitmap.setStyle(Paint.Style.FILL);
            String text = percentage + "%";
            mPaintBitmap.setTextSize(DimenUtil.dip2px(mContext, 28));
            mPaintBitmap.getTextBounds(text, 0, text.length(), mRect);

            int units = percentage % 10;//个位
            int tens = percentage / 10;//十位

            Bitmap leftBitmap = mBitmapManager.get(mContext, mBitmapManager.getNumberResId(tens), R.color.basic_number_color);
            Bitmap rightBitmap = mBitmapManager.get(mContext, mBitmapManager.getNumberResId(units), R.color.basic_number_color);
            Bitmap dst = BitmapManager.mergeLeftRight(leftBitmap, rightBitmap);
            if (dst != null) {
                dst = BitmapManager.mergeLeftRight(dst, mBitmapManager.get(mContext, R.drawable.basic_sign_percentage, R.color.basic_number_color));
                if (dst != null) {
                    Matrix matrix = new Matrix();
                    matrix.preTranslate(70, 70);
                    matrix.postScale(1.8f, 1.8f);
                    /*canvas.drawBitmap(dst, matrix, mPaintBitmap);*/
                    /*canvas.drawBitmap(dst, mPointScreenCenter.x - 1.0f * dst.getWidth() / 2,
                            (mPointScreenCenter.y - 1.0f * dst.getHeight()) / 2, mPaintBitmap);*/
                }
            }

            if (dst == null) {
                return;
            }

            Log.d(TAG, "percentage = " + percentage + ", units = " + units + ", tens = " + tens);
            int degrees;
            Bitmap bitmap;
            //for (int i = 0; i < 12; i++) {
            canvas.save();
            //degrees = 30 * i;
            canvas.rotate(sweepAngle, mPointScreenCenter.x, mPointScreenCenter.y);//旋转后有时合成的图片变模糊？

            bitmap = BitmapManager.rotate(dst, -sweepAngle);
            canvas.drawBitmap(bitmap, mPointScreenCenter.x, 0, mPaintBitmap);

            canvas.restore();
            //}

            /*canvas.drawText(text, mPointScreenCenter.x - 1.0f * mRect.width() / 2,
                    margin + DimenUtil.dip2px(mContext, 4) + mRect.height(), mPaintBitmap);*/
        }

        private void paintDate(Canvas canvas) {
            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);
            int week = instance.get(Calendar.DAY_OF_WEEK);

            float ratio = 1.7f;
            //mPaintBitmap.setShadowLayer(0.1f, 0, 0, Color.parseColor("#FF0000"));//外围阴影效果

            float offset = mPointScreenCenter.x;
            Bitmap bitmap = mBitmapManager.getScale(R.drawable.basic_text_day, ratio);
            int marginTop = DimenUtil.dip2px(mContext, 14);
            float top = mPointScreenCenter.y + marginTop;
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            //星期
            bitmap = mBitmapManager.getScale(R.drawable.basic_text_week, ratio);//星期
            int weekMarginLeft = bitmap.getWidth() + DimenUtil.dip2px(mContext, 10);
            offset += weekMarginLeft;
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            offset += bitmap.getWidth();

            bitmap = mBitmapManager.getScale(mBitmapManager.getWeekResId(), ratio - 0.2f);
            canvas.drawBitmap(bitmap, offset, top + 2, mPaintBitmap);

            //号数
            int units = day % 10;//个位
            int tens = day / 10;//十位
            offset = mPointScreenCenter.x;
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(units), ratio);
            offset -= bitmap.getWidth();
            if (tens > 0) {
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
                bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(tens), ratio);
                offset -= bitmap.getWidth();
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            }

            //月份
            bitmap = mBitmapManager.getScale(R.drawable.basic_text_month, ratio);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(units), ratio);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            if (tens > 0) {
                mBitmapManager.getScale(mBitmapManager.getNumberResId(tens), ratio);
                offset -= bitmap.getWidth();
                canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            }

            /*Log.d(TAG, "date, month = " + month + ", day = " + day + ", week = " + week
                    + ", tens = " + tens + ", units = " + units);*/

            //冒号
            float v = 1.0f * bitmap.getWidth() / 2;
            offset = mPointScreenCenter.x - v;
            bitmap = mBitmapManager.get(R.drawable.basic_sign_colon);
            top = mPointScreenCenter.y - (bitmap.getHeight() + marginTop);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            int hour = instance.get(Calendar.HOUR);
            int minute = instance.get(Calendar.MINUTE);

            offset = mPointScreenCenter.x + v;

            //分钟
            units = minute % 10;//个位
            tens = minute / 10;//十位
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(tens), ratio);
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(units), ratio);
            offset += bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            offset = mPointScreenCenter.x - v - 2;

            //时钟
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(units), ratio);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            bitmap = mBitmapManager.getScale(mBitmapManager.getNumberResId(tens), ratio);
            offset -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            mPaintBitmap.clearShadowLayer();
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
