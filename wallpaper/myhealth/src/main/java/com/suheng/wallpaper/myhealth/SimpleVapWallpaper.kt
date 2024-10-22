package com.suheng.wallpaper.myhealth

import android.app.Presentation
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.suheng.wallpaper.myhealth.file.identityHashCode
import com.suheng.wallpaper.myhealth.repository.VideoRepository
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.VapSurface
import com.tencent.qgame.animplayer.util.ScaleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import java.io.File
import kotlin.properties.Delegates

class SimpleVapWallpaper : WallpaperService() {

    companion object {
        private val TAG = SimpleVapWallpaper::class.java.simpleName
        private const val RENDER_VIRTUAL_DISPLAY = 0
        private const val RENDER_OPEN_GL = 1
        private var context: SimpleVapWallpaper by Delegates.notNull()
        fun getInstance(): SimpleVapWallpaper = context
    }

    private var renderType = RENDER_OPEN_GL
    val wallpaperScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        context = this
        Log.d(TAG, "Wallpaper, onCreate: $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Wallpaper, onDestroy: $this")
        wallpaperScope.cancel()
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

    //https://github.com/bytedance/AlphaPlayer.git
    //https://github.com/yangdong123/MediaGiftPlayer.git
    //https://github.com/androidx/media.git
    //https://github.com/androidx/androidx
    private inner class OpenGLEngine : Engine() {
        private var vapSurface: VapSurface? = null
        private var isVisible = false
        private var file: File? = null
        private val job: Job = wallpaperScope.launch(start = CoroutineStart.LAZY) {
            VideoRepository.getVideoFile().onEmpty { Log.w(TAG, "loadVideoFile fail: flow empty") }
                .collect { fl ->
                    Log.v(TAG, "loadVideoFile success: $fl, isVisible: $isVisible")
                    file = fl
                    if (isVisible) {
                        vapSurface?.takeUnless { it.isRunning() }?.startPlay(fl)
                    }
                }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine onCreate: ${identityHashCode()}")
            job.start()
        }

        override fun onDestroy() {
            super.onDestroy()
            job.cancel()
            Log.v(TAG, "Engine onDestroy: ${identityHashCode()}")
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            Log.d(TAG, "onSurfaceCreated: ${identityHashCode()}")
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            Log.v(TAG, "onSurfaceDestroyed: ${identityHashCode()}")
            vapSurface?.onSurfaceDestroyed()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int, width: Int, height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.i(TAG, "onSurfaceChanged width: $width, height: $height, ${identityHashCode()}")
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
            isVisible = visible
            val isCompleted = job.isCompleted
            Log.i(
                TAG,
                "visible: $visible, isPreview: $isPreview, isCompleted: $isCompleted, file: $file, ${identityHashCode()}"
            )
            if (visible) {
                file?.takeIf { isCompleted }?.let {
                    vapSurface?.startPlay(it)
                }
            } else {
                vapSurface?.takeIf { it.isRunning() }?.stopPlay()
            }
        }
    }

}
