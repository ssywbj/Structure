package com.suheng.wallpaper.myhealth;

import android.app.Presentation
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.Surface
import android.view.View
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.util.ScaleType

class VapService : Service() {

    companion object {
        const val TAG = "VapTextureView"
        const val EXTRA_SURFACE_AVAILABLE = "onSurfaceTextureAvailable"
        const val EXTRA_SURFACE_CHANGED = "onSurfaceTextureSizeChanged"
        const val EXTRA_SURFACE_DESTROYED = "onSurfaceTextureDestroyed"
        const val EXTRA_VAP_WIDTH = "vap_width"
        const val EXTRA_VAP_HEIGHT = "vap_height"

        const val ON_VISIBILITY_AGGREGATED = "onVisibilityAggregated"
        const val ON_SCREEN_STATE_CHANGED = "onScreenStateChanged"

        const val EXTRA_ON_CLICK = "onClick"
        const val EVENT_ON_CLICK = "com.suheng.wallpaper.event.ON_CLICK"
        const val EXTRA_ON_LONG_CLICK = "onLongClick"
        const val EVENT_ON_LONG_CLICK = "com.suheng.wallpaper.event.ON_LONG_CLICK"

        const val EXTRA_PROTOCOL = "protocol"
    }

    private lateinit var displayManager: DisplayManager
    private var virtualDisplay: VirtualDisplay? = null
    private var presentation: Presentation? = null

    private var animView: AnimView? = null
    private var isVisible: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "VapService, onCreate")
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: intent = $intent, flags = $flags, startId = $startId")
        intent?.extras?.let {
            (it.getParcelable<Parcelable>(EXTRA_SURFACE_AVAILABLE) as? Surface)?.run {
                val width = it.getInt(EXTRA_VAP_WIDTH, 0)
                val height = it.getInt(EXTRA_VAP_HEIGHT, 0)
                Log.d(TAG, "surface create: $this, width = $width, height = $height")
                if (width > 0 && height > 0) {
                    onSurfaceTextureAvailable(this, width, height)
                }
            }

            (it.getParcelable<Parcelable>(EXTRA_SURFACE_CHANGED) as? Surface)?.run {
                val width = it.getInt(EXTRA_VAP_WIDTH, 0)
                val height = it.getInt(EXTRA_VAP_HEIGHT, 0)
                Log.d(TAG, "surface change: $this, width = $width, height = $height")
                if (width > 0 && height > 0) {
                    onSurfaceTextureSizeChanged(this, width, height)
                }
            }

            (intent.hasExtra(EXTRA_SURFACE_DESTROYED)).takeIf { bool -> bool }?.run {
                val flagDestroyed = it.getBoolean(EXTRA_SURFACE_DESTROYED, false)
                Log.d(TAG, "surface destroy = $flagDestroyed")
                onSurfaceTextureDestroyed(null)
            }

            (intent.hasExtra(ON_VISIBILITY_AGGREGATED)).takeIf { bool -> bool }?.run {
                val isVisible = it.getBoolean(ON_VISIBILITY_AGGREGATED, false)
                Log.d(TAG, "visibility = $isVisible")
                onVisibilityAggregated(isVisible)
            }

            (intent.hasExtra(ON_SCREEN_STATE_CHANGED)).takeIf { bool -> bool }?.run {
                val screenState = it.getInt(ON_SCREEN_STATE_CHANGED, View.SCREEN_STATE_OFF)
                Log.d(TAG, "screenState = $screenState")
                onScreenStateChanged(screenState)
            }

            if (it.getString(EXTRA_ON_CLICK) == EVENT_ON_CLICK) {
                onClick()
            }
            if (it.getString(EXTRA_ON_LONG_CLICK) == EVENT_ON_LONG_CLICK) {
                onLongClick()
            }

            (it.getString(EXTRA_PROTOCOL))?.run {
                protocol(this)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun onSurfaceTextureAvailable(surface: Surface, width: Int, height: Int) {
        virtualDisplay = displayManager.createVirtualDisplay(
            TAG, width, height,
            resources.displayMetrics.densityDpi, surface, 0
        )
        virtualDisplay?.let {
            presentation = Presentation(this, it.display).apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(R.layout.vap_wallpaper)

                this@VapService.animView = findViewById<AnimView>(R.id.animView).apply {
                    setLoop(Int.MAX_VALUE)
                    setScaleType(ScaleType.FIT_CENTER)
                }
            }
        }
        animView.takeIf { isVisible }?.let {
            this.onVisibilityAggregated(true)
        }
    }

    private fun onSurfaceTextureSizeChanged(surface: Surface, width: Int, height: Int) {
    }

    private fun onSurfaceTextureDestroyed(surface: Surface?) {
        this.onVisibilityAggregated(false)
        virtualDisplay?.release()
        virtualDisplay = null
    }

    private fun onVisibilityAggregated(isVisible: Boolean) {
        this.isVisible = isVisible

        virtualDisplay?.let {
            if (isVisible) {
                presentation?.show()
                animView?.startPlay(assets, "demo.mp4")
            } else {
                animView?.takeIf { it.isRunning() }?.stopPlay()
                presentation?.takeIf { it.isShowing }?.dismiss()
            }
        }
    }

    private fun onScreenStateChanged(screenState: Int) {
    }

    private fun onClick() {
        Log.d(TAG, "onClick")
    }

    private fun onLongClick() {
        Log.d(TAG, "onLongClick")
    }

    private fun protocol(protocol: String) {
        Log.d(TAG, "protocol: $protocol")
    }

}
