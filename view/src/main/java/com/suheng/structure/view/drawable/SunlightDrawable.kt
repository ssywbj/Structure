package com.suheng.structure.view.drawable

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.PathInterpolator
import androidx.core.graphics.toColorInt

class SunlightDrawable() : Drawable() {

    companion object {
        const val TAG = "HPNotificationBg"
    }

    private val paint = Paint()

    private val colors = intArrayOf(
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#79BEFF".toColorInt(),
        "#9DCFFF".toColorInt()
    )

    private val endColors = intArrayOf(
        "#B69FFF".toColorInt(),
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#8297FF".toColorInt()
    )

    private val positions = floatArrayOf(0f, 0.26f, 0.6f, 0.79f, 1f)
    private val endPositions = floatArrayOf(0f, 0.51f, 0.7f, 0.88f, 1f)

    private val animator by lazy {
        val pvhList = mutableListOf<PropertyValuesHolder>()
        val colorLen = colors.size.coerceAtMost(endColors.size)
        val colorProps = mutableListOf<String>()
        for (i in 0 until colorLen) {
            val property = "color$i".also { colorProps.add(it) }
            pvhList.add(
                PropertyValuesHolder.ofMultiInt(
                    property, arrayOf(colors[i].toArgb(), endColors[i].toArgb())
                )
            )
        }
        val pstLen = positions.size.coerceAtMost(endPositions.size)
        val pstProps = mutableListOf<String>()
        for (i in 0 until pstLen) {
            val property = "position$i".also { pstProps.add(it) }
            pvhList.add(PropertyValuesHolder.ofFloat(property, positions[i], endPositions[i]))
        }
        ValueAnimator.ofPropertyValuesHolder(*pvhList.toTypedArray()).apply {
            setDuration(500)
            interpolator = PathInterpolator(0.2f, 0f, 0.1f, 1f)
            addUpdateListener { animation ->
                for ((i, prop) in colorProps.withIndex()) {
                    (animation.getAnimatedValue(prop) as? IntArray)?.takeIf { it.size > 3 }?.let {
                        if (i in colors.indices) {
                            colors[i] = Color.argb(it[0], it[1], it[2], it[3])
                        }
                        //Log.d(TAG, "color$i:${colors[i]}, a:${it[0]}, r:${it[1]}, g:${it[2]}, b:${it[3]}")
                    }
                }
                for ((i, prop) in pstProps.withIndex()) {
                    (animation.getAnimatedValue(prop) as? Float)?.let {
                        if (i in positions.indices) {
                            positions[i] = it
                        }
                    }
                }
                //Log.d(TAG, "position0:${positions[0]}, position1:${positions[1]}, positions:${positions[2]}, position3:${positions[3]}, position4:${positions[4]}")
                invalidateSelf()
            }
        }
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.setColorFilter(colorFilter)
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        Log.d(TAG, "draw: width: $width, height: $height")
        paint.shader = LinearGradient(
            0f, height.toFloat(), width.toFloat(), 0f, colors, positions, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        /*paintLeftBlur(width, height, canvas)
        paintBottomRightBlur(width, height, canvas)
        paintTopRightBlur(width, height, canvas)*/
    }

    private val paintBlur = Paint()

    private fun paintLeftBlur(width: Int, height: Int, canvas: Canvas) {
        val radius = width / 2f
        val centerX = -width / 11f
        val centerY = height / 3.5f
        val startColor = "#80C1FF".toColorInt()
        val endColor = Color.TRANSPARENT
        val shader = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(startColor, endColor),
            floatArrayOf(0f, 1.0f), Shader.TileMode.CLAMP
        )
        paintBlur.setShader(shader)
        canvas.drawCircle(centerX, centerY, radius, paintBlur)
    }

    private fun paintBottomRightBlur(width: Int, height: Int, canvas: Canvas) {
        val radius = width / 1.7f
        val centerX = width / 0.9f
        val centerY = height / 1.2f
        val startColor = "#80E6FF".toColorInt()
        val endColor = Color.TRANSPARENT
        val shader = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(startColor, endColor),
            floatArrayOf(0f, 1.0f), Shader.TileMode.CLAMP
        )
        paintBlur.setShader(shader)
        canvas.drawCircle(centerX, centerY, radius, paintBlur)
    }

    private fun paintTopRightBlur(width: Int, height: Int, canvas: Canvas) {
        val radius = width / 2.4f
        val centerX = width / 0.9f
        val centerY = height / 5f
        val startColor = "#8297FF".toColorInt()
        val endColor = Color.TRANSPARENT
        //val endColor = with(startColor.toArgb()) { Color.argb(0, this[1], this[2], this[3]) }
        val shader = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(startColor, endColor),
            floatArrayOf(0f, 1.0f), Shader.TileMode.CLAMP
        )
        paintBlur.setShader(shader)
        canvas.drawCircle(centerX, centerY, radius, paintBlur)
    }

    fun start() {
        cancel()
        animator.start()
    }

    fun cancel() {
        if (animator.isRunning) {
            animator.cancel()
        }
    }

    fun Int.toArgb(): IntArray = intArrayOf(
        Color.alpha(this), Color.red(this), Color.green(this), Color.blue(this)
    )
}