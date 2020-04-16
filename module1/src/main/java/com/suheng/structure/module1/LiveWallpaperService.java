package com.suheng.structure.module1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.List;

public class LiveWallpaperService extends WallpaperService {
    public static final String TAG = LiveWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final float POINT_RADIUS = 5.0f;//圆点半径
        private static final int POINTS = 60;//绘制60个点
        private final double RADIANS = Math.toRadians(1.0f * 360 / POINTS);//弧度值，Math.toRadians：度换算成弧度

        //Digital clock color in interactive mode
        private boolean mVisible = false;
        private boolean mRegisteredTimeTickReceiver = false;

        private float mCenterX;//圆心X坐标
        private float mCenterY;//圆心Y坐标
        private float mMaxRadius;
        private Rect mRect = new Rect();

        private boolean mAmbientMode;
        private Paint mPaintText, mPaintPointer, mPaintPoint;

        private Context mContext;

        private final Handler mHandler = new Handler() {
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                invalidate();
            }
        };

        private boolean mRegisteredBatteryReceiver = false;
        private String mTextBattery;
        private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);//当前电量
                    int total = intent.getIntExtra("scale", 100);//总电量
                    int percentage = (level * 100) / total;
                    mTextBattery = "Battery:" + percentage + "%";
                    Log.d(TAG, "battery, level = " + level + ", total = " + total + ", percentage = " + percentage);
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = LiveWallpaperService.this;

            mPaintText = new Paint();
            mPaintText.setColor(Color.WHITE);
            mPaintText.setAntiAlias(true);
            mPaintText.setTextSize(50f);
            mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

            mPaintPoint = new Paint();
            mPaintPoint.setColor(Color.WHITE);
            mPaintPoint.setAntiAlias(true);

            mPaintPointer = new Paint();
            mPaintPointer.setAntiAlias(true);
            mPaintPointer.setDither(true);
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
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            //Log.d(TAG, "onDraw, draw watch face, width = " + width + ", height = " + height);

            int hour = Calendar.getInstance().get(Calendar.HOUR);//时
            int minute = Calendar.getInstance().get(Calendar.MINUTE);//分
            int second = Calendar.getInstance().get(Calendar.SECOND);//秒
            //Log.d(TAG, "---FaceEngine, paintPointer---hour = " + hour + ", minute = " + minute + ", second = " + second);

            String text = "zhipu";
            float textPaintHeight;
            if (mAmbientMode) {
                canvas.drawColor(ContextCompat.getColor(mContext, R.color.zhipu_watchface_ambient_mode));//画面背景

                mPaintText.setTextSize(64f);
                mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                String time = addZero(hour) + ":" + addZero(minute);
                mRect.setEmpty();
                mPaintText.getTextBounds(time, 0, time.length(), mRect);
                canvas.drawText(time, (width - mPaintText.measureText(time)) / 2, 1.0f * height / 2 - 12, mPaintText);

                //canvas.drawLine(0, height / 2, width, height / 2, mPaintText);//垂直居中线

                mPaintText.setTextSize(50f);
                mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                mRect.setEmpty();
                mPaintText.getTextBounds(text, 0, text.length(), mRect);
                //textPaintHeight = 1.0f * height / 2 + 1.0f * mRect.height() / 4;//文字垂直居中
                textPaintHeight = (height + 1.0f * mRect.height()) / 2 + 12;
                canvas.drawText(text, (width - mPaintText.measureText(text)) / 2, textPaintHeight, mPaintText);
            } else {
                canvas.drawColor(ContextCompat.getColor(mContext, R.color.zhipu_watchface_interactive_mode));//画面背景

                //textPaintHeight = (height - mPaintText.getTextSize() + 16);//文字到屏幕底部有一定间距
                textPaintHeight = (mCenterY + mMaxRadius + 40);//文字到屏幕底部有一定间距

                this.paintPointer(canvas, hour, minute, second);
                this.paintScaleNumber(canvas);

                mPaintText.setTextSize(50f);
                mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                canvas.drawText(text, (width - mPaintText.measureText(text)) / 2, textPaintHeight, mPaintText);

                if (!TextUtils.isEmpty(mTextBattery)) {
                    mPaintText.setTextSize(30f);
                    mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    float dimen = mPaintText.measureText(mTextBattery);
                    canvas.drawText(mTextBattery, (width - dimen) / 2, mCenterY + dimen / 2 - 10, mPaintText);
                }

                //this.drawComplications(canvas, System.currentTimeMillis());
                //this.drawUnreadNotificationIcon(canvas);
            }
        }

        private void paintScaleNumber(Canvas canvas) {
            canvas.save();

            mPaintText.setTextSize(26f);
            mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            String digit = "0";
            mPaintText.getTextBounds(digit, 0, digit.length(), mRect);

            mPaintText.setStyle(Paint.Style.STROKE);
            float radius = mMaxRadius - 30;
            float radiusText = radius - mRect.height() - 4;

            mPaintText.setStyle(Paint.Style.FILL);
            float cxPoint, cyPoint, cxText, cyText;
            double sinValue, cosValue;
            for (int index = 0; index < POINTS; index++) {
                sinValue = Math.sin(RADIANS * index);
                cosValue = Math.cos(RADIANS * index);
                cxPoint = (float) (mCenterX + radius * sinValue);
                cyPoint = (float) (mCenterY - radius * cosValue);
                cxText = (float) (mCenterX - radiusText * sinValue);
                cyText = (float) (mCenterY - radiusText * cosValue);
                if (index % 5 == 0) {
                    canvas.drawCircle(cxPoint, cyPoint, POINT_RADIUS, mPaintPoint);

                    digit = String.valueOf((index / 5) == 0 ? 12 : 12 - (index / 5));
                    mRect.setEmpty();
                    mPaintText.getTextBounds(digit, 0, digit.length(), mRect);
                    canvas.drawText(digit, cxText - 1.0f * mRect.width() / 2, cyText + 1.0f * mRect.height() / 2, mPaintText);
                } else {
                    canvas.drawCircle(cxPoint, cyPoint, POINT_RADIUS / 2, mPaintPoint);
                }
            }

            canvas.restore();
        }

        private String addZero(int number) {
            StringBuilder sBuilder = new StringBuilder(String.valueOf(number));
            if (sBuilder.length() < 2) {
                sBuilder.insert(0, "0");
            }
            return sBuilder.toString();
        }

        private float mHourPointWidth = 4;//时针宽度
        private float mMinutePointWidth = 3;//分针宽度
        private float mSecondPointWidth = 2;//秒针宽度
        private int mHourPointColor = Color.RED; //时针的颜色
        private int mMinutePointColor = Color.BLACK;//分针的颜色
        private int mSecondPointColor = Color.WHITE;//秒针的颜色
        private float mPointRadius = 2;//指针圆角
        private float mPointEndLength = 20;//指针末尾长度

        private void paintPointer(Canvas canvas, int hour, int minute, int second) {
            float radius = mMaxRadius - 32;

            //转过的角度
            float angleHour = (hour + (float) minute / 60) * 360 / 12;
            float angleMinute = (minute + (float) second / 60) * 360 / 60;
            int angleSecond = second * 360 / 60;

            //绘制时针
            canvas.save();
            canvas.rotate(angleHour, mCenterX, mCenterY); // 旋转到时针的角度
            RectF rectHour = new RectF(mCenterX - mHourPointWidth / 2, mCenterY - radius * 3 / 6,
                    mCenterX + mHourPointWidth / 2, mCenterY + mPointEndLength);
            mPaintPointer.setColor(mHourPointColor);
            mPaintPointer.setStyle(Paint.Style.STROKE);
            mPaintPointer.setStrokeWidth(mHourPointWidth);
            canvas.drawRoundRect(rectHour, mPointRadius, mPointRadius, mPaintPointer);
            canvas.restore();
            //绘制分针
            canvas.save();
            canvas.rotate(angleMinute, mCenterX, mCenterY); //旋转到分针的角度
            RectF rectMinute = new RectF(mCenterX - mMinutePointWidth / 2, mCenterY - radius * 3.5f / 5,
                    mCenterX + mMinutePointWidth / 2, mCenterY + mPointEndLength);
            mPaintPointer.setColor(mMinutePointColor);
            mPaintPointer.setStrokeWidth(mMinutePointWidth);
            canvas.drawRoundRect(rectMinute, mPointRadius, mPointRadius, mPaintPointer);
            canvas.restore();
            //绘制分针
            canvas.save();
            canvas.rotate(angleSecond, mCenterX, mCenterY); //旋转到分针的角度
            RectF rectSecond = new RectF(mCenterX - mSecondPointWidth / 2, mCenterY - radius + 12,
                    mCenterX + mSecondPointWidth / 2, mCenterY + mPointEndLength);
            mPaintPointer.setStrokeWidth(mSecondPointWidth);
            mPaintPointer.setColor(mSecondPointColor);
            canvas.drawRoundRect(rectSecond, mPointRadius, mPointRadius, mPaintPointer);
            canvas.restore();

            //绘制原点
            canvas.save();
            mPaintPointer.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mCenterX, mCenterY, mSecondPointWidth * 4, mPaintPointer);
            canvas.restore();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.i(TAG, "onVisibilityChanged, visible = " + visible);
            mVisible = visible;

            /**
             * Watchface visibility has changed. Send the REQUEST_STATE
             * intent so that the home activity can send the appropriate
             * command to the wallpaper based on the current contextual
             * state.
             */
            Intent intent = new Intent("com.google.android.wearable.watchfaces.action.REQUEST_STATE");
            sendBroadcast(intent);

            if (visible) {
                registerReceivers();
                invalidate();
            } else {
                unregisterReceivers();
                mHandler.removeMessages(111);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mCenterX = 1.0f * width / 2;//圆心X坐标
            mCenterY = 1.0f * height / 2 - 28;//圆心Y坐标
            mMaxRadius = Math.min(mCenterX, mCenterY);

            invalidate();
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

                //Redraw digital clock in black and white during ambient mode
                if (mAmbientMode) {
                    invalidate();
                    unregisterReceivers();
                } else if (mVisible) {//Redraw digital clock in green during non-ambient mode
                    registerReceivers();
                    invalidate();
                }
            }
            //Update and redraw digital clock every minute
            else if (action.matches("com.google.android.wearable.action.AMBIENT_UPDATE")) {
                invalidate();
            }
            Log.i(TAG, "onCommand, action = " + action + ", x = " + x + ", y = "
                    + y + ", ambient_mode = " + mAmbientMode);
            return extras;
        }

        //Broadcast receiver to update clock time every minute
        private BroadcastReceiver mClockTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                invalidate();
            }
        };

        //Register broadcast receiver
        private void registerReceivers() {
            if (mRegisteredTimeTickReceiver) {
                return;
            }
            mRegisteredTimeTickReceiver = true;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(mClockTimeReceiver, intentFilter);

            this.registerBatteryReceiver();
        }

        //Unregister broadcast receiver
        private void unregisterReceivers() {
            if (!mRegisteredTimeTickReceiver) {
                return;
            }
            mRegisteredTimeTickReceiver = false;
            unregisterReceiver(mClockTimeReceiver);

            this.unregisterBatteryReceiver();
        }

        private void registerBatteryReceiver() {
            if (mRegisteredBatteryReceiver) {
                return;
            }
            mRegisteredBatteryReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            LiveWallpaperService.this.registerReceiver(mBatteryReceiver, filter);
        }

        private void unregisterBatteryReceiver() {
            if (!mRegisteredBatteryReceiver) {
                return;
            }
            mRegisteredBatteryReceiver = false;
            LiveWallpaperService.this.unregisterReceiver(mBatteryReceiver);
        }
    }

}
