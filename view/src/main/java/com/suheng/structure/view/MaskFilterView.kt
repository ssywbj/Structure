package com.suheng.structure.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt
import com.suheng.structure.view.drawable.SunlightDrawable

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
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            shader = LinearGradient(
                0f, bmpHeight.toFloat(), bmpWidth.toFloat(), 0f, intArrayOf(
                    "#CB94FF".toColorInt(),
                    "#9F84FF".toColorInt(),
                    "#8297FF".toColorInt(),
                    "#79BEFF".toColorInt(),
                    "#9DCFFF".toColorInt()
                ), floatArrayOf(0f, 0.26f, 0.6f, 0.79f, 1f), Shader.TileMode.CLAMP
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
        bitmap = this.createBitmap(w, h, 3f)
    }

    /*override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width.toFloat() / 2f
        val centerY = height.toFloat() / 2f
        val radius = centerX.coerceAtMost(centerY)

        bitmap?.let {
            canvas.drawBitmap(it, null, rect, null)
        }
    }*/

    fun createSunshineEffect(context: Context, source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        // 1. 创建径向渐变遮罩
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mask)
        val centerX = width / 2f
        val centerY = height / 2f
        //val radius = Math.max(width, height) * 0.6f
        val radius = Math.max(width, height).toFloat()

        val gradient = RadialGradient(
            centerX, centerY, radius,
            //intArrayOf(Color.WHITE, Color.TRANSPARENT),
            intArrayOf(Color.argb(180, 255, 255, 255), Color.TRANSPARENT),
            floatArrayOf(0.0f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // 2. 对遮罩做高斯模糊
        val blurredMask = blurBitmap(context, mask, 40f) // 40f为模糊半径，可调

        // 3. 叠加到原图
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val resultCanvas = Canvas(result)
        resultCanvas.drawBitmap(source, 0f, 0f, null)

        val overlayPaint = Paint()
        overlayPaint.isAntiAlias = true
        overlayPaint.alpha = 160 // 0-255，调节中心亮度
        resultCanvas.drawBitmap(blurredMask, 0f, 0f, overlayPaint)

        return result
    }

    // 高斯模糊辅助函数（API 17+）
    private fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val outputAlloc = Allocation.createFromBitmap(rs, output)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius.coerceIn(0f, 25f)) // 最大25
        script.setInput(input)
        script.forEach(outputAlloc)
        outputAlloc.copyTo(output)
        rs.destroy()
        return output
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i("Wbj", "onAttachedToWindow, background: $background")
        background = SunlightDrawable()/*.also { it.start() }*/
        Log.i("Wbj", "onAttachedToWindow, background: $background")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i("Wbj", "onDetachedFromWindow")
        (background as? SunlightDrawable)?.cancel()
    }

}