package com.suheng.wallpaper.myhealth

import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.VapSurface
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.system.measureTimeMillis

class VapWallpaper : WallpaperService() {

    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
        startService(Intent(this, VapService::class.java))
    }

    override fun onCreateEngine(): Engine {
        //return VirtualDisplayEngine()
        return SurfaceGLEngine()
    }

    private inner class VirtualDisplayEngine : Engine() {
        private var virtualDisplay: VirtualDisplay? = null
        private var presentation: Presentation? = null
        private var animView: AnimView? = null
        private var isVisible = false
        private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine, onCreate: $this")

        }

        override fun onDestroy() {
            super.onDestroy()
            Log.v(TAG, "Engine, onDestroy: $this")
            defaultScope.cancel()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.d(TAG, "onSurfaceCreated: $this")
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            val densityDpi = resources.displayMetrics.densityDpi
            Log.i(TAG, "onSurfaceChanged: width = $width, height = $height, densityDpi = $densityDpi, $virtualDisplay")
            measureTimeMillis {
                virtualDisplay?.resize(width, height, densityDpi)
                presentation?.window?.let {
                    val attributes = it.attributes
                    attributes.width = width
                    attributes.height = height
                    it.attributes = attributes
                }
            }.also {
                Log.w(TAG, "onSurfaceChanged: resize time = $it")
            }

            if (virtualDisplay == null) {
                val displayManager = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
                virtualDisplay = displayManager.createVirtualDisplay(
                    TAG, width, height, densityDpi,
                    holder.surface, 0
                ).also {
                    presentation = Presentation(context, it.display).apply {
                        setContentView(R.layout.vap_wallpaper)
                        animView = findViewById<AnimView?>(R.id.animView).apply {
                            setScaleType(ScaleType.FIT_CENTER)
                            setLoop(Int.MAX_VALUE)
                            getAnimListenerFlow(this).onEach { value ->
                                Log.w(TAG, "onEach: $value")
                            }.launchIn(defaultScope)
                            /*setAnimListener(object : IAnimListener {
                                override fun onVideoStart() {
                                }

                                override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
                                }

                                override fun onVideoComplete() {
                                }

                                override fun onVideoDestroy() {
                                    Log.e(TAG, "onVideoDestroy, isPreview: $isPreview, isVisible: $isVisible")
                                    if (!isPreview && isVisible) {
                                        animView?.startPlay(context.assets, "demo.mp4")
                                    }
                                }

                                override fun onFailed(errorType: Int, errorMsg: String?) {
                                }
                            })*/

                        }
                    }
                }
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Log.v(TAG, "onSurfaceDestroyed: $this")
            animView?.takeIf { it.isRunning() }?.stopPlay()
            presentation?.dismiss()
            virtualDisplay?.release()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.i(TAG, "visible: $visible, isRunning: ${animView?.isRunning()}, isPreview: $isPreview")
            isVisible = visible
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

    private fun getAnimListenerFlow(animView: AnimView) = callbackFlow<Any> {
        val listener = object : IAnimListener {
            override fun onVideoStart() {
                trySendBlocking(0)
            }

            override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
                //trySendBlocking(frameIndex to config)
            }

            override fun onVideoComplete() {
                trySendBlocking(1)
            }

            override fun onVideoDestroy() {
                trySendBlocking(2)
            }

            override fun onFailed(errorType: Int, errorMsg: String?) {
                trySendBlocking(errorType to errorMsg)
            }
        }
        animView.setAnimListener(listener)
        awaitClose {
            animView.setAnimListener(null)
        }
    }

    private inner class SurfaceGLEngine : Engine() {
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
            Log.i(
                TAG,
                "visible: $visible, isRunning: ${vapSurface.isRunning()}, isPreview: $isPreview"
            )
            if (visible) {
                vapSurface.startPlay(context.assets, "demo.mp4")
            } else {
                if (vapSurface.isRunning()) {
                    vapSurface.stopPlay()
                }
            }
        }
    }

    companion object {
        private val TAG: String = VapWallpaper::class.java.simpleName
    }
}
