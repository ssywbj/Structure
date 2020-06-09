package com.suheng.structure.wallpaper.myhealth;

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

import java.util.Calendar;

public class MyHealthWatchFace extends WallpaperService {
    public static final String TAG = MyHealthWatchFace.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final int SCALES = 6;
        private static final float SCALE_DEGREES = 1.0f * 360 / SCALES;
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter, mRadiusInner;

        private Paint mPaint;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;
        private int mLittleTriangleHeight;

        private MyHealthBitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = MyHealthWatchFace.this;

            mBitmapManager = new MyHealthBitmapManager(mContext);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                    - DimenUtil.dip2px(mContext, 1);
            mRadiusInner = mRadiusOuter - DimenUtil.dip2px(mContext, 36);

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
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black));//画面背景
            mPaint.setStyle(Paint.Style.STROKE);

            float radius = mRadiusInner - DimenUtil.dip2px(mContext, 8);
            mRectF.set(mPointScreenCenter.x - radius, mPointScreenCenter.y - radius
                    , mPointScreenCenter.x + radius, mPointScreenCenter.y + radius);
            //canvas.drawRect(mRectF, mPaint);
            canvas.drawBitmap(mBitmapManager.get(R.drawable.my_health_stripe_bg), null, mRectF, null);

            float strokeWidth = 1f;
            //-90：从矩形区域顶边中点开始(-110在它的左侧)；0：从矩形区域右边中点开始；90：从矩形区域底边中点开始；180：从矩形区域左边中点开始
            float startAngle, sweepAngle;
            for (int index = 0; index < SCALES; index++) {
                canvas.save();
                canvas.rotate(index * SCALE_DEGREES, mPointScreenCenter.x, mPointScreenCenter.y);

                //外弧
                mPaint.setStrokeWidth(strokeWidth * 1.5f);
                mPaint.setColor(Color.parseColor("#1E1E1E"));
                startAngle = -114;
                sweepAngle = (Math.abs(startAngle) - Math.abs(-90)) * 2;
                //在这个矩形区域里画弧
                mRectF.set(mPointScreenCenter.x - mRadiusOuter, mPointScreenCenter.y - mRadiusOuter
                        , mPointScreenCenter.x + mRadiusOuter, mPointScreenCenter.y + mRadiusOuter);
                canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);

                //内弧
                if (index != 5) {
                    mPaint.setStrokeWidth(strokeWidth * 2);
                    mPaint.setColor(Color.parseColor("#656565"));
                    startAngle = -112;
                    sweepAngle = (Math.abs(startAngle) - Math.abs(-90)) * 2;
                    mRectF.set(mPointScreenCenter.x - mRadiusInner, mPointScreenCenter.y - mRadiusInner
                            , mPointScreenCenter.x + mRadiusInner, mPointScreenCenter.y + mRadiusInner);
                    canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
                }
                canvas.restore();
            }

            Bitmap bitmap = mBitmapManager.get(R.drawable.my_health_little_triangle, R.color.my_health_little_triangle);
            mLittleTriangleHeight = bitmap.getHeight();
            float degrees;
            for (int index = 0; index < SCALE_DEGREES * 2; index++) {
                canvas.save();
                degrees = index * SCALE_DEGREES / 2;
                canvas.rotate(degrees, mPointScreenCenter.x, mPointScreenCenter.y);
                if (degrees % 60 != 0) {
                    canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                            , mPointScreenCenter.y - mRadiusInner, null);

                    /*canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y
                            , mPointScreenCenter.x, mPointScreenCenter.y - mRadiusOuter, mPaint);*/
                }
                canvas.restore();
            }

            this.paintTime(canvas);
            this.paintIconInfo(canvas);
        }

        private void paintTime(Canvas canvas) {
            Calendar instance = Calendar.getInstance();
            //分钟
            int color = android.R.color.white;
            int minute = instance.get(Calendar.MINUTE);
            int units = minute % 10;//个位
            int tens = minute / 10;//十位
            Bitmap bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            float left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            float hourBitmapHeight = 1.0f * bitmap.getHeight() / 2;
            float top = mPointScreenCenter.y - hourBitmapHeight;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_minute_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //秒钟
            color = R.color.second_number;
            int second = instance.get(Calendar.SECOND);
            units = second % 10;//个位
            tens = second / 10;//十位
            left = mPointScreenCenter.x + mRadiusInner / 2;
            top = mPointScreenCenter.y + hourBitmapHeight;
            bitmap = mBitmapManager.getMerge(mBitmapManager.getSmallNumberResId(tens), color, mBitmapManager.getSmallNumberResId(units), color);
            left -= DimenUtil.dip2px(mContext, 6);
            top -= (bitmap.getHeight() + 2);
            canvas.drawBitmap(bitmap, left, top, null);
            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //时钟
            color = R.color.my_health_little_triangle;
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getMiddleNumberResId(tens), color, mBitmapManager.getMiddleNumberResId(units), color);
            left = mPointScreenCenter.x - mRadiusInner - DimenUtil.dip2px(mContext, 4);
            top = mPointScreenCenter.y - bitmap.getHeight() - mLittleTriangleHeight;
            canvas.drawBitmap(bitmap, left, top, null);

            int marginBottom = DimenUtil.dip2px(mContext, 2);
            float lineLeft = left + 1.0f * bitmap.getWidth() / 4;
            int lineHeight = DimenUtil.dip2px(mContext, 4);
            float lineTop = top - (marginBottom + lineHeight);
            float lineLen = 1.0f * bitmap.getWidth() / 4;

            //黄线上方的日期
            color = android.R.color.white;
            int month = instance.get(Calendar.MONTH) + 1;
            units = month % 10;//个位
            tens = month / 10;//十位
            left += 1.0f * bitmap.getWidth() / 2;
            bitmap = mBitmapManager.getMerge(mBitmapManager.getSmallerNumberResId(tens), color
                    , mBitmapManager.getSmallerNumberResId(units), color);
            top -= (bitmap.getHeight() + marginBottom + 2.6f * lineHeight);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_diagonal, color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            int day = instance.get(Calendar.DAY_OF_MONTH);
            units = day % 10;//个位
            tens = day / 10;//十位
            left += bitmap.getWidth();
            bitmap = mBitmapManager.getMerge(mBitmapManager.getSmallerNumberResId(tens), color
                    , mBitmapManager.getSmallerNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            //时钟与时间之前的黄线
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.my_health_little_triangle));
            mRectF.set(lineLeft, lineTop - 1.0f * lineHeight / 2
                    , lineLeft + lineLen, lineTop + 1.0f * lineHeight / 2);
            canvas.drawRoundRect(mRectF, 1.0f * lineHeight / 2, 1.0f * lineHeight / 2, mPaint);
        }

        private void paintIconInfo(Canvas canvas) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.RED);
            float radius = mRadiusInner + (mRadiusOuter - mRadiusInner) / 2;
            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, radius, mPaint);

            final int scales = 100;
            float scaleDegree = 1.0f * 360 / scales, rotateDegrees;
            float stopY = mPointScreenCenter.y - radius;
            Bitmap bitmap;
            for (int index = 0; index < scales; index++) {
                rotateDegrees = index * scaleDegree;

                if (rotateDegrees > 254 && rotateDegrees < 340) {
                    continue;
                }

                canvas.save();
                canvas.rotate(rotateDegrees, mPointScreenCenter.x, mPointScreenCenter.y);//画布旋转后，在低分辨率手机上bitmap会有些锯齿现象，但能接受
                //canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y, mPointScreenCenter.x, stopY, mPaint);

                if (rotateDegrees > 90 && rotateDegrees < 254) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_a + index % 26,
                            R.color.colorPrimary);
                } else {
                    //旋转回相应的角度，目的是摆正图片。但再次旋转后，在低分辨率手机上锯齿现象会严重加剧，不能接受
                    /*bitmap = mBitmapManager.getRotate(R.drawable.alphabet_uppercase_a + index % 26,
                            R.color.alphabet_uppercase, -rotateDegrees);*/
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a + index % 26,
                            R.color.alphabet_uppercase);
                }

                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                            , stopY - 1.0f * bitmap.getHeight() / 2, null);
                }
                canvas.restore();
            }
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
