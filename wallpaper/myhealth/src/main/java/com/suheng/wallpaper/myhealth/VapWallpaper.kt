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
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.VapSurface
import com.tencent.qgame.animplayer.util.ScaleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

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
                    //changeRenderWay(renderWay)
                    changeRenderWay(PrefsUtils.loadRenderWay(context))
                    vapSurface?.startPlay(context.assets, "demo.mp4")
                } else {
                    if (vapSurface?.isRunning() == true) {
                        vapSurface?.stopPlay()
                    }
                }
            } else {
                if (visible) {
                    //changeRenderWay(renderWay)
                    changeRenderWay(PrefsUtils.loadRenderWay(context))
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
                resizeVirtualDisplay()
                initVirtualDisplay()
            }
        }

        private fun initVapSurface() {
            if (vapSurface != null) {
                return
            }

            surface.takeIf { width > 0 && height > 0 }?.let {
                vapSurface = VapSurface().apply {
                    setLoop(Int.MAX_VALUE)
                    onSurfaceAvailable(it, width, height)
                }
                Log.i(TAG, "initVapSurface vapSurface: $vapSurface")
                resizeVapSurface()
            }
        }

        private fun resizeVapSurface() {
            vapSurface?.onSurfaceSizeChanged(width, height)
        }

        private fun releaseVapSurface() {
            vapSurface?.let {
                Log.i(TAG, "releaseVapSurface")
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

            virtualDisplay = (context.getSystemService(DISPLAY_SERVICE) as DisplayManager).createVirtualDisplay(
                TAG + System.currentTimeMillis(), width, height, resources.displayMetrics.densityDpi, surface,
                0
            ).apply {
                presentation = Presentation(context, display).apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.BLUE))
                    setContentView(R.layout.vap_wallpaper)
                    animView = findViewById<AnimView?>(R.id.animView).apply {
                        setScaleType(ScaleType.FIT_CENTER)
                        setLoop(Int.MAX_VALUE)
                    }
                }
            }
            Log.i(TAG, "initVirtualDisplay virtualDisplay: $virtualDisplay")
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
                Log.i(TAG, "releaseVirtualDisplay")
                animView?.takeIf { av -> av.isRunning() }?.stopPlay()
                presentation?.dismiss()
                it.release()
                virtualDisplay = null
                presentation = null
                animView = null
            }
        }

    }

    companion object {
        private val TAG: String = VapWallpaper::class.java.simpleName
    }
}
