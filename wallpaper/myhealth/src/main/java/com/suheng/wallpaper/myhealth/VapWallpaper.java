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
            displayManager = (DisplayManager) VapWallpaper.this.getSystemService(Context.DISPLAY_SERVICE);
            /*virtualDisplay = displayManager.createVirtualDisplay("VirtualDisplayWallpaper", 100
                    , 100, 100, surfaceHolder.getSurface(), DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION);*/
            /*presentation = new Presentation(getBaseContext(), virtualDisplay.getDisplay());
            presentation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            presentation.setContentView(R.layout.myhealth_watchface);
            animView = presentation.findViewById(R.id.animView);
            animView.setLoop(Integer.MAX_VALUE);
            animView.setScaleType(ScaleType.FIT_CENTER);*/
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            int densityDpi = getResources().getDisplayMetrics().densityDpi;
            Log.d("Wbj", "onSurfaceChanged: width = " + width + ", height = " + height
                    + ", densityDpi = " + densityDpi + ", flag = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                    + ", flag2 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
                    + ", flag3 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE
                    + ", flag4 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                    + ", flag5 = " + DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR);
            if (virtualDisplay == null) {
                virtualDisplay = displayManager.createVirtualDisplay("VirtualDisplayWallpaper", width
                        , height, densityDpi, holder.getSurface(), 0);
                presentation = new Presentation(VapWallpaper.this, virtualDisplay.getDisplay());
                presentation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                presentation.setContentView(R.layout.vap_wallpaper);
                animView = presentation.findViewById(R.id.animView);
                animView.setLoop(Integer.MAX_VALUE);
                animView.setScaleType(ScaleType.FIT_CENTER);
            } else {
                //virtualDisplay.release();
                virtualDisplay.setSurface(holder.getSurface());
                virtualDisplay.resize(width, height, densityDpi);
                presentation = new Presentation(VapWallpaper.this, virtualDisplay.getDisplay());

                presentation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                presentation.setContentView(R.layout.vap_wallpaper);
                animView = presentation.findViewById(R.id.animView);
                animView.setLoop(Integer.MAX_VALUE);
                animView.setScaleType(ScaleType.FIT_CENTER);

                /*virtualDisplay.resize(width, height, densityDpi);
                Window window = presentation.getWindow();
                WindowManager.LayoutParams layoutParams = presentation.getWindow().getAttributes();
                layoutParams.width = width;
                layoutParams.height = height;
                layoutParams.format = format;
                window.setAttributes(layoutParams);*/
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d("Wbj", "onSurfaceDestroyed, onSurfaceDestroyed");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Wbj", "onDestroy, onDestroy");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                if (presentation != null) {
                    presentation.show();
                }
                if (animView != null) {
                    if (animView.isRunning()) {
                        return;
                    }
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
