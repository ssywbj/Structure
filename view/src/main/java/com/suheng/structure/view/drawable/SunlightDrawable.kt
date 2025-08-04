package com.suheng.structure.view.drawable

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.PathInterpolator
import androidx.core.graphics.toColorInt

class SunlightDrawable() : Drawable() {

    companion object {
        lateinit var instance: SunlightDrawable
        const val TAG = "Wbj"
    }

    init {
        instance = this
    }

    private val paint = Paint()

    private val colors = intArrayOf(
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#79BEFF".toColorInt(),
        "#9DCFFF".toColorInt()
    )

    /*private val colors = intArrayOf(
        "#99CB94FF".toColorInt(),
        "#999F84FF".toColorInt(),
        "#998297FF".toColorInt(),
        "#9979BEFF".toColorInt(),
        "#999DCFFF".toColorInt()
    )*/

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

    /*fun createBitmap(w: Int, h: Int, scaleRatio: Float = 1f): Bitmap? {
        if (w <= 0 || h <= 0 || scaleRatio == 0f) {
            return null
        }
        val bmpWidth = (w / scaleRatio).toInt()
        val bmpHeight = (h / scaleRatio).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            shader = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
        }

        Log.i(TAG, "createBitmap width: $w, height: $h")
        canvas.drawRect(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat(), paint)
        return bitmap
    }*/

    fun createBitmap(w: Int, h: Int, scaleRatio: Float = 1f, inset: Int = 0): Bitmap? {
        if (w <= 0 || h <= 0 || scaleRatio == 0f) {
            return null
        }
        val bmpWidth = (w / scaleRatio).toInt()
        val bmpHeight = (h / scaleRatio).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
            shader = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
        }

        Log.i(TAG, "createBitmap, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                ", scaleRatio: $scaleRatio, inset: ${inset / scaleRatio}")

        val rect = Rect(0, 0, bmpWidth, bmpHeight)
        patternBlackBitmap(bmpWidth, bmpHeight)?.let {
            canvas.drawBitmap(it, null, rect, null)
        }
        if (inset == 0) {
            canvas.drawRect(rect, paint)
        } else {
            val dx = (inset / scaleRatio / 2f).toInt()
            rect.inset(dx, dx)
            Log.i(TAG, "insert, rect: $rect, ${rect.width()}, ${rect.height()}")
            canvas.drawRect(rect, paint)
        }

        val src = Rect(0, 0, bmpWidth, bmpHeight)
        val bitmap2 = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(bitmap2)
        src.inset(16,16)
        val rect2 = Rect(0, 0, bmpWidth, bmpHeight)
        canvas2.drawBitmap(bitmap, src, rect2, null)

        //return bitmap
        return bitmap2
        //return Bitmap.createBitmap(bitmap,20,20,bitmap.width-20,bitmap.height-20)
    }

    fun patternBlackBitmap(w: Int = 480, h: Int = 320): Bitmap? {
        if (w <= 0 || h <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            /*shader = LinearGradient(
                0f, w.toFloat(), h.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )*/
        }

        val rect = Rect(0, 0, w, h)
        Log.i(TAG, "patternBlackBitmap, width: $w, height: $h")
        canvas.drawRect(rect, paint)
        return bitmap
    }

    fun patternBlurBitmap(w: Int = 480, h: Int = 320): Bitmap? {
        if (w <= 0 || h <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val fl = 40
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            maskFilter = BlurMaskFilter(fl.toFloat(), BlurMaskFilter.Blur.NORMAL)
        }

        val rect = Rect(0, 0, w, h)
        Log.i(TAG, "patternBlurBitmap, width: $w, height: $h")
        canvas.drawRect(rect, paint)
        val extractAlpha = bitmap.extractAlpha(paint, intArrayOf(0, 0))
        //bitmap.recycle()

        val bitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(bitmap2)
        val paint2: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            shader = LinearGradient(
                0f, h.toFloat(), w.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
        }
        //val src = Rect(0, 0, w, h)
        val src = Rect(0, 0, extractAlpha.width, extractAlpha.height)
        src.inset((fl*1.7f).toInt(), (fl*1.7f).toInt())
        //val rect2 = Rect(0, 0, w, h)
        //canvas2.translate(-fl.toFloat()/2,-fl.toFloat()/2)
        //src.inset(0, -fl)
        patternBlackBitmap(w, h)?.let {
            val rect2 = Rect(0, 0, w, h)
            //rect2.inset(10, 10)
            canvas2.drawBitmap(it, null, rect2, null)
        }
        canvas2.drawBitmap(extractAlpha, src, rect, paint2)

        return bitmap2
        //return extractAlpha
    }

    /*fun createBitmap2(w: Int, h: Int, scaleRatio: Float = 1f, inset: Int = 0): Bitmap? {
        if (w <= 0 || h <= 0 || scaleRatio == 0f) {
            return null
        }
        val bmpWidth = (w / scaleRatio).toInt()
        val bmpHeight = (h / scaleRatio).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            setMaskFilter(BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL))
            val linearGradient = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
            shader = linearGradient
        }

        val rect = Rect(0, 0, bmpWidth, bmpHeight)

        Log.i(TAG, "createBitmap2, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                ", scaleRatio: $scaleRatio, actually inset: ${inset / scaleRatio}")
        if (inset == 0) {
            canvas.drawRect(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat(), paint)
        } else {
            //val rect = Rect(0, 0, bmpWidth, bmpHeight)
            val dx = (inset / scaleRatio / 2f).toInt()
            //rect.inset(dx, dx)
            Log.i(TAG, "insert, rect: $rect, ${rect.width()}, ${rect.height()}")
            //canvas.scale(0.85f, 0.85f, rect.centerX().toFloat(), rect.centerY().toFloat())
            canvas.drawRect(rect, paint)
        }
        //return bitmap
        //return bitmap.extractAlpha(paint,intArrayOf(-2,-2))
        val extractAlpha = bitmap.extractAlpha(paint, intArrayOf(0, 0))

        val bitmap2 = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(bitmap2)
        canvas2.drawBitmap(extractAlpha, null, rect, paint)
        //return extractAlpha
        bitmap.recycle()
        return bitmap2
    }*/

}