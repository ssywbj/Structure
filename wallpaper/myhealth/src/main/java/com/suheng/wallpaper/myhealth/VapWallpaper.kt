package com.suheng.wallpaper.myhealth

import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import com.suheng.wallpaper.myhealth.file.PrefsUtils
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
import kotlin.system.measureTimeMillis

class VapWallpaper : WallpaperService() {

    private lateinit var context: Context
    private lateinit var renderEngine: RenderEngine
    private val onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, key ->
        if (PrefsUtils.RENDER_WAY_KEY == key) {
            val renderWay = PrefsUtils.loadRenderWay(this)
            Log.d(TAG, "prefs changed renderWay: $renderWay")
            renderEngine.changeRenderWay(renderWay)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Wallpaper, onCreate: $this")
        context = this
        startService(Intent(this, VapService::class.java))
        PrefsUtils.sharedPrefs(this)
            .registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Wallpaper, onDestroy: $this")
        PrefsUtils.sharedPrefs(this)
            .unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onCreateEngine(): Engine {
        return RenderEngine(PrefsUtils.loadRenderWay(this)).apply {
            renderEngine = this
        }
    }

    private inner class RenderEngine(var renderWay: Int = PrefsUtils.RENDER_WAY_VALUE_DEF) :
        Engine() {

        private var virtualDisplay: VirtualDisplay? = null
        private var presentation: Presentation? = null
        private var animView: AnimView? = null
        private var isVisible = false
        private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        private var surface: Surface? = null
        private var format: Int = 0
        private var width: Int = 0
        private var height: Int = 0
        private var densityDpi: Int = 0

        private var vapSurface: VapSurface? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "Engine, onCreate: $this")
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d(TAG, "Engine, onDestroy: $this")
            defaultScope.cancel()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.v(TAG, "onSurfaceCreated: $this")
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Log.v(TAG, "onSurfaceDestroyed: $this")
            if (renderWay == PrefsUtils.RENDER_WAY_VALUE_1) {
                vapSurface?.onSurfaceDestroyed()
            } else {
                animView?.takeIf { it.isRunning() }?.stopPlay()
                presentation?.dismiss()
                virtualDisplay?.release()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surface = holder.surface
            this.width = width
            this.height = height
            this.format = format
            densityDpi = resources.displayMetrics.densityDpi

            Log.i(
                TAG,
                "onSurfaceChanged: width = $width, height = $height, densityDpi = $densityDpi, $virtualDisplay"
            )
            if (renderWay == PrefsUtils.RENDER_WAY_VALUE_1) {
                vapSurface = VapSurface().apply {
                    setLoop(Int.MAX_VALUE)
                }
                if (vapSurface!!.getSurface() == null) {
                    vapSurface!!.onSurfaceAvailable(surface!!, width, height)
                } else {
                    vapSurface!!.onSurfaceSizeChanged(width, height)
                }
            } else {
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
                        surface, 0
                    ).also {
                        presentation = Presentation(context, it.display).apply {
                            window?.setBackgroundDrawable(ColorDrawable(Color.BLUE))
                            setContentView(R.layout.vap_wallpaper)
                            animView = findViewById<AnimView?>(R.id.animView).apply {
                                setScaleType(ScaleType.FIT_CENTER)
                                setLoop(Int.MAX_VALUE)
                                /*getAnimListenerFlow(this).onEach { value ->
                                    Log.w(TAG, "onEach: $value")
                                }.launchIn(defaultScope)*/
                                setAnimListener(object : IAnimListener {
                                    override fun onVideoStart() {
                                        Log.d(
                                            TAG,
                                            "onVideoStart, isPreview: $isPreview, isVisible: $isVisible"
                                        )
                                    }

                                    override fun onVideoRender(
                                        frameIndex: Int,
                                        config: AnimConfig?,
                                    ) {
                                        //Log.d(TAG, "onVideoRender, frameIndex: $frameIndex, config: $config")
                                    }

                                    override fun onVideoComplete() {
                                    }

                                    override fun onVideoDestroy() {
                                        Log.i(
                                            TAG,
                                            "onVideoDestroy, isPreview: $isPreview, isVisible: $isVisible"
                                        )
                                    }

                                    override fun onFailed(errorType: Int, errorMsg: String?) {
                                        Log.w(
                                            TAG,
                                            "onFailed, errorType: $errorType, errorMsg: $errorMsg"
                                        )
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.i(
                TAG,
                "onVisibilityChanged, visible: $visible, isRunning: ${animView?.isRunning()}, isPreview: $isPreview"
            )
            isVisible = visible
            if (renderWay == PrefsUtils.RENDER_WAY_VALUE_1) {
                if (visible) {
                    vapSurface?.startPlay(context.assets, "demo.mp4")
                } else {
                    if (vapSurface?.isRunning()==true) {
                        vapSurface?.stopPlay()
                    }
                }
            } else {
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

        fun changeRenderWay(renderWay: Int) {
            if (this.renderWay == renderWay) return
            this.renderWay = renderWay
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

    companion object {
        private val TAG: String = VapWallpaper::class.java.simpleName
    }
}
