package com.suheng.structure.wallpaper.boneblack;

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

        private Paint mPaintBitmap;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private BitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = BoneBlackWallpaperService.this;

            mBitmapManager = new BitmapManager(mContext);

            mPaintBitmap = new Paint();
            mPaintBitmap.setAntiAlias(true);
            mPaintBitmap.setStyle(Paint.Style.FILL);
            mPaintBitmap.setColor(Color.parseColor("#FF3333"));
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
            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusOuter, mPaintScale);
            this.paintScale(canvas);
            this.paintIconInfo(canvas);
            this.paintDate(canvas);
            this.paintPointer(canvas);
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
                        bitmap = mBitmapManager.getRotate(R.drawable.basic_icon_battary, -degrees);
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() - mMarginIconText;
                        top = mMarginRadiusOuter + mMarginRadiusOuter * 3.5f;
                        break;
                    case 6:
                        bitmap = mBitmapManager.getRotate(R.drawable.boneblack_scale_number_6, -degrees);
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
                        top = mMarginRadiusOuter;

                        mRadiusInner = mRadiusOuter - 1.0f * bitmap.getHeight();
                        break;
                    case 9:
                        bitmap = mBitmapManager.getRotate(R.drawable.basic_icon_weather_day_duoyun, -degrees);
                        left = mPointScreenCenter.x + mMarginIconText;
                        top = mMarginRadiusOuter + mMarginRadiusOuter * 3.5f;
                        break;
                    default:
                        if (index == 0) {
                            bitmap = mBitmapManager.get(R.drawable.boneblack_sacle_number_12);
                        } else {
                            bitmap = mBitmapManager.get(R.drawable.boneblack_scale_paperclip, R.color.boneblack_wallpaper_scale_paperclip);
                        }
                        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
                        top = mMarginRadiusOuter;
                        break;
                }

                canvas.drawBitmap(bitmap, left, top, mPaintBitmap);
                //canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y, mPointScreenCenter.x, mMarginRadiusOuter, mPaintScale);
                canvas.restore();
            }

            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusInner, mPaintScale);

            //bitmap = mArrayBitmapScale.get(R.drawable.boneblack_hand_second);
            //canvas.drawBitmap(bitmap, mMarginRadiusOuter, mPointScreenCenter.y, mPaintScale);
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
            float top = mPointScreenCenter.y + (mRadiusInner - bitmap.getHeight()) / 2 + 8;
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);

            //星期
            bitmap = mBitmapManager.get(R.drawable.basic_text_week);//星期
            int weekMarginLeft = bitmap.getWidth() + DimenUtil.dip2px(mContext, 4);
            offset += weekMarginLeft;
            canvas.drawBitmap(bitmap, offset, top, mPaintBitmap);
            offset += bitmap.getWidth();

            bitmap = mBitmapManager.getWeekBitmap();
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
        }

        private void paintPointer(Canvas canvas) {
            int hour = Calendar.getInstance().get(Calendar.HOUR);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int second = Calendar.getInstance().get(Calendar.SECOND);
            //Log.d(TAG, "time, hour = " + hour + ", minute = " + minute + ", second = " + second);

            float degreesHour = (hour + 1.0f * minute / 60) * 360 / 12;
            float degreesMinute = (minute + 1.0f * second / 60) * 360 / 60;
            float degreesSecond = 1.0f * second * 360 / 60;

            int margin = DimenUtil.dip2px(mContext, 12);
            int bottomOffset = DimenUtil.dip2px(mContext, 20);
            float bottom = mPointScreenCenter.y - bottomOffset;
            int shadowColor = Color.parseColor("#2E42FF");

            float pointerWidth = DimenUtil.dip2px(mContext, 5.6f);//分针宽度
            float top = mMarginRadiusOuter + (mRadiusOuter - mRadiusInner) + margin;
            canvas.save();
            canvas.rotate(degreesMinute, mPointScreenCenter.x, mPointScreenCenter.y);
            mRectF.set(mPointScreenCenter.x - pointerWidth / 2, top,
                    mPointScreenCenter.x + pointerWidth / 2, bottom);
            mPaintBitmap.setShadowLayer(pointerWidth, 0, 0, shadowColor);//外围阴影效果
            mPaintBitmap.setColor(Color.WHITE);
            canvas.drawRoundRect(mRectF, pointerWidth, pointerWidth, mPaintBitmap);

            mPaintBitmap.clearShadowLayer();//分针内部小黑棒
            mRectF.set(mPointScreenCenter.x - pointerWidth / 5, top + bottomOffset * 0.2f,
                    mPointScreenCenter.x + pointerWidth / 5, bottom - 1.4f * bottomOffset);
            mPaintBitmap.setColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));
            canvas.drawRoundRect(mRectF, pointerWidth / 5, pointerWidth / 5, mPaintBitmap);
            canvas.restore();

            pointerWidth = pointerWidth * 1.5f;//时针宽度
            top += margin * 1.5;
            canvas.save();
            canvas.rotate(degreesHour, mPointScreenCenter.x, mPointScreenCenter.y);
            mRectF.set(mPointScreenCenter.x - pointerWidth / 2, top,
                    mPointScreenCenter.x + pointerWidth / 2, bottom);
            mPaintBitmap.setColor(Color.WHITE);
            mPaintBitmap.setShadowLayer(pointerWidth, 0, 0, shadowColor);
            canvas.drawRoundRect(mRectF, pointerWidth, pointerWidth, mPaintBitmap);

            mPaintBitmap.clearShadowLayer();//时针内部小黑棒
            mRectF.set(mPointScreenCenter.x - pointerWidth / 5, top + bottomOffset * 0.2f,
                    mPointScreenCenter.x + pointerWidth / 5, bottom - 1.4f * bottomOffset);
            mPaintBitmap.setColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));
            canvas.drawRoundRect(mRectF, pointerWidth / 5, pointerWidth / 5, mPaintBitmap);
            canvas.restore();

            pointerWidth = pointerWidth / 3f;//秒针宽度
            top = mMarginRadiusOuter;
            canvas.save();
            canvas.rotate(degreesSecond, mPointScreenCenter.x, mPointScreenCenter.y);
            mRectF.set(mPointScreenCenter.x - pointerWidth / 2, top,
                    mPointScreenCenter.x + pointerWidth / 2, bottom);
            mPaintBitmap.setShadowLayer(pointerWidth, 0, 0, Color.parseColor("#FF2E2E"));

            mPaintBitmap.setColor(Color.parseColor("#FF3333"));
            canvas.drawRoundRect(mRectF, pointerWidth, pointerWidth, mPaintBitmap);
            canvas.restore();

            mPaintBitmap.clearShadowLayer();

            /*canvas.save();//中心点
            mPaintScale.setStrokeWidth(2);
            mPaintScale.setColor(Color.RED);
            mPaintScale.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, bottomOffset, mPaintScale);
            canvas.restore();*/
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
