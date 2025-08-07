package com.suheng.structure.view.drawable

import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.animation.PathInterpolator
import androidx.core.graphics.toColorInt
import com.suheng.structure.view.kt.saveLayer

class SunlightDrawable(ctx: Context) : Drawable() {

    companion object {
        lateinit var instance: SunlightDrawable
        const val TAG = "Wbj"
    }

    init {
        instance = this
    }

    var blurBgInsert = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 16f, ctx.resources.displayMetrics
    ).toInt()

    val blurBgScale: Float = 2f
    private val blurBgRadio: Float = 20f
    private var blurBgPaint: Paint = Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
        isDither = true
        maskFilter = BlurMaskFilter(blurBgRadio, BlurMaskFilter.Blur.NORMAL)
    }

    private var xfermodePaint: Paint = Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
        isDither = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    private var blurBgBitmap: Bitmap? = null
    private var transBitmap: Bitmap? = null
    private val dstRect = Rect()

    /*private val colors = intArrayOf(
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#79BEFF".toColorInt(),
        "#9DCFFF".toColorInt()
    )*/

    private val colors = intArrayOf(
        "#7FCB94FF".toColorInt(),
        "#7F9F84FF".toColorInt(),
        "#7F8297FF".toColorInt(),
        "#7F79BEFF".toColorInt(),
        "#7F9DCFFF".toColorInt()
    )

    /*private val endColors = intArrayOf(
        "#B69FFF".toColorInt(),
        "#CB94FF".toColorInt(),
        "#9F84FF".toColorInt(),
        "#8297FF".toColorInt(),
        "#8297FF".toColorInt()
    )*/

    private val endColors = intArrayOf(
        "#7FB69FFF".toColorInt(),
        "#7FCB94FF".toColorInt(),
        "#7F9F84FF".toColorInt(),
        "#7F8297FF".toColorInt(),
        "#7F8297FF".toColorInt()
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
                blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())
                invalidateSelf()
            }
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
        blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())
        leftRadialBitmap = createLeftRadialBitmap(bounds.width(), bounds.height())
        topRightRadialBitmap = createTopRightRadialBitmap(bounds.width(), bounds.height())
        bottomRightRadialBitmap = createBottomRightRadialBitmap(bounds.width(), bounds.height())
        transBitmap = createTransBitmap(bounds.width(), bounds.height())
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        Log.d(TAG, "draw: width: $width, height: $height")
        dstRect.set(0, 0, width, height)
        /*blurBgBitmap?.let {
            canvas.drawBitmap(it, null, dstRect, null)
        }*/

        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        leftRadialBitmap?.let {
            canvas.saveLayer(rectF, null) {
                blurBgBitmap?.let { bm ->
                    drawBitmap(bm, null, rectF, null)
                }
                drawBitmap(it, null, rectF, xfermodePaint)
            }
        }

        /*topRightRadialBitmap?.let {
            canvas.drawBitmap(it, null, dstRect, null)
        }*/
        /*bottomRightRadialBitmap?.let {
            canvas.drawBitmap(it, null, dstRect, null)
        }*/
    }

    fun start() {
        if (animator.isRunning) {
            return
        }
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

    private fun createBgBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        var bmpWidth = (w / blurBgScale).toInt()
        var bmpHeight = (h / blurBgScale).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        bmpWidth += blurBgInsert * 2
        bmpHeight += blurBgInsert * 2
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
                    ", blurBgScale: $blurBgScale, insetBlurBg: $blurBgInsert"
        )
        dstRect.set(0, 0, bmpWidth, bmpHeight)
        dstRect.inset(blurBgInsert, blurBgInsert)
        Log.i(TAG, "insert, dstRect: $dstRect, ${dstRect.width()}, ${dstRect.height()}")
        canvas.drawRect(dstRect, blurBgPaint)

        return bitmap
    }

    private fun createTransBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        var bmpWidth = (w / blurBgScale).toInt()
        var bmpHeight = (h / blurBgScale).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        bmpWidth += blurBgInsert * 2
        bmpHeight += blurBgInsert * 2
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        transBitmap?.takeUnless { it.isRecycled }?.recycle()
        val paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.WHITE
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        Log.i(
            TAG, "createBitmap, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                    ", blurBgScale: $blurBgScale, insetBlurBg: $blurBgInsert"
        )
        dstRect.set(0, 0, bmpWidth, bmpHeight)
        dstRect.inset(blurBgInsert, blurBgInsert)
        Log.i(TAG, "insert, dstRect: $dstRect, ${dstRect.width()}, ${dstRect.height()}")
        canvas.drawRect(dstRect, paint)

        return bitmap
    }

    private fun createBgBitmap2(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        var bmpWidth = (w / blurBgScale).toInt()
        var bmpHeight = (h / blurBgScale).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        bmpWidth += blurBgInsert * 2
        bmpHeight += blurBgInsert * 2
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
                    ", blurBgScale: $blurBgScale, insetBlurBg: $blurBgInsert"
        )
        dstRect.set(0, 0, bmpWidth, bmpHeight)
        dstRect.inset(blurBgInsert, blurBgInsert)
        Log.i(TAG, "insert, dstRect: $dstRect, ${dstRect.width()}, ${dstRect.height()}")
        canvas.drawRect(dstRect, blurBgPaint)

        return bitmap
    }

    private var leftRadialBitmap: Bitmap? = null

    private fun createLeftRadialBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        leftRadialBitmap?.takeUnless { it.isRecycled }?.recycle()

        val centerX = bmpWidth / 2f
        val centerY = bmpHeight / 2f
        val radius = centerX.coerceAtMost(centerY)
        val startColor = "#FF80C1FF".toColorInt()
        val middleColor = "#9E8BE8FF".toColorInt()
        val endColor = "#009DCFFF".toColorInt()
        //val endColor = "#FF0000".toColorInt()

        val paint = Paint().apply {
            shader = RadialGradient(
                centerX, centerY, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.46f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.translate(-bmpWidth.toFloat() / 2, 0f)
        canvas.drawCircle(centerX, centerY, radius, paint)

        return bitmap
    }

    private var topRightRadialBitmap: Bitmap? = null

    private fun createTopRightRadialBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        topRightRadialBitmap?.takeUnless { it.isRecycled }?.recycle()

        val centerX = bmpWidth / 2f
        val centerY = bmpHeight / 2f
        val radius = centerX.coerceAtMost(centerY)
        val startColor = "#FF8297FF".toColorInt()
        val middleColor = "#D696A8FF".toColorInt()
        val endColor = "#00FFFFFF".toColorInt()

        val paint = Paint().apply {
            shader = RadialGradient(
                centerX, centerY, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.34f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(centerX, centerY, radius, paint)

        return bitmap
    }

    private var bottomRightRadialBitmap: Bitmap? = null

    private fun createBottomRightRadialBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        bottomRightRadialBitmap?.takeUnless { it.isRecycled }?.recycle()

        val centerX = bmpWidth / 2f
        val centerY = bmpHeight / 2f
        val radius = centerX.coerceAtMost(centerY)
        val startColor = "#FF80E6FF".toColorInt()
        val middleColor = "#9E8BE8FF".toColorInt()
        val endColor = "#009DCFFF".toColorInt()

        val paint = Paint().apply {
            shader = RadialGradient(
                centerX, centerY, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.46f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(centerX, centerY, radius, paint)

        return bitmap
    }

    fun setAnimatorListener(animatorListener: AnimatorListenerAdapter) {
        animator.addListener(animatorListener)
    }

    fun createBitmap(w: Int, h: Int, scaleRatio: Float = 1f, inset: Int = 0): Bitmap? {
        if (w <= 0 || h <= 0 || scaleRatio == 0f) {
            return null
        }
        var bmpWidth = (w / scaleRatio).toInt()
        var bmpHeight = (h / scaleRatio).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        bmpWidth += inset * 2
        bmpHeight += inset * 2
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL)
            shader = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
        }

        Log.i(TAG, "createBitmap, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                ", scaleRatio: $scaleRatio, inset: $inset")

        val rect = Rect(0, 0, bmpWidth, bmpHeight)
        rect.inset(inset, inset)
        Log.i(TAG, "insert, rect: $rect, ${rect.width()}, ${rect.height()}")
        canvas.drawRect(rect, paint)

        return bitmap
    }

}