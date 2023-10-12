package com.suheng.wallpaper.myhealth;

import android.app.Presentation
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
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
    }

    private var surface: Surface? = null
    private var animView: AnimView? = null
    private var presentation: Presentation? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "VapService, onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Log.d(TAG, "onStartCommand: flags = $flags, startId = $startId, intent = $intent")
        intent?.takeIf { it.getParcelableExtra<Parcelable>(EXTRA_SURFACE_AVAILABLE) is Surface }
            ?.let {
                surface = it.getParcelableExtra(EXTRA_SURFACE_AVAILABLE)
                //handler.post(runnable)

                val width = it.getIntExtra(EXTRA_VAP_WIDTH, 0)
                val height = it.getIntExtra(EXTRA_VAP_HEIGHT, 0)
                Log.d(
                    TAG,
                    "onStartCommand, onSurfaceTextureAvailable: width = $width, height = $height, surface = $surface"
                )
                surface.takeIf { width > 0 && height > 0 }?.let { sf ->
                    this.onSurfaceTextureAvailable(sf, width, height)
                }

            }

        intent?.takeIf { it.getParcelableExtra<Parcelable>(EXTRA_SURFACE_CHANGED) is Surface }
            ?.let {
                val surface = it.getParcelableExtra<Surface>(EXTRA_SURFACE_CHANGED)
                val width = it.getIntExtra(EXTRA_VAP_WIDTH, 0)
                val height = it.getIntExtra(EXTRA_VAP_HEIGHT, 0)
                Log.d(
                    TAG,
                    "onStartCommand, onSurfaceTextureSizeChanged: width = $width, height = $height, surface = $surface"
                )
                surface.takeIf { width > 0 && height > 0 }?.let { sf ->
                    this.onSurfaceTextureSizeChanged(sf, width, height)
                }
            }

        intent?.takeIf { it.hasExtra(EXTRA_SURFACE_DESTROYED) }?.let {
            val flagDestroyed = it.getBooleanExtra(EXTRA_SURFACE_DESTROYED, false)
            Log.d(TAG, "onStartCommand: onSurfaceTextureDestroyed = $flagDestroyed")
            this.onSurfaceTextureDestroyed(null)
        }

        intent?.takeIf { it.hasExtra(ON_VISIBILITY_AGGREGATED) }?.let {
            val isVisible = it.getBooleanExtra(ON_VISIBILITY_AGGREGATED, false)
            Log.d(TAG, "onStartCommand: onVisibilityAggregated = $isVisible")
            this.onVisibilityAggregated(isVisible)
        }

        intent?.takeIf { it.hasExtra(ON_SCREEN_STATE_CHANGED) }?.let {
            val screenState = it.getIntExtra(ON_SCREEN_STATE_CHANGED, View.SCREEN_STATE_OFF)
            Log.d(TAG, "onStartCommand: screenState = $screenState")
            this.onScreenStateChanged(screenState)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun onSurfaceTextureAvailable(surface: Surface, width: Int, height: Int) {
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        val virtualDisplay =
            displayManager.createVirtualDisplay(
                TAG,
                width,
                height,
                resources.displayMetrics.densityDpi,
                surface,
                0
            )
        presentation = Presentation(this, virtualDisplay.display).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.vap_wallpaper)

            animView = findViewById<AnimView>(R.id.animView).apply {
                setLoop(Int.MAX_VALUE)
                setScaleType(ScaleType.FIT_CENTER)
            }
        }

        if (isVisible) {
            this.onVisibilityAggregated(true)
        }
    }

    private fun onSurfaceTextureSizeChanged(surface: Surface, width: Int, height: Int) {
        Log.d(
            TAG,
            "onStartCommand, onSurfaceTextureSizeChanged: surface = $surface, width = $width, height = $height"
        )
    }

    private fun onSurfaceTextureDestroyed(surface: Surface?) {
        this.onVisibilityAggregated(false)
        //this.surface?.release()
        animView = null
        presentation = null
        this.surface = null
    }

    private var isVisible: Boolean = false

    private fun onVisibilityAggregated(isVisible: Boolean) {
        this.isVisible = isVisible

        if (isVisible) {
            presentation?.show()
            animView?.startPlay(assets, "demo.mp4")
        } else {
            animView?.takeIf { it.isRunning() }?.stopPlay()
            presentation?.takeIf { it.isShowing }?.dismiss()
        }
    }

    private fun onScreenStateChanged(screenState: Int) {
    }

    private lateinit var paint: Paint
    private val handler by lazy {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                20f,
                resources.displayMetrics
            )
            color = Color.RED
        }

        Handler(Looper.getMainLooper())
    }
    private val runnable = Runnable {
        surface?.let {
            var canvas: Canvas? = null
            try {
                canvas = it.lockCanvas(null)
                this.onDraw(canvas)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            } finally {
                if (canvas != null) {
                    it.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.translate(canvas.width / 2f, canvas.height / 2f)
        val text = System.currentTimeMillis().toString()
        canvas.drawText(
            text,
            -paint.measureText(text) / 2,
            -(paint.descent() + paint.ascent()) / 2,
            paint
        )

        handler.postDelayed(runnable, 1000)
    }

}
