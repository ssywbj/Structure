package com.suheng.structure.view.drawable

import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Trace
import android.util.Log
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColorInt

class Halo2(
    val ctx: Context,
) : Drawable() {

    companion object {
        const val TAG = "Halo2"
    }

    private val blurBgScale: Float = 2f

    private var blurBgPaint: Paint = Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
        isDither = true
    }

    private var blurBgBitmap: Bitmap? = null

    //private val dstRect = RectF()

    private val colors = intArrayOf(
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#79BEFF".toColorInt(),
        "#9DCFFF".toColorInt()
    )

    /*private val colors = intArrayOf(
        "#7FCB94FF".toColorInt(),
        "#7F9F84FF".toColorInt(),
        "#7F8297FF".toColorInt(),
        "#7F79BEFF".toColorInt(),
        "#7F9DCFFF".toColorInt()
    )*/

    /*private val endColors = intArrayOf(
        "#B69FFF".toColorInt(),
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#8297FF".toColorInt()
    )*/

    private val endColors = intArrayOf(
        "#F291B2".toColorInt(),
        "#CAA5FE".toColorInt(),
        "#75BFFF".toColorInt(),
        "#79BEFF".toColorInt(),
        "#85EFFF".toColorInt()
    )

    /*private val endColors = intArrayOf(
        "#7FB69FFF".toColorInt(),
        "#7FCB94FF".toColorInt(),
        "#7F9F84FF".toColorInt(),
        "#7F8297FF".toColorInt(),
        "#7F8297FF".toColorInt()
    )*/

    private val positions = floatArrayOf(0f, 0.26f, 0.6f, 0.79f, 1f)
    //private val endPositions = floatArrayOf(0f, 0.51f, 0.7f, 0.88f, 1f)
    private val endPositions = floatArrayOf(0f, 0.3f, 0.66f, 0.85f, 1f)

    private val animator by lazy {
        val pvhList = mutableListOf<PropertyValuesHolder>()
        val colorProps = mutableListOf<String>()
        val pstProps = mutableListOf<String>()
        val colorLen = colors.size.coerceAtMost(endColors.size)
        for (i in 0 until colorLen) {
            val property = "color$i".also { colorProps.add(it) }
            pvhList.add(
                PropertyValuesHolder.ofMultiInt(
                    property, arrayOf(colors[i].toArgb(), endColors[i].toArgb())
                )
            )
        }
        val pstLen = positions.size.coerceAtMost(endPositions.size)
        for (i in 0 until pstLen) {
            val property = "position$i".also { pstProps.add(it) }
            pvhList.add(PropertyValuesHolder.ofFloat(property, positions[i], endPositions[i]))
        }

        ValueAnimator.ofPropertyValuesHolder(*pvhList.toTypedArray()).apply {
            duration = 1500
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
                //blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())

                blurBgPaint.shader = LinearGradient(
                    0f, bounds.height().toFloat(), bounds.width().toFloat(), 0f,
                    colors, positions, Shader.TileMode.CLAMP
                )

                invalidateSelf()
            }

            addListener(doOnEnd {
                //blurBgPaint.shader = null
                invalidateSelf()
            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) {
        blurBgPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        blurBgPaint.setColorFilter(colorFilter)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        Log.d(TAG, "onBoundsChange: width: ${bounds.width()}, height: ${bounds.height()}")
        //blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())

        blurBgPaint.shader = LinearGradient(
            0f, bounds.height().toFloat(), bounds.width().toFloat(), 0f,
            colors, positions, Shader.TileMode.CLAMP
        )
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        //dstRect.set(0f, 0f, width.toFloat(), height.toFloat())
        Log.d(TAG, "2, draw: width: $width, height: $height")

        /*blurBgBitmap?.let {
            canvas.drawBitmap(it, null, dstRect, null)
        }*/
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), blurBgPaint)
    }

    fun start() {
        if (!animator.isRunning) {
            animator.start()
        }
    }

    fun cancel() {
        if (animator.isRunning) {
            animator.cancel()
        }
    }

    private fun createBgBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        blurBgBitmap?.takeUnless { it.isRecycled }?.recycle()

        blurBgPaint.shader = LinearGradient(
            0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
            colors, positions, Shader.TileMode.CLAMP
        )

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        Log.i(
            TAG, "createBitmap, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                    ", blurBgScale: $blurBgScale"
        )
        /*dstRect.set(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat())
        Log.i(TAG, "insert, dstRect: $dstRect, ${dstRect.width()}, ${dstRect.height()}")
        canvas.drawRect(dstRect, blurBgPaint)*/

        return bitmap
    }

    fun setAnimatorListener(animatorListener: AnimatorListenerAdapter) {
        animator.addListener(animatorListener)
    }
}

inline fun beginSection(sectionName: String, block: () -> Unit) {
    Trace.beginSection(sectionName)
    block()
    Trace.endSection()
}

fun Int.toArgb(): IntArray = intArrayOf(
    Color.alpha(this), Color.red(this), Color.green(this), Color.blue(this)
)

inline fun Canvas.saveLayer(bounds: RectF?, paint: Paint?, block: Canvas.() -> Unit) {
    val saveLayer = saveLayer(bounds, paint)
    block()
    restoreToCount(saveLayer)
}