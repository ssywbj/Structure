package com.suheng.wallpaper.myhealth

import android.app.Presentation
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.suheng.wallpaper.myhealth.file.FileUtil
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.VapSurface
import com.tencent.qgame.animplayer.util.ScaleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

class SimpleVapWallpaper : WallpaperService() {

    private lateinit var context: Context
    private var renderType = RENDER_OPEN_GL

    override fun onCreate() {
        super.onCreate()
        context = this
        Log.d(TAG, "Wallpaper, onCreate: $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Wallpaper, onDestroy: $this")
    }

    override fun onCreateEngine(): Engine {
        return when (renderType) {
            RENDER_OPEN_GL -> OpenGLEngine()
            else -> VirtualDisplayEngine()
        }
    }

    private inner class VirtualDisplayEngine : Engine() {
        private var virtualDisplay: VirtualDisplay? = null
        private var presentation: Presentation? = null
        private var animView: AnimView? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine, onCreate: $this")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.v(TAG, "Engine, onDestroy: $this")
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.d(TAG, "onSurfaceCreated: $this")
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Log.v(TAG, "onSurfaceDestroyed: $this")
            animView?.takeIf { it.isRunning() }?.stopPlay()
            presentation?.dismiss()
            virtualDisplay?.release()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.i(TAG, "onSurfaceChanged, width: $width, height: $height, $this")
            virtualDisplay?.resize(width, height, resources.displayMetrics.densityDpi)
            presentation?.window?.let {
                val attributes = it.attributes
                attributes.width = width
                attributes.height = height
                it.attributes = attributes
            }
            if (virtualDisplay == null) {
                val displayManager = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
                virtualDisplay = (displayManager).createVirtualDisplay(
                    TAG, width, height, resources.displayMetrics.densityDpi,
                    holder.surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
                ).also {
                    presentation = Presentation(context, it.display).apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.BLUE))
                        setContentView(R.layout.vap_wallpaper)
                        animView = findViewById<AnimView?>(R.id.animView).apply {
                            setScaleType(ScaleType.FIT_CENTER)
                            setLoop(Int.MAX_VALUE)
                        }
                    }
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.i(TAG, "onVisibilityChanged, visible: $visible, isPreview: $isPreview, $this")
            if (visible) {
                presentation?.let {
                    it.show()
                    animView?.startPlay(context.assets, "demo.mp4")
                }
            } else {
                animView?.takeIf { it.isRunning() }?.stopPlay()
                presentation?.dismiss()
            }
        }
    }

    private inner class OpenGLEngine : Engine() {
        private var vapSurface: VapSurface? = null

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine, onCreate, cacheDir: ${context.cacheDir.absolutePath}")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.v(TAG, "Engine, onDestroy: $this")
            coroutineScope.cancel()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            Log.d(TAG, "onSurfaceCreated: $this")
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            Log.v(TAG, "onSurfaceDestroyed: $this")
            super.onSurfaceDestroyed(holder)
            vapSurface?.onSurfaceDestroyed()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int, width: Int, height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.i(TAG, "onSurfaceChanged, width: $width, height: $height, $this")
            vapSurface?.onSurfaceSizeChanged(width, height)
            if (vapSurface == null) {
                vapSurface = holder?.surface?.let {
                    VapSurface().apply {
                        setScaleType(ScaleType.FIT_CENTER)
                        setLoop(Int.MAX_VALUE)
                        onSurfaceAvailable(it, width, height)
                    }
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.i(TAG, "onVisibilityChanged, visible: $visible, isPreview: $isPreview, $this")
            if (visible) {
                if (videoFile.exists()) {
                    vapSurface?.startPlay(videoFile)
                } else {
                    fileToCacheFlow().onEach {
                        it.onSuccess { result ->
                            Log.v(TAG, "saveFileToCache success: $result, thread: ${Thread.currentThread().name}")
                            vapSurface?.startPlay(videoFile)
                        }.onFailure { cause ->
                            Log.e(TAG, "saveFileToCache fail: $cause")
                        }
                    }.launchIn(coroutineScope)
                }
                //vapSurface?.startPlay(context.assets, "demo.mp4")
            } else {
                vapSurface?.takeIf { it.isRunning() }?.stopPlay()
            }
        }

        private val fileName = "demo.mp4"
        private val videoFile = File(context.cacheDir.absolutePath + File.separator + fileName)
        private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        private fun saveFileToCache(): Result<Unit> {
            return kotlin.runCatching {
                FileUtil.streamInputToFile(context.assets.open(fileName), videoFile)
            }
        }

        private fun fileToCacheFlow() = flow {
            emit(saveFileToCache())
        }

    }

    companion object {
        private val TAG = SimpleVapWallpaper::class.java.simpleName
        private const val RENDER_VIRTUAL_DISPLAY = 0
        private const val RENDER_OPEN_GL = 1
    }

}
