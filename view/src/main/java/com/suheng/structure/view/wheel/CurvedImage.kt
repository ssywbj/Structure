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
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import com.suheng.structure.view.BitmapHelper
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.save

class CurvedImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = CurvedImage::class.java.simpleName
    }

    private val paintLine = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics)
    }
    private val camera = Camera()
    private val matrixCamera = Matrix()
    private val rect = RectF()
    private var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.girl_gaitubao)
    private var depthZ = 0f
    private val floatArray = FloatArray(9)
    private var displayDensity = 1f
    private val animator = ValueAnimator.ofFloat(0f, 180f, 0f).apply {
        duration = 1800
        interpolator = LinearInterpolator()
        addUpdateListener { animation ->
            (animation.animatedValue as? Float)?.let {
                depthZ = it
                invalidate()
            }
        }
    }
    private var centerX = 1f
    private var centerY = 1f
    private val rectTextIn: RectF = RectF()
    private val rectText: Rect = Rect()
    private val rectBitmap: RectF = RectF()
    private val bitmap7: Bitmap = BitmapHelper.get(getContext(), R.drawable.number_second_7, 0.4f)
    private val bitmap0: Bitmap = BitmapHelper.get(getContext(), R.drawable.number_second_0, 0.4f)
    private val bitmap70: Bitmap = BitmapHelper.mergeLeftRight(bitmap7, bitmap0)

    init {
        displayDensity = resources.displayMetrics.density
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        centerX = w / 2f
        centerY = h / 2f

        val rectDimen =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40f, resources.displayMetrics)
                .toInt()
        rectTextIn.left = 30f
        rectTextIn.top = 30f
        rectTextIn.right = rectTextIn.left + rectDimen
        rectTextIn.bottom = rectTextIn.top + rectDimen

        rectBitmap.left = rectTextIn.right
        rectBitmap.top = rectTextIn.top
        rectBitmap.right = rectBitmap.left + bitmap70.width.toFloat() + 30
        rectBitmap.bottom = rectBitmap.top + bitmap70.height.toFloat() + 30
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.CYAN)
        canvas.save {
            camera.save()
            camera.translate(0f, 0f, depthZ)
            camera.rotateY(depthZ)
            camera.getMatrix(matrixCamera)
            camera.restore()

            floatArray.also {
                matrixCamera.getValues(it)
                it[6] = it[6] / displayDensity //数值修正
                it[7] = it[7] / displayDensity //数值修正
                matrixCamera.setValues(it)
            }

            matrixCamera.preTranslate(-centerX, -centerY)
            matrixCamera.postTranslate(centerX, centerY)

            concat(matrixCamera)
            drawBitmap(bitmap, null, rect, null)
        }

        /*canvas.save {
            drawRect(rectTextIn, paintLine)
            paintLine.color = Color.BLACK
            val text = "48"
            paintLine.getTextBounds(text, 0, text.length, rectText)
            val textCenterX = rectTextIn.centerX().toFloat()
            val textCentreY =
                rectTextIn.top.toFloat() + rectText.height() + rectTextIn.height() / 2f
            translate(textCenterX, textCentreY)
            rotate(depthZ)
            drawText(text, -rectText.width() / 2f, -rectText.height() / 2f, paintLine)
        }*/

        canvas.save {
            drawRect(rectTextIn, paintLine)
            paintLine.color = Color.GRAY
            drawLine(
                rectTextIn.left,
                rectTextIn.centerY(),
                rectTextIn.right,
                rectTextIn.centerY(),
                paintLine
            )
            drawLine(
                rectTextIn.centerX(),
                rectTextIn.top,
                rectTextIn.centerX(),
                rectTextIn.bottom,
                paintLine
            )

            camera.save()
            camera.translate(0f, 0f, -depthZ)
            camera.rotateY(-depthZ)
            camera.getMatrix(matrixCamera)
            camera.restore()
            /*floatArray.also {
                matrixCamera.getValues(it)
                it[6] = it[6] / displayDensity //数值修正
                it[7] = it[7] / displayDensity //数值修正
                matrixCamera.setValues(it)
            }*/
            matrixCamera.preTranslate(-rectTextIn.centerX(), -rectTextIn.centerY())
            matrixCamera.postTranslate(rectTextIn.centerX(), rectTextIn.centerY())

            val text = "48"
            paintLine.getTextBounds(text, 0, text.length, rectText)
            matrixCamera.preTranslate(rectTextIn.centerX(), rectTextIn.centerY())
            concat(matrixCamera)
            paintLine.color = Color.BLACK
            drawText(
                text,
                -paintLine.measureText(text) / 2f,
                -(paintLine.descent() + paintLine.ascent()) / 2f,
                paintLine
            )
        }

        canvas.save {
            paintLine.color = Color.GRAY
            drawRect(rectBitmap, paintLine)
            paintLine.color = Color.RED
            drawLine(
                rectBitmap.left,
                rectBitmap.centerY(),
                rectBitmap.right,
                rectBitmap.centerY(),
                paintLine
            )
            drawLine(
                rectBitmap.centerX(),
                rectBitmap.top,
                rectBitmap.centerX(),
                rectBitmap.bottom,
                paintLine
            )

            camera.save()
            camera.translate(0f, 0f, depthZ)
            camera.rotateY(depthZ)
            camera.getMatrix(matrixCamera)
            camera.restore()
            floatArray.also {
                matrixCamera.getValues(it)
                it[6] = it[6] / displayDensity //数值修正
                it[7] = it[7] / displayDensity //数值修正
                matrixCamera.setValues(it)
            }
            matrixCamera.preTranslate(-rectBitmap.centerX(), -rectBitmap.centerY())
            matrixCamera.postTranslate(rectBitmap.centerX(), rectBitmap.centerY())

            matrixCamera.preTranslate(rectBitmap.centerX(), rectBitmap.centerY())
            concat(matrixCamera)
            drawBitmap(bitmap70, -bitmap70.width / 2f, -bitmap70.height / 2f, null)
        }

        canvas.save {
            paintLine.color = Color.RED
            drawLine(0f, centerY, width.toFloat(), centerY, paintLine)
            drawLine(centerX, 0f, centerX, height.toFloat(), paintLine)
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