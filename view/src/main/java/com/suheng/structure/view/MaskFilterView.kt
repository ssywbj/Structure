package com.suheng.structure.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.pow
import kotlin.math.sqrt

class MaskFilterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var blurBgPaint: Paint = Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
        isDither = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i("Wbj", "onAttachedToWindow, background: $background")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i("Wbj", "onDetachedFromWindow")
    }

    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = sqrt((centerX - 0).pow(2) + (centerY - 0).pow(2))
        blurBgPaint.shader = RadialGradient(
            centerX, centerY, radius,
            intArrayOf("#7FCB94FF".toColorInt(), "#008297FF".toColorInt()),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, radius, blurBgPaint)
    }

}