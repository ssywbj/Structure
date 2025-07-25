package com.suheng.structure.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt

class MaskFilterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        lateinit var instance: MaskFilterView
        private const val TAG = "MaskFilterView"
    }

    init {
        instance = this
    }

    private var bitmap: Bitmap? = null
    private val rect = Rect()

    private fun createBitmap(w: Int, h: Int, scaleRatio: Float): Bitmap? {
        if (w <= 0 || h <= 0 || scaleRatio <= 0) {
            return null
        }

        val bmpWidth = (w / scaleRatio).toInt()
        val bmpHeight = (h / scaleRatio).toInt()
        val bitmap = Bitmap.createBitmap(
            bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val blurRadius = 20f
        val paint: Paint = Paint().apply {
            setMaskFilter(BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL))
            setShader(
                LinearGradient(
                    0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f, intArrayOf(
                        "#CB94FF".toColorInt(),
                        "#9F84FF".toColorInt(),
                        "#8297FF".toColorInt(),
                        "#79BEFF".toColorInt(),
                        "#9DCFFF".toColorInt()
                    ), floatArrayOf(0f, 0.26f, 0.6f, 0.79f, 1f), Shader.TileMode.CLAMP
                )
            )
            alpha = 100
        }

        val insertDx = (blurRadius * 1.5f).toInt()
        val insertDy = (insertDx * (bmpHeight.toFloat() / bmpWidth)).toInt()
        val rectSrc = Rect(0, 0, bmpWidth, bmpHeight)
        rectSrc.inset(insertDx, insertDx)
        Log.i(
            TAG,
            "createBitmap width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight, insertDx: $insertDx, insertDy: $insertDy"
        )
        canvas.drawRect(rectSrc, paint)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, w, h)
        bitmap?.takeUnless { it.isRecycled }?.recycle()
        bitmap = null
        //bitmap = this.createBitmap(w, h)
        bitmap = this.createBitmap(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width.toFloat() / 2f
        val centerY = height.toFloat() / 2f
        val radius = centerX.coerceAtMost(centerY)

        bitmap?.let {
            canvas.drawBitmap(it, null, rect, null)
        }
    }

    fun createBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            setShader(
                LinearGradient(
                    0f, bitmap.height.toFloat(), bitmap.width.toFloat(), 0f, intArrayOf(
                        "#CB94FF".toColorInt(),
                        "#9F84FF".toColorInt(),
                        "#8297FF".toColorInt(),
                        "#79BEFF".toColorInt(),
                        "#9DCFFF".toColorInt()
                    ), floatArrayOf(0f, 0.26f, 0.6f, 0.79f, 1f), Shader.TileMode.CLAMP
                )
            )
        }


        Log.i(TAG, "createBitmap width: $w, height: $h")
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        return bitmap
    }
}