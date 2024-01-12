package com.suheng.wallpaper.myhealth;

import android.app.KeyguardManager;
import android.app.Presentation;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.inter.IAnimListener;
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

        private DisplayManager displayManager;
        private VirtualDisplay virtualDisplay;
        private Presentation presentation;
        private AnimView animView;
        private KeyguardManager mKeyguardManager;
        private TextureView mTextureView;
        private ImageView mImageView;

        private WallpaperManager mWallpaperManager;

        private final IAnimListener mAnimListener = new IAnimListener() {
            @Override
            public boolean onVideoConfigReady(@NonNull AnimConfig animConfig) {
                return true;
            }

            @Override
            public void onVideoStart() {
                if (mTextureView == null) {
                    View view = animView.getChildAt(0);
                    if (view instanceof TextureView) {
                        mTextureView = (TextureView) view;
                    }
                    Log.w(TAG, "mTextureView: " + mTextureView + ", thread: " + Thread.currentThread().getName());
                }
                //mImageView.setAlpha(0);
                mImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        //mImageView.setImageAlpha(0);
                        mImageView.setAlpha(0f);
                        //mImageView.setBackgroundColor(Color.RED);
                    }
                });
            }

            @Override
            public void onVideoRender(int frameIndex, @Nullable AnimConfig animConfig) {
                final boolean keyguardLocked = mKeyguardManager.isKeyguardLocked();
                final boolean isPreview = isPreview();
                String msg = "onVideoRender, frameIndex: " + frameIndex + ", isKeyguardLocked: " + keyguardLocked
                        + ", isPreview: " + isPreview;
                if (mIsVisible) {
                    if (keyguardLocked) {
                        //Log.w(TAG, msg);
                    } else {
                        if (isPreview || animConfig == null) {
                            return;
                        }

                        Log.v(TAG, msg);
                        if (frameIndex == animConfig.getTotalFrames()) {
                            if (mTextureView != null) {
                                final Bitmap bitmap = mTextureView.getBitmap();
                                Log.e(TAG, "bitmap:" + bitmap + ", thread:" + Thread.currentThread().getName());
                                mImageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //animView.stopPlay();
                                        //mImageView.setBackground(new BitmapDrawable(getResources(), bitmap));
                                        //mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()));
                                        //mImageView.setImageAlpha(255);
                                        mImageView.setAlpha(1.0f);
                                        Log.w(TAG, "bitmap:" + bitmap + ", thread:" + Thread.currentThread().getName());
                                    }
                                });
                            }

                            //animView.stopPlay();
                            //animView.setAlpha(0f);
                        }
                    }
                } else {
                    if (animView.isRunning()) {
                        animView.stopPlay();
                    }
                }
            }

            @Override
            public void onVideoComplete() {
                if (mTextureView != null) {
                    //Bitmap bitmap = mTextureView.getBitmap();
                    //Log.w(TAG, "onVideoComplete, bitmap: " + bitmap);
                }
            }

            @Override
            public void onVideoDestroy() {
                if (mTextureView != null) {
                    //Bitmap bitmap = mTextureView.getBitmap();
                    //Log.w(TAG, "onVideoDestroy, bitmap: " + bitmap);
                }
            }

            @Override
            public void onFailed(int i, @Nullable String s) {
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "Engine, onCreate: " + this);
            displayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            mWallpaperManager = WallpaperManager.getInstance(mContext);
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
            if (presentation == null) {
                presentation = new Presentation(VapWallpaper.this, virtualDisplay.getDisplay());
                Window window = presentation.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                presentation.setContentView(R.layout.vap_wallpaper);
                animView = presentation.findViewById(R.id.animView);
                animView.setScaleType(ScaleType.FIT_CENTER);
                mImageView = presentation.findViewById(R.id.imageView);
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
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
        }

        private boolean mIsVisible = false;

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mIsVisible = visible;
            final int lockWallpaperId = mWallpaperManager.getWallpaperId(WallpaperManager.FLAG_LOCK);
            final boolean keyguardLocked = mKeyguardManager.isKeyguardLocked();
            Log.i(TAG, "onVisibilityChanged, visible: " + visible
                    + ", isKeyguardLocked: " + keyguardLocked
                    + ", lockWallpaperId: " + lockWallpaperId
                    + ", isKeyguardSecure: " + mKeyguardManager.isKeyguardSecure()
                    + ", inKeyguardRestrictedInputMode: " + mKeyguardManager.inKeyguardRestrictedInputMode()
            );
            if (virtualDisplay == null) {
                return;
            }
            if (visible) {
                if (presentation != null) {
                    presentation.show();
                }
                if (animView != null) {
                    animView.setAlpha(1f);
                    if (keyguardLocked) {
                        animView.setLoop(Integer.MAX_VALUE);
                    } else {
                        if (isPreview()) {
                            animView.setLoop(Integer.MAX_VALUE);
                        } else {
                            animView.setLoop(1);
                        }
                    }
                    animView.setAnimListener(new IAnimListener() {
                        @Override
                        public boolean onVideoConfigReady(@NonNull AnimConfig animConfig) {
                            return true;
                        }

                        @Override
                        public void onVideoStart() {
                            if (mTextureView == null) {
                                View view = animView.getChildAt(0);
                                if (view instanceof TextureView) {
                                    mTextureView = (TextureView) view;
                                }
                                Log.w(TAG, "mTextureView: " + mTextureView + ", thread: " + Thread.currentThread().getName());
                            }
                            //mImageView.setAlpha(0);
                            mImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    //mImageView.setImageAlpha(0);
                                    mImageView.setAlpha(0f);
                                    //mImageView.setBackgroundColor(Color.RED);
                                }
                            });
                        }

                        @Override
                        public void onVideoRender(int frameIndex, @Nullable AnimConfig animConfig) {
                            final boolean keyguardLocked = mKeyguardManager.isKeyguardLocked();
                            final boolean isPreview = isPreview();
                            String msg = "onVideoRender, frameIndex: " + frameIndex + ", isKeyguardLocked: " + keyguardLocked
                                    + ", isPreview: " + isPreview;
                            if (mIsVisible) {
                                if (keyguardLocked) {
                                    //Log.w(TAG, msg);
                                } else {
                                    if (isPreview || animConfig == null) {
                                        return;
                                    }

                                    //Log.v(TAG, msg);
                                    if (frameIndex == animConfig.getTotalFrames()) {
                                        if (mTextureView != null) {
                                            final Bitmap bitmap = mTextureView.getBitmap();
                                            Log.e(TAG, "bitmap:" + bitmap + ", thread:" + Thread.currentThread().getName());
                                            //if (bitmap != null) {
                                                mImageView.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //animView.stopPlay();
                                                        //mImageView.setBackground(new BitmapDrawable(getResources(), bitmap));
                                                        if (bitmap != null) {
                                                            //mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()));
                                                            mImageView.setImageBitmap(bitmap);
                                                        }
                                                        //mImageView.setImageAlpha(255);
                                                        //mImageView.setImageResource(R.drawable.shot_20240112_155330);
                                                        mImageView.setAlpha(1.0f);
                                                        animView.setAlpha(0f);
                                                        Log.w(TAG, "bitmap:" + bitmap + ", thread:" + Thread.currentThread().getName());
                                                    }
                                                });
                                            //}
                                        }

                                        animView.stopPlay();
                                        //animView.setAlpha(0f);
                                    }
                                }
                            } else {
                                if (animView.isRunning()) {
                                    animView.stopPlay();
                                }
                            }
                        }

                        @Override
                        public void onVideoComplete() {
                            if (mTextureView != null) {
                                //Bitmap bitmap = mTextureView.getBitmap();
                                //Log.w(TAG, "onVideoComplete, bitmap: " + bitmap);
                            }
                        }

                        @Override
                        public void onVideoDestroy() {
                            if (mTextureView != null) {
                                //Bitmap bitmap = mTextureView.getBitmap();
                                //Log.w(TAG, "onVideoDestroy, bitmap: " + bitmap);
                            }
                        }

                        @Override
                        public void onFailed(int i, @Nullable String s) {
                        }
                    });
                    animView.startPlay(VapWallpaper.this.getAssets(), "demo.mp4");

                    mImageView.setAlpha(0f);
                    /*animView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animView.startPlay(VapWallpaper.this.getAssets(), "demo.mp4");
                        }
                    },200);*/
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
