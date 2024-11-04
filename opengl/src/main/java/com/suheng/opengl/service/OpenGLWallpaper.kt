package com.suheng.opengl.service

import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.suheng.opengl.identityHashCode
import com.suheng.opengl.renderer.MySurfaceRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

//https://github.com/arthabus/AndroidViewToGLRendering
class OpenGLWallpaper : WallpaperService() {
    companion object {
        private const val TAG = "OpenGLWallpaper"
    }

    private val wallpaperLifeScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "Wallpaper onCreate: ${identityHashCode()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Wallpaper onDestroy: ${identityHashCode()}")
        wallpaperLifeScope.cancel()
    }

    override fun onCreateEngine(): Engine = OpenGLEngine()

    inner class OpenGLEngine : Engine() {

        private val ctx = this@OpenGLWallpaper
        private lateinit var surfaceRenderer: MySurfaceRenderer

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "onCreate: ${identityHashCode()}")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d(TAG, "onDestroy: ${identityHashCode()}")
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            surfaceRenderer = MySurfaceRenderer(holder.surface)
            surfaceRenderer.onSurfaceCreated()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.d(
                TAG,
                "onSurfaceChanged, width: $width, height: $height, format: $format, ${identityHashCode()}"
            )
            surfaceRenderer.onSurfaceChanged(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            surfaceRenderer.onSurfaceDestroyed()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.d(
                TAG, "onVisibilityChanged: $visible, isPreview: $isPreview, ${identityHashCode()}"
            )
            if (visible) {
                surfaceRenderer.onDrawFrame()
            }
        }
    }

}