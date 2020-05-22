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

public class BoneBlackWallpaperService extends WallpaperService {
    public static final String TAG = BoneBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final int SCALES = 12;//12个刻度
        private final double RADIANS = Math.toRadians(1.0f * 360 / SCALES);//弧度值，Math.toRadians：度换算成弧度

        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusPaint;//绘制的半径长度

        private Paint mPaintScale;
        private SparseArray<Bitmap> mArrayBitmapScale = new SparseArray<>();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

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

            int margin = DimenUtil.dip2px(mContext, 6);//屏幕边缘到绘制半径的留白
            mRadiusPaint = screenRadius - margin;
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
            Bitmap scalePaperclip = BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_paperclip);
            if (scalePaperclip == null) {
                return;
            }

            for (int index = 0; index < SCALES; index++) {
                if (index == 0) {
                    mArrayBitmapScale.put(index, BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_sacle_number_12));
                } else if (index == 3) {
                    mArrayBitmapScale.put(index, BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_battary));
                } else if (index == 6) {
                    mArrayBitmapScale.put(index, BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_scale_number_6));
                } else if (index == 9) {
                    mArrayBitmapScale.put(index, BitmapUtil.getFromDrawable(mContext, R.drawable.boneblack_icon_weather_day_duoyun));
                } else {
                    mArrayBitmapScale.put(index, BitmapUtil.rotate(scalePaperclip, (float) Math.toDegrees(RADIANS * index)));
                }
            }

            scalePaperclip.recycle();
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

        private void onDraw(Canvas canvas) {
            //Log.d(TAG, "onDraw, draw watch face, width = " + mScreenWidth + ", height = " + mScreenHeight);
            canvas.save();

            canvas.drawColor(ContextCompat.getColor(mContext, R.color.boneblack_wallpaper_bg_black));//画面背景
            /*canvas.drawBitmap(mBitmapScalePaperclip, (mCanvasWidth - mBitmapScalePaperclip.getWidth()) / 2,
                    (mCanvasHeight - mBitmapScalePaperclip.getHeight()) / 2, mPaintScale);
            canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusPaint, mPaintScale);*/
            this.paintScale(canvas);

            canvas.restore();
        }

        private void paintScale(Canvas canvas) {
            Bitmap bitmap;
            float radiusScale, leftPst, rightPst, sinValue, cosValue;
            for (int index = 0; index < SCALES; index++) {
                sinValue = (float) Math.sin(RADIANS * index);
                cosValue = (float) Math.cos(RADIANS * index);

                /*canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y, mPointScreenCenter.x + mRadiusPaint * sinValue
                        , mPointScreenCenter.y - mRadiusPaint * cosValue, mPaintScale);*/

                bitmap = mArrayBitmapScale.get(index);
                if (bitmap == null) {
                    return;
                }

                switch (index) {
                    case 0:
                    case 3:
                    case 6:
                    case 9:
                        radiusScale = mRadiusPaint - 1.0f * bitmap.getHeight() / 2;
                        break;
                    default:
                        radiusScale = mRadiusPaint - 1.0f * bitmap.getHeight() / 2.7f;
                        break;
                }
                leftPst = mPointScreenCenter.x + radiusScale * sinValue;
                rightPst = mPointScreenCenter.y - radiusScale * cosValue;
                canvas.drawBitmap(bitmap, leftPst - 1.0f * bitmap.getWidth() / 2
                        , rightPst - 1.0f * bitmap.getHeight() / 2, mPaintScale);
            }

            /*canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y
                    , mRadiusPaint - 1.0f * mArrayBitmapScale.get(6).getHeight(), mPaintScale);*/
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
