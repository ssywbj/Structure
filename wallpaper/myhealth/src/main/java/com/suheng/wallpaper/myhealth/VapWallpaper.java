package com.suheng.wallpaper.myhealth;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.qgame.animplayer.AnimView;

public class VapWallpaper extends WallpaperService {

    private static final String TAG = VapWallpaper.class.getSimpleName();
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = VapWallpaper.this;
        startService(new Intent(this, VapService.class));
    }

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    private final class LiveEngine extends Engine {

        private DisplayManager mDisplayManager;
        private VirtualDisplay mVirtualDisplay;
        private Presentation mPresentation;
        private AnimView mAnimView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "Engine, onCreate: " + this);
            mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "Engine, onDestroy: " + this);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d(TAG, "onSurfaceCreated: " + this);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            int densityDpi = getResources().getDisplayMetrics().densityDpi;
            Log.d(TAG, "onSurfaceChanged: width = " + width + ", height = " + height + ", densityDpi = " + densityDpi + ", " + this);
            if (mVirtualDisplay == null) {
                mVirtualDisplay = mDisplayManager.createVirtualDisplay(TAG, width
                        , height, densityDpi, holder.getSurface(), DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION);
            } else {
                mVirtualDisplay.resize(width, height, densityDpi);
            }

            if (mPresentation == null) {
                mPresentation = new Presentation(mContext, mVirtualDisplay.getDisplay());
                mPresentation.setContentView(R.layout.vap_wallpaper);
                mAnimView = mPresentation.findViewById(R.id.animView);
                //mAnimView.setScaleType(ScaleType.FIT_CENTER);
                mAnimView.setLoop(Integer.MAX_VALUE);
            } else {
                Window window = mPresentation.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams attributes = window.getAttributes();
                    attributes.width = width;
                    attributes.height = height;
                    window.setAttributes(attributes);
                }
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d(TAG, "onSurfaceDestroyed: " + this);
            if (mAnimView.isRunning()) {
                mAnimView.stopPlay();
            }
            if (mPresentation.isShowing()) {
                mPresentation.dismiss();
            }
            mVirtualDisplay.release();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.i(TAG, "onVisibilityChanged, visible: " + visible + ", " + this);
            if (visible) {
                if (!mPresentation.isShowing()) {
                    mPresentation.show();
                    mAnimView.startPlay(mContext.getAssets(), "demo.mp4");
                }
            } else {
                if (mAnimView.isRunning()) {
                    mAnimView.stopPlay();
                }
                if (mPresentation.isShowing()) {
                    mPresentation.dismiss();
                }
            }
        }
    }

}
