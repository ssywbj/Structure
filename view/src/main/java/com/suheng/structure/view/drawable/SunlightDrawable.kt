package com.suheng.structure.view.drawable

import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColorInt
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.saveLayer
import kotlin.math.pow
import kotlin.math.sqrt

class SunlightDrawable(
    val ctx: Context,
    val showBottomBitmap: Boolean = true,
    val showEdgeRadial: Boolean = true,
    val showBlurBg: Boolean = true,
    val showLinearColor: Boolean = true
) : Drawable() {

    companion object {
        const val TAG = "Wbj"
    }

    private val itWidth = ctx.resources.getDimensionPixelOffset(R.dimen.sunlight_width_out)
    private val itHeight = ctx.resources.getDimensionPixelOffset(R.dimen.sunlight_heigh_out)

    private var topBitmap: Bitmap? = null
    private var topBitmapScale: Float = 1f
    private var topBitmapRotate: Float = 0f
    private val topOriginBitmap by lazy {
        BitmapFactory.decodeResource(
            ctx.resources, R.drawable.top_rotate,
            BitmapFactory.Options().apply { inScaled = false })
    }
    private var bottomBitmap: Bitmap? = null
    private var bottomBitmapScale: Float = 0f
    private var bottomBitmapRotate: Float = 0f
    private val bottomOriginBitmap by lazy {
        BitmapFactory.decodeResource(
            ctx.resources, R.drawable.bottom_rotate,
            BitmapFactory.Options().apply { inScaled = false })
    }

    private fun createTopBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        topBitmap?.takeUnless { it.isRecycled }?.recycle()

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        dstRect.set(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat())
        canvas.scale(
            topBitmapScale, topBitmapScale, bmpWidth.toFloat() / 2, bmpHeight.toFloat() / 2
        )
        canvas.rotate(topBitmapRotate, bmpWidth.toFloat() / 2, bmpHeight.toFloat() / 2)
        canvas.drawBitmap(topOriginBitmap, null, dstRect, null)
        return bitmap
    }

    private fun createBottomBitmap(w: Int, h: Int): Bitmap? {
        if (!showBottomBitmap) {
            return null
        }
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        bottomBitmap?.takeUnless { it.isRecycled }?.recycle()

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        dstRect.set(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat())
        canvas.translate(-200f, 150f)
        canvas.scale(
            bottomBitmapScale, bottomBitmapScale, bmpWidth.toFloat() / 2, bmpHeight.toFloat() / 2
        )
        canvas.rotate(bottomBitmapRotate, bmpWidth.toFloat() / 2, bmpHeight.toFloat() / 2)
        canvas.drawBitmap(bottomOriginBitmap, null, dstRect, null)
        return bitmap
    }

    private var blurBgInsert = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 16f, ctx.resources.displayMetrics
    ).toInt()

    private val blurBgScale: Float = 2f
    private val blurBgRadio: Float = 20f

    private var blurBgPaint: Paint = Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
        isDither = true
        if (showBlurBg) {
            maskFilter = BlurMaskFilter(blurBgRadio, BlurMaskFilter.Blur.NORMAL)
        }
    }
    private var xfermodePaint: Paint = Paint().apply {
        set(blurBgPaint)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    private var maskCirclePaint: Paint = Paint().apply {
        set(blurBgPaint)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private var blurBgBitmap: Bitmap? = null

    private val dstRect = RectF()

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
        val colorProps = mutableListOf<String>()
        val pstProps = mutableListOf<String>()
        if (showLinearColor) {
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
        }
        /*val propTopBitmapScale = "propTopBitmapScale"
        val propTopBitmapRotate = "propTopBitmapScale"
        pvhList.add(PropertyValuesHolder.ofFloat(propTopBitmapScale, 0f, 3f))
        pvhList.add(PropertyValuesHolder.ofFloat(propTopBitmapRotate, 0f, 360f))*/

        val propBottomBitmapScale = "propBottomBitmapScale"
        val propBottomBitmapRotate = "propBottomBitmapRotate"
        if (showBottomBitmap) {
            pvhList.add(PropertyValuesHolder.ofFloat(propBottomBitmapScale, 0f, 3f))
            pvhList.add(PropertyValuesHolder.ofFloat(propBottomBitmapRotate, 0f, 360f))
        }

        ValueAnimator.ofPropertyValuesHolder(*pvhList.toTypedArray()).apply {
            //duration = 500
            duration = 500
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
                //blurBgBitmap = createBgBitmap(itWidth, itHeight)
                /*(animation.getAnimatedValue(propTopBitmapScale) as? Float)?.let {
                    topBitmapScale = it
                }
                (animation.getAnimatedValue(propTopBitmapRotate) as? Float)?.let {
                    topBitmapRotate = it
                }
                topBitmap = createTopBitmap(topOriginBitmap.width, topOriginBitmap.height)*/

                (animation.getAnimatedValue(propBottomBitmapScale) as? Float)?.let {
                    bottomBitmapScale = it
                }
                (animation.getAnimatedValue(propBottomBitmapRotate) as? Float)?.let {
                    bottomBitmapRotate = it
                }
                bottomBitmap = createBottomBitmap(bottomOriginBitmap.width, bottomOriginBitmap.height)

                invalidateSelf()
            }

            addListener(doOnEnd {
                maskCircleBitmap = createMaskCircleBitmap(bounds.width(), bounds.height(), 0f)
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
        blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())
        if (showEdgeRadial) {
            leftRadialBitmap = createLeftRadialBitmap(bounds.width(), bounds.height())
            topRightRadialBitmap = createTopRightRadialBitmap(bounds.width(), bounds.height())
            bottomRightRadialBitmap = createBottomRightRadialBitmap(bounds.width(), bounds.height())
        }

        maskRadius = null
        maskCircleBitmap = createMaskCircleBitmap(bounds.width(), bounds.height(), 0f)
        //topBitmap = createTopBitmap(topOriginBitmap.width, topOriginBitmap.height)
        bottomBitmap = createBottomBitmap(bottomOriginBitmap.width, bottomOriginBitmap.height)
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        dstRect.set(0f, 0f, width.toFloat(), height.toFloat())
        Log.d(TAG, "draw: width: $width, height: $height")

        blurBgBitmap?.let {
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.saveLayer(dstRect, null) {
                drawBitmap(it, null, dstRect, null)

                if (showBlurBg) {
                    val dx = blurBgInsert.toFloat() * 2
                    dstRect.inset(dx, dx)
                }

                /*topBitmap?.let { bm ->
                    drawBitmap(bm, null, dstRect, xfermodePaint)
                }*/

                bottomBitmap?.let { bm ->
                    drawBitmap(bm, null, dstRect, xfermodePaint)
                }

                leftRadialBitmap?.let { bm ->
                    drawBitmap(bm, null, dstRect, xfermodePaint)
                }
                topRightRadialBitmap?.let { bm ->
                    drawBitmap(bm, null, dstRect, xfermodePaint)
                }
                bottomRightRadialBitmap?.let { bm ->
                    drawBitmap(bm, null, dstRect, xfermodePaint)
                }

                maskCircleBitmap?.let { bm ->
                    dstRect.set(0f, 0f, width.toFloat(), height.toFloat())
                    drawBitmap(bm, null, dstRect, maskCirclePaint)
                }
            }
        }
    }

    fun start() {
        if (showLinearColor) {
            if (!animator.isRunning) {
                animator.start()
            }
        }

        if (!animatorCircleBitmap.isRunning) {
            animatorCircleBitmap.start()
        }
    }

    fun cancel() {
        if (animator.isRunning) {
            animator.cancel()
        }

        if (animatorCircleBitmap.isRunning) {
            animatorCircleBitmap.cancel()
        }
    }

    fun Int.toArgb(): IntArray = intArrayOf(
        Color.alpha(this), Color.red(this), Color.green(this), Color.blue(this)
    )

    private var linearGradient: LinearGradient? = null

    private fun createBgBitmap(w: Int, h: Int): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        var bmpWidth = (w / blurBgScale).toInt()
        var bmpHeight = (h / blurBgScale).toInt()
        Log.i(TAG, "origin area, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        //bmpWidth += blurBgInsert * 2
        //bmpHeight += blurBgInsert * 2
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        blurBgBitmap?.takeUnless { it.isRecycled }?.recycle()
        if (showLinearColor) {
            blurBgPaint.shader = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                colors, positions, Shader.TileMode.CLAMP
            )
        } else {
            if (linearGradient == null) {
                LinearGradient(
                    0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f,
                    colors, positions, Shader.TileMode.CLAMP
                ).also {
                    linearGradient = it
                    blurBgPaint.shader = it
                }
            }
        }

        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        Log.i(
            TAG, "createBitmap, width: $w, height: $h, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight" +
                    ", blurBgScale: $blurBgScale, insetBlurBg: $blurBgInsert"
        )
        dstRect.set(0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat())
        if (showBlurBg) {
            dstRect.inset(blurBgInsert.toFloat(), blurBgInsert.toFloat())
        }
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

        /*val cx = bmpWidth / 2f
        val cy = bmpHeight / 2f
        val radius = cx.coerceAtMost(cy)*/
        val cx = 0f
        val cy = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 54f, ctx.resources.displayMetrics
        )
        val radius = bmpWidth * 0.73f / 2
        /*val startColor = "#0000FF".toColorInt()
        val middleColor = "#00FF00".toColorInt()
        val endColor = "#FF0000".toColorInt()*/
        val startColor = "#FF80C1FF".toColorInt()
        val middleColor = "#9E8BE8FF".toColorInt()
        val endColor = "#009DCFFF".toColorInt()

        val paint = Paint().apply {
            shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.46f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(cx, cy, radius, paint)

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

        /*val cx = bmpWidth / 2f
        val cy = bmpHeight / 2f
        val radius = cx.coerceAtMost(cy)*/
        val offset = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, ctx.resources.displayMetrics
        )
        val cx = bmpWidth.toFloat() - offset
        val cy = offset
        val radius = bmpWidth * 0.86f / 2
        /*val startColor = "#0000FF".toColorInt()
        val middleColor = "#00FF00".toColorInt()
        val endColor = "#FF0000".toColorInt()*/
        val startColor = "#FF8297FF".toColorInt()
        val middleColor = "#D696A8FF".toColorInt()
        val endColor = "#00FFFFFF".toColorInt()

        val paint = Paint().apply {
            shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.34f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(cx, cy, radius, paint)

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

        /*val cx = bmpWidth / 2f
        val cy = bmpHeight / 2f
        val radius = cx.coerceAtMost(cy)*/
        val cx = bmpWidth / 1.3f
        val cy = bmpHeight+TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 30f, ctx.resources.displayMetrics
        )
        val radius = bmpWidth * 0.98f / 2
        val startColor = "#FF80E6FF".toColorInt()
        val middleColor = "#9E8BE8FF".toColorInt()
        val endColor = "#009DCFFF".toColorInt()
        /*val startColor = "#0000FF".toColorInt()
        val middleColor = "#00FF00".toColorInt()
        val endColor = "#FF0000".toColorInt()*/

        val paint = Paint().apply {
            shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(startColor, middleColor, endColor),
                floatArrayOf(0f, 0.46f, 1f), Shader.TileMode.CLAMP
            )
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(cx, cy, radius, paint)

        return bitmap
    }

    private var maskCircleBitmap: Bitmap? = null
    private var maskRadius: Float? = null
    private var animatorCircleBitmap = ValueAnimator.ofFloat().apply {
        duration = 500
        interpolator = PathInterpolator(0.2f, 0f, 0.1f, 1f)
        addUpdateListener {
            (animatedValue as? Float)?.let { radius ->
                maskCircleBitmap = createMaskCircleBitmap(bounds.width(), bounds.height(), radius)
                if (!showLinearColor) {
                    if (blurBgBitmap == null) {
                        blurBgBitmap = createBgBitmap(bounds.width(), bounds.height())
                    }
                    invalidateSelf()
                }
            }
        }
        addListener(doOnEnd {
            maskCircleBitmap = createMaskCircleBitmap(bounds.width(), bounds.height(), 0f)
            invalidateSelf()
        })
    }

    private val offset = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 60f, ctx.resources.displayMetrics
    )

    private fun createMaskCircleBitmap(w: Int, h: Int, radius: Float): Bitmap? {
        if (w <= 0 || h <= 0 || blurBgScale == 0f) {
            return null
        }
        val bmpWidth = (w / blurBgScale).toInt()
        val bmpHeight = (h / blurBgScale).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            return null
        }

        maskCircleBitmap?.takeUnless { it.isRecycled }?.recycle()

        val cx = bmpWidth / 2f
        var cy = bmpHeight.toFloat() - blurBgInsert
        cy += offset
        if (maskRadius == null) {
            maskRadius = radius
            animatorCircleBitmap.setFloatValues(0f, sqrt((cx - bmpWidth).pow(2) + (cy - 0).pow(2)))
        }

        val startColor = Color.WHITE
        val endColor = Color.BLACK
        /*val startColor = "#FFFFFFFF".toColorInt()
        val endColor = "#FFFFFFFF".toColorInt()*/

        val paint = Paint().apply {
            if (radius > 0) {
                shader = RadialGradient(
                    cx, cy, radius,
                    intArrayOf(startColor, endColor),
                    floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
                )
            }
        }
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(cx, cy, radius, paint)

        return bitmap
    }

    fun setAnimatorListener(animatorListener: AnimatorListenerAdapter) {
        if (showLinearColor) {
            animator.addListener(animatorListener)
        } else {
            animatorCircleBitmap.addListener(animatorListener)
        }
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