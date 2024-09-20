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

class VapWallpaper : WallpaperService() {

    private lateinit var context: Context
    private lateinit var renderEngine: RenderEngine
    private val onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, key ->
        if (PrefsUtils.RENDER_WAY_KEY == key) {
            val renderWay = PrefsUtils.loadRenderWay(this)
            Log.d(TAG, "prefs changed renderWay: $renderWay")
            //renderEngine.changeRenderWay(renderWay)
            renderEngine.setRenderWay(renderWay)
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
        return RenderEngine().apply {
            setRenderWay(PrefsUtils.loadRenderWay(this@VapWallpaper))
            renderEngine = this
        }
    }

    private inner class RenderEngine : Engine() {

        private var virtualDisplay: VirtualDisplay? = null
        private var presentation: Presentation? = null
        private var animView: AnimView? = null
        private var isVisible = false
        private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        private var surface: Surface? = null
        private var width: Int = 0
        private var height: Int = 0

        private var vapSurface: VapSurface? = null
        private var renderWay: Int = PrefsUtils.RENDER_WAY_VALUE_DEF

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
            releaseVapSurface()
            releaseVirtualDisplay()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surface = holder.surface
            this.width = width
            this.height = height
            changeRenderWay(renderWay)
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
                    changeRenderWay(renderWay)
                    vapSurface?.startPlay(context.assets, "demo.mp4")
                } else {
                    if (vapSurface?.isRunning()==true) {
                        vapSurface?.stopPlay()
                    }
                }
            } else {
                if (visible) {
                    changeRenderWay(renderWay)
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

        fun setRenderWay(renderWay: Int) {
            this.renderWay = renderWay
        }

        fun changeRenderWay(renderWay: Int) {
            Log.i(TAG, "changeRenderWay: width = $width, height = $height, renderWay: $renderWay")
            if (renderWay == PrefsUtils.RENDER_WAY_VALUE_1) {
                releaseVirtualDisplay()
                initVapSurface()
            } else {
                releaseVapSurface()
                //resizeVirtualDisplay()
                initVirtualDisplay()
            }
        }

        private fun initVapSurface() {
            if (vapSurface != null) {
                return
            }

            vapSurface = VapSurface().apply {
                setLoop(Int.MAX_VALUE)
            }
            if (vapSurface!!.getSurface() == null) {
                vapSurface!!.onSurfaceAvailable(surface!!, width, height)
            } else {
                vapSurface!!.onSurfaceSizeChanged(width, height)
            }
        }

        private fun releaseVapSurface() {
            vapSurface?.let {
                it.onSurfaceDestroyed()
                vapSurface = null
            }
        }

        private fun initVirtualDisplay() {
            if (virtualDisplay != null) {
                return
            }
            if (surface == null || width <= 0 || height <= 0) {
                return
            }

            (context.getSystemService(DISPLAY_SERVICE) as DisplayManager).createVirtualDisplay(
                TAG+System.currentTimeMillis(), width, height, resources.displayMetrics.densityDpi, surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
            ).apply {
                virtualDisplay = this
                presentation = Presentation(context, display).apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.BLUE))
                    setContentView(R.layout.vap_wallpaper)
                    animView = findViewById<AnimView?>(R.id.animView).apply {
                        setScaleType(ScaleType.FIT_CENTER)
                        setLoop(Int.MAX_VALUE)/*getAnimListenerFlow(this).onEach { value ->
                            Log.w(TAG, "onEach: $value")
                        }.launchIn(defaultScope)*/
                        /*setAnimListener(object : IAnimListener {
                            override fun onVideoStart() {
                                Log.d(
                                    TAG,
                                    "onVideoStart, isPreview: $isPreview, isVisible: $isVisible"
                                )
                            }

                            override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
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
                                    TAG, "onFailed, errorType: $errorType, errorMsg: $errorMsg"
                                )
                            }
                        })*/
                    }
                }
            }
        }

        private fun resizeVirtualDisplay() {
            virtualDisplay?.let {
                it.resize(width, height, resources.displayMetrics.densityDpi)
                presentation?.window?.let { wd ->
                    val attributes = wd.attributes
                    attributes.width = width
                    attributes.height = height
                    wd.attributes = attributes
                }
            }
        }

        private fun releaseVirtualDisplay() {
            virtualDisplay?.let {
                animView?.takeIf { av -> av.isRunning() }?.stopPlay()
                presentation?.dismiss()
                it.release()
                virtualDisplay = null
                presentation = null
                animView = null
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

    companion object {
        private val TAG: String = VapWallpaper::class.java.simpleName
    }
}
