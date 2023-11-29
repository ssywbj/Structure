package com.suheng.structure.view.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.save

class Rotate3DImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = Rotate3DImage::class.java.simpleName
    }

    private val paintLine = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
    }
    private val camera = Camera()
    private val matrixCamera = Matrix()
    private val rect = RectF()
    private var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.girl_gaitubao)
    private var degrees = 0f
    private val floatArray = FloatArray(9)
    private var displayDensity = 1f
    private val animator = ValueAnimator.ofFloat(0f, 180f).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener { animation ->
            (animation.animatedValue as? Float)?.let {
                degrees = it
                invalidate()
            }
        }
    }

    init {
        displayDensity = resources.displayMetrics.density
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.CYAN)
        canvas.save {
            camera.save()
            camera.rotateY(degrees)
            //camera.applyToCanvas(canvas)
            camera.getMatrix(matrixCamera)
            camera.restore()

            floatArray.also {
                matrixCamera.getValues(it)
                it[6] = it[6] / displayDensity //数值修正
                it[7] = it[7] / displayDensity //数值修正
                matrixCamera.setValues(it)
            }

            matrixCamera.preTranslate(-width / 2f, -height / 2f)
            matrixCamera.postTranslate(width / 2f, height / 2f)

            concat(matrixCamera)
            drawBitmap(bitmap, null, rect, null)
        }

        canvas.save {
            drawLine(0f, height / 2f, width.toFloat(), height / 2f, paintLine)
            drawLine(width / 2f, 0f, width / 2f, height.toFloat(), paintLine)
        }
    }

    fun startAnimation() {
        if (animator.isRunning) {
            animator.reverse()
        } else {
            animator.start()
        }
    }

}