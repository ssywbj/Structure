package com.suheng.structure.view.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.save
import java.util.*

class DigitalBeatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = DigitalBeatView::class.java.simpleName
        private const val SECOND_SCALES = 60 //秒刻度数
        private const val SECOND_NUMBERS_INSIDE = 5 //屏蔽内显示5个
        private const val SECOND_NUMBERS_OUTSIDE = 1 //屏蔽外两侧各显示1个
        private const val SECOND_MIDDLE_OFFSET =
            SECOND_NUMBERS_INSIDE / 2 + SECOND_NUMBERS_OUTSIDE //以中间刻度为基准，两侧显示的个数
    }

    private var secondWidth = 0f
    private var secondAnimator: ValueAnimator
    private var totalOffsetSecond = 0f
    private var offsetSecond = 0f
    private var currentSecond = 0
    private var bitmapManager: DigitalBeatBitmapManager
    private var scaleRatio = 0.0f
    private var itemPaddingHorizontal = 10 * 3f

    init {
        bitmapManager = DigitalBeatBitmapManager(context)
        currentSecond = Calendar.getInstance()[Calendar.SECOND]

        secondAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
            addUpdateListener { animation: ValueAnimator ->
                offsetSecond = animation.animatedValue as Float
                //Log.d(mTAG, "onAnimationUpdate, mAnimValueSecond: $mAnimValueSecond")
                invalidate()
            }
            duration = 500
            interpolator = LinearInterpolator()
        }
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            val currentSecond = Calendar.getInstance()[Calendar.SECOND]
            this@DigitalBeatView.currentSecond = currentSecond
            startSecondAnim()

            val delayMs = 1000 - System.currentTimeMillis() % 1000
            handler.postDelayed(this, delayMs)
        }
    }

    private fun startSecondAnim() {
        secondAnimator.setFloatValues(
            totalOffsetSecond,
            secondWidth.let { totalOffsetSecond += it; totalOffsetSecond })
        secondAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        secondWidth = (w / SECOND_NUMBERS_INSIDE).toFloat()
        val secondAvailableWidth = secondWidth - itemPaddingHorizontal * 2
        ContextCompat.getDrawable(context, R.drawable.number_second_0)?.let {
            val originSecondWidth = it.intrinsicWidth * 2
            scaleRatio = secondAvailableWidth / originSecondWidth
            Log.d(
                TAG,
                "w: $w, secondWidth: $secondWidth, secondAvailableWidth: $secondAvailableWidth, originSecondWidth: $originSecondWidth, scaleRatio: $scaleRatio"
            )
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            if (handler != null) {
                handler.post(mRunnable)
            }
        } else {
            if (handler != null) {
                handler.removeCallbacks(mRunnable)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.CYAN)
        drawSeconds(canvas)
    }

    private fun drawSeconds(canvas: Canvas) {
        canvas.save {
            translate(totalOffsetSecond, 0f)
            val startSecond = currentSecond - SECOND_MIDDLE_OFFSET
            val endSecond = currentSecond + SECOND_MIDDLE_OFFSET

            val sb = StringBuilder()
            var outsideOffsetX = -secondWidth * SECOND_NUMBERS_OUTSIDE //减掉屏幕外数字的宽度，让数字从屏幕外开始绘制
            for (second in startSecond..endSecond) { //1.屏幕内显示5个，屏幕外两侧各显示一个，一共7个；2.当前秒数在中间，它的前后各有3个数字
                val number = (second + SECOND_SCALES) % SECOND_SCALES
                sb.append(number).append(" ")
                val bitmap =
                    bitmapManager.getSecondBitmap(
                        number,
                        R.color.os_text_primary_color,
                        scaleRatio
                    )
                drawBitmap(
                    bitmap,
                    outsideOffsetX - offsetSecond + itemPaddingHorizontal,
                    0f,
                    null
                )
                outsideOffsetX += secondWidth
            }
            Log.d(TAG, "drawSeconds: $sb, startSecond: $startSecond,endSecond: $endSecond")
        }
    }

    private fun releaseAnim(animator: ValueAnimator?) {
        if (animator == null) {
            return
        }
        if (animator.isRunning) {
            animator.cancel()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (handler != null) {
            handler.removeCallbacks(mRunnable)
        }
        releaseAnim(secondAnimator)
    }

}