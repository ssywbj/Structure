package com.suheng.wallpaper.myhealth

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.widget.Button
import android.widget.FrameLayout

class ViewRenderService : WallpaperService() {

    private lateinit var mContext: Context
    private lateinit var mTAG: String

    override fun onCreate() {
        super.onCreate()
        mContext = this
        mTAG = javaClass.simpleName
        Log.d(mTAG, "onCreate, service: $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(mTAG, "onDestroy, service: $this")
    }

    override fun onCreateEngine(): Engine {
        return CanvasEngine()
    }

    //https://github.com/arthabus/AndroidViewToGLRendering
    inner class CanvasEngine : Engine() {
        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24f, resources.displayMetrics)
        }

        private var mRootLayout: FrameLayout? = null
        private var mButton: Button? = null
        private val mHandler = Handler(Looper.getMainLooper())
        private val mRunnable = Runnable {
            mButton?.let {
                it.text = System.currentTimeMillis().toString()
                //mRootLayout.invalidate(mRect);
                invalidate()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d(mTAG, "onCreate, surfaceHolder = $surfaceHolder")
            (LayoutInflater.from(mContext)
                .inflate(R.layout.rendered_layout, null) as? FrameLayout)?.let {
                mRootLayout = it
                mButton = it.findViewById(R.id.button)
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.d(mTAG, "onSurfaceCreated, holder = $holder")
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.d(
                mTAG,
                "onSurfaceChanged, format = $format, width = $width, height = $height, holder = $holder"
            )
            invalidate()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Log.d(mTAG, "onSurfaceDestroyed, holder: $holder")
        }

        override fun onDestroy() {
            super.onDestroy()
            mHandler.removeCallbacks(mRunnable)
        }

        private fun invalidate() {
            surfaceHolder.lockCanvas()?.run {
                try {
                    onDraw(this)
                } catch (e: java.lang.Exception) {
                    Log.e(mTAG, "onDraw error: $e")
                }
                surfaceHolder.unlockCanvasAndPost(this)
            }
        }

        private fun onDraw(canvas: Canvas) {
            mRootLayout?.let {
                val widthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY)
                val heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.EXACTLY)
                //val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                //val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                it.measure(widthMeasureSpec, heightMeasureSpec)
                it.layout(0, 0, canvas.width, canvas.height)
                it.draw(canvas)

                mHandler.postDelayed(mRunnable, 1000)
            }

            canvas.save()
            canvas.translate(canvas.width / 2f, canvas.height / 2f)
            val text = "Surface Render"
            canvas.drawText(text, -mPaint.measureText(text) / 2f, 0f, mPaint)
            canvas.restore()
        }

    }
}