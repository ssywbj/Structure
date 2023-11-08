package com.suheng.structure.view.drawable

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange

class ProgressDrawable(private val animatorListener: Animator.AnimatorListener) : Drawable() {

    private val rect: RectF = RectF()
    private val path: Path = Path()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 60f
        strokeWidth = 2f
    }

    private var progress: Int = 0
    private var max: Int = 100

    fun setProgress(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) progress: Int) {
        val value = (1f * progress / max * 100).toInt()
        this.progress = if (value > 100) 100 else value

        invalidateSelf()
    }

    fun setProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        this.progress = (progress * 100).toInt()

        invalidateSelf()
    }

    fun setMax(@IntRange(from = 1, to = Int.MAX_VALUE.toLong()) max: Int) {
        this.max = if (max < 1) 1 else max
    }

    private val animator by lazy {
        ValueAnimator.ofFloat(0f, 0f).apply {
            duration = 2000
            addUpdateListener {
                (it.animatedValue as? Float)?.let { value ->
                    path.reset()
                    //rect.set(rect.left, value, rect.right, rect.bottom)
                    rect.set(value, rect.top, rect.right, rect.bottom)
                    path.addRect(rect, Path.Direction.CCW)

                    setProgress(it.animatedFraction)
                }
            }

            addListener(animatorListener);
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        if (animator.isRunning) {
            //animator.reverse()
            return
        } else {
            path.reset()
            rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            Log.d("Wbj", "rect: ${rect.toShortString()}")
            path.addRect(rect, Path.Direction.CCW)
            //animator.setFloatValues(top.toFloat(), bottom.toFloat())
            animator.setFloatValues(left.toFloat(), right.toFloat())
            animator.start()
        }
    }

    override fun draw(canvas: Canvas) {
        val centerX = bounds.width() / 2f
        val centerY = bounds.height() / 2f
        canvas.drawLine(0f, centerY, centerX * 2, centerY, paint.apply { color = Color.RED })
        canvas.drawLine(centerX, 0f, centerX, centerY * 2, paint)

        val text = "$progress%"
        canvas.drawText(
            text,
            centerX - paint.measureText(text) / 2,
            centerY - (paint.descent() + paint.ascent()) / 2f,
            paint.apply { color = Color.WHITE }
        )

        canvas.clipPath(path)
        canvas.drawColor(Color.argb((255 * 0.5f).toInt(), 0xFF, 0xFF, 0xFF))
        canvas.drawText(
            text,
            centerX - paint.measureText(text) / 2,
            centerY - (paint.descent() + paint.ascent()) / 2f,
            paint.apply { color = Color.BLACK }
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}