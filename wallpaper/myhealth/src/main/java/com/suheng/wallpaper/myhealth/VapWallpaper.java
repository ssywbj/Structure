package com.suheng.wallpaper.myhealth;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.util.ScaleType;

public class VapWallpaper extends WallpaperService {

    private static final String TAG = VapWallpaper.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, VapService.class));
    }

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    private final class LiveEngine extends Engine {

        private DisplayManager displayManager;
        private VirtualDisplay virtualDisplay;
        private Presentation presentation;
        private AnimView animView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "Engine, onCreate: " + this);
            displayManager = (DisplayManager) VapWallpaper.this.getSystemService(Context.DISPLAY_SERVICE);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            int densityDpi = getResources().getDisplayMetrics().densityDpi;
            Log.d(TAG, "onSurfaceChanged: width = " + width + ", height = " + height
                    + ", densityDpi = " + densityDpi + ", flag = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                    + ", flag2 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
                    + ", flag3 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE
                    + ", flag4 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                    + ", flag5 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR + ", " + this);
            if (virtualDisplay == null) {
                virtualDisplay = displayManager.createVirtualDisplay(TAG, width
                        , height, densityDpi, holder.getSurface(), 0);
            } else {
                virtualDisplay.setSurface(holder.getSurface());
                virtualDisplay.resize(width, height, densityDpi);
            }
            presentation = new Presentation(VapWallpaper.this, virtualDisplay.getDisplay());
            presentation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            presentation.setContentView(R.layout.vap_wallpaper);
            animView = presentation.findViewById(R.id.animView);
            animView.setLoop(Integer.MAX_VALUE);
            animView.setScaleType(ScaleType.FIT_CENTER);
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
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "onVisibilityChanged, visible = " + visible + ", " + this);
            if (virtualDisplay == null) {
                return;
            }
            if (visible) {
                if (presentation != null) {
                    presentation.show();
                }
                if (animView != null) {
                    animView.startPlay(VapWallpaper.this.getAssets(), "demo.mp4");
                }
            } else {
                if (animView != null) {
                    if (animView.isRunning()) {
                        animView.stopPlay();
                    }
                }
                if (presentation != null) {
                    presentation.dismiss();
                }
            }
        }

    }
}
