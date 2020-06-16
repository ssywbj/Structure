package com.suheng.structure.wallpaper.photoface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.core.content.ContextCompat;

import com.suheng.structure.wallpaper.basic.DimenUtil;

import java.util.Calendar;

public class PhotoFaceService extends WallpaperService {
    public static final String TAG = PhotoFaceService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private PhotoBitmapManager mBitmapManager;
        private SharedPreferences mPrefs;
        private Bitmap mBitmap;
        private RectF mRectF = new RectF();

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = PhotoFaceService.this;

            mBitmapManager = new PhotoBitmapManager(mContext);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PhotoFaceConfigActivity.ACTION_SET_WATCH_FACE_PHOTO);
            registerReceiver(mReceiver, intentFilter);

            mPrefs = getSharedPreferences(PhotoFaceConfigActivity.PREFS_FILE, MODE_PRIVATE);
            mPrefs.registerOnSharedPreferenceChangeListener(mOnPrefsChangeListener);
            this.getPhoto();
        }

        private void getPhoto() {
            String path = mPrefs.getString(PhotoFaceConfigActivity.PREFS_KEY_PATH, "");
            if (TextUtils.isEmpty(path)) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo_face_default);
            } else {
                mBitmap = BitmapFactory.decodeFile(path);
            }
        }

        private void releasePhoto() {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = null;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标

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
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            this.release();
        }

        private void release() {
            mVisible = false;
            mBitmapManager.clear();
            mPrefs.unregisterOnSharedPreferenceChangeListener(mOnPrefsChangeListener);
            this.releasePhoto();
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
            Log.d(TAG, "onCommand, action = " + action + ", x = " + x + ", y = " + y + ", ambient_mode = " + mAmbientMode);
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
        }

        private void onDraw(Canvas canvas) {
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black));//画面背景
            mRectF.set(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(mBitmap, null, mRectF, null);
            //canvas.drawBitmap(mBitmap, 0, 0, null);
            this.paintDate(canvas);
        }

        private void paintDate(Canvas canvas) {
            int color = android.R.color.white;

            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);

            //月份
            int units = month % 10;//个位
            int tens = month / 10;//十位
            Bitmap bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(0), color);
            float left = DimenUtil.dip2px(mContext, 28);
            float top = left + DimenUtil.dip2px(mContext, 14);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(8), color);
            canvas.drawBitmap(bitmap, left + bitmap.getWidth(), top, null);
            top += (bitmap.getHeight() + DimenUtil.dip2px(mContext, 12));

            //号数
            units = day % 10;//个位
            tens = day / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(2), color);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(5), color);
            canvas.drawBitmap(bitmap, left + bitmap.getWidth(), top, null);
        }

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (PhotoFaceConfigActivity.ACTION_SET_WATCH_FACE_PHOTO.equals(action)) {
                    invalidate();
                }
            }
        };

        private SharedPreferences.OnSharedPreferenceChangeListener mOnPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged, key = " + key);
                if (PhotoFaceConfigActivity.PREFS_KEY_PATH.equals(key)) {
                    releasePhoto();
                    getPhoto();
                }
            }
        };
    }

}
