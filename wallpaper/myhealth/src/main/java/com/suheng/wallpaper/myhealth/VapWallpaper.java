package com.suheng.wallpaper.myhealth;

import android.app.KeyguardManager;
import android.app.Presentation;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;

import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.util.ScaleType;

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
        private KeyguardManager mKeyguardManager;
        private WallpaperManager mWallpaperManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "Engine, onCreate: " + this);
            mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            mWallpaperManager = WallpaperManager.getInstance(mContext);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            int densityDpi = getResources().getDisplayMetrics().densityDpi;
            Log.d(TAG, "onSurfaceChanged: width = " + width + ", height = " + height
                    + ", format = " + format + ", densityDpi = " + densityDpi
                    + ", flag = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                    + ", flag2 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
                    + ", flag3 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE
                    + ", flag4 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                    + ", flag5 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR + ", " + this);
            if (mVirtualDisplay == null) {
                mVirtualDisplay = mDisplayManager.createVirtualDisplay(TAG, width
                        , height, densityDpi, holder.getSurface(), DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION);
            } else {
                mVirtualDisplay.resize(width, height, densityDpi);
            }
            if (mPresentation == null) {
                mPresentation = new Presentation(mContext, mVirtualDisplay.getDisplay());
                Window window = mPresentation.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                }
                mPresentation.setContentView(R.layout.vap_wallpaper);
                mAnimView = mPresentation.findViewById(R.id.animView);
                mAnimView.setScaleType(ScaleType.FIT_CENTER);
                mAnimView.setLoop(Integer.MAX_VALUE);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d(TAG, "Engine, onSurfaceDestroyed: " + this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "Engine, onDestroy: " + this);
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
            Log.i(TAG, "onVisibilityChanged, visible: " + visible
                    + ", lockWallpaperId: " + mWallpaperManager.getWallpaperId(WallpaperManager.FLAG_LOCK)
                    + ", lockWallpaper: " + mWallpaperManager.getBuiltInDrawable(WallpaperManager.FLAG_LOCK)
                    + ", isKeyguardLocked: " + mKeyguardManager.isKeyguardLocked()
                    + ", isKeyguardSecure: " + mKeyguardManager.isKeyguardSecure()
                    + ", inKeyguardRestrictedInputMode: " + mKeyguardManager.inKeyguardRestrictedInputMode()
            );
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
