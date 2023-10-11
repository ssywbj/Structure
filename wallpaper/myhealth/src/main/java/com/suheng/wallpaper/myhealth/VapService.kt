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
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.util.ScaleType

class VapService : Service() {

    companion object {
        const val TAG = "VapTextureView"
        const val EXTRA_VAP_SURFACE = "vap_surface"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "VapService, onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "VapService, onStartCommand: flags = $flags, startId = $startId")
        intent.takeIf { it.hasExtra(EXTRA_VAP_SURFACE) }
            ?.getParcelableExtra<Parcelable>(EXTRA_VAP_SURFACE).takeIf { it is Surface }?.let {
                surface = it as Surface
                Log.d(TAG, "VapService, surface = $surface")

                //handler.post(runnable)

                val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
                val virtualDisplay = displayManager.createVirtualDisplay(
                    "VirtualDisplayWallpaper",
                    1044,
                    600,
                    resources.displayMetrics.densityDpi,
                    surface,
                    0
                )
                val presentation = Presentation(this, virtualDisplay.display)
                presentation.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                presentation.setContentView(R.layout.vap_wallpaper)
                val animView = presentation.findViewById<AnimView>(R.id.animView)
                animView.setLoop(Int.MAX_VALUE)
                animView.setScaleType(ScaleType.FIT_CENTER)
                animView.startPlay(assets, "demo.mp4")
                Log.d(TAG, "VapService, isRunning = ${animView.isRunning()}")
                presentation.show()

                /*var canvas: Canvas? = null
                try {
                    canvas = surface?.lockCanvas(Rect(0, 0, 1044, 600))
                    canvas?.let { cs ->
                        this.onDraw(cs)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                } finally {
                    if (canvas != null) {
                        surface?.unlockCanvasAndPost(canvas)
                    }
                }*/

            }
        return super.onStartCommand(intent, flags, startId)
    }

    private lateinit var paint: Paint
    private var surface: Surface? = null
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
