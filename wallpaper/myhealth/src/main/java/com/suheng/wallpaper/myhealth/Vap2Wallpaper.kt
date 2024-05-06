package com.suheng.wallpaper.myhealth

import android.content.Context
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.tencent.qgame.animplayer.VapSurface

class Vap2Wallpaper : WallpaperService() {

    companion object {
        private const val TAG = "Vap2Wallpaper"
    }

    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this@Vap2Wallpaper
    }

    override fun onCreateEngine(): Engine = VapEngine()

    inner class VapEngine : Engine() {
        private var vapSurface: VapSurface = VapSurface().apply {
            //setScaleType(ScaleType.FIT_CENTER)
            setLoop(Int.MAX_VALUE)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine, onCreate: $this")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d(TAG, "Engine, onDestroy: $this")
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            Log.d(TAG, "onSurfaceCreated: $this")
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int, width: Int, height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.d(TAG, "onSurfaceChanged: width = $width, height = $height , $this")
            holder?.let {
                if (vapSurface.getSurface() == null) {
                    vapSurface.onSurfaceAvailable(it.surface, width, height)
                } else {
                    vapSurface.onSurfaceSizeChanged(width, height)
                }
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            Log.d(TAG, "onSurfaceDestroyed: $this")
            super.onSurfaceDestroyed(holder)
            vapSurface.onSurfaceDestroyed()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.i(TAG, "onVisibilityChanged, visible: $visible, $this")
            if (visible) {
                vapSurface.startPlay(context.assets, "demo.mp4")
            } else {
                if (vapSurface.isRunning()) {
                    vapSurface.stopPlay()
                }
            }
        }
    }

}