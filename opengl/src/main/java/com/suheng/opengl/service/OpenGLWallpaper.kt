package com.suheng.opengl.service

import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.suheng.opengl.countDownFlow
import com.suheng.opengl.isHomeScreen
import com.suheng.opengl.renderer.MySurfaceRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

//https://github.com/arthabus/AndroidViewToGLRendering
class OpenGLWallpaper : WallpaperService() {
    companion object {
        private const val TAG = "OpenGLWallpaper"
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "Wallpaper onCreate: ${System.identityHashCode(this)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Wallpaper onDestroy: ${System.identityHashCode(this)}")
    }

    override fun onCreateEngine(): Engine = OpenGLEngine()

    inner class OpenGLEngine : Engine() {

        private val ctx = this@OpenGLWallpaper
        private lateinit var surfaceRenderer: MySurfaceRenderer

        private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        private val periodScanFlow by lazy {
            countDownFlow()
        }
        private var periodScanJob: Job? = null

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "onCreate: ${System.identityHashCode(this)}")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d(TAG, "onDestroy: ${System.identityHashCode(this)}")
            coroutineScope.cancel()
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
                "onSurfaceChanged, width: $width, height: $height, format: $format, ${
                    System.identityHashCode(this)
                }"
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
                TAG,
                "onVisibilityChanged: $visible, isPreview: $isPreview, ${
                    System.identityHashCode(this)
                }"
            )
            if (visible) {
                surfaceRenderer.onDrawFrame()
                periodScanJob = coroutineScope.launch {
                    periodScanFlow.collect {
                        Log.v(TAG, "period scan: $it, ${isHomeScreen(ctx)}")
                    }
                }
            } else {
                periodScanJob?.cancel()
                periodScanJob = null
            }
        }
    }

}