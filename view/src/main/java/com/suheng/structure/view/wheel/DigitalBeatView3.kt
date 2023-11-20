package com.suheng.structure.view.wheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.content.ContextCompat
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.save
import java.util.*

class DigitalBeatView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = DigitalBeatView3::class.java.simpleName
        private const val SECOND_SCALES = 60 //秒刻度数
        private const val SECOND_NUMBERS_INSIDE = 5 //屏蔽内显示5个
        private const val SECOND_NUMBERS_OUTSIDE = 1 //屏蔽外两侧各显示1个
        private const val SECOND_MIDDLE_OFFSET =
            SECOND_NUMBERS_INSIDE / 2 + SECOND_NUMBERS_OUTSIDE //以中间刻度为基准，两侧显示的个数
    }

    private var secondWidth = 0f
    private var offsetSecond = 0f
    private var currentSecond = 0
    private var bitmapManager: DigitalBeatBitmapManager
    private var scaleRatio = 0.0f
    private var itemPaddingHorizontal = 10 * 3f
    private var scroller: OverScroller

    init {
        bitmapManager = DigitalBeatBitmapManager(context)
        currentSecond = Calendar.getInstance()[Calendar.SECOND]
        scroller = OverScroller(context, LinearInterpolator())
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            val second = Calendar.getInstance()[Calendar.SECOND]
            Log.i(TAG, "second: $second, currentSecond: $currentSecond")
            if (second != currentSecond) {
                currentSecond = second
                offsetSecond -= secondWidth.toInt()
                startSecondAnim()
            }

            val delayMs = 1000 - System.currentTimeMillis() % 1000
            handler.postDelayed(this, delayMs)
        }
    }

    private fun startSecondAnim() {
        scroller.startScroll(scroller.finalX, 0, secondWidth.toInt(), 0, 500)
        invalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        val scrollOffset = scroller.computeScrollOffset()
        //Log.i(TAG, "computeScroll, finished: $finished, scrollOffset: $scrollOffset")
        if (scrollOffset) {
            val currX = scroller.currX //滚动中的水平方向相对于原点的偏移量，即当前的X坐标。
            scrollTo(currX, 0)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        secondWidth = (w / SECOND_NUMBERS_INSIDE).toFloat()
        val secondAvailableWidth = secondWidth - itemPaddingHorizontal * 2
        ContextCompat.getDrawable(context, R.drawable.number_second_0)?.let {
            val originSecondWidth = it.intrinsicWidth * 2
            scaleRatio = secondAvailableWidth / originSecondWidth
            /*Log.d(
                TAG,
                "w: $w, secondWidth: $secondWidth, secondAvailableWidth: $secondAvailableWidth, originSecondWidth: $originSecondWidth, scaleRatio: $scaleRatio"
            )*/
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            handler?.post(mRunnable)
        } else {
            handler?.removeCallbacks(mRunnable)
            releaseAnim()
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
            /*Log.w(
                TAG,
                "drawSeconds: $sb, startSecond: $startSecond, endSecond: $endSecond, currentSecond: $currentSecond"
            )*/
        }
    }

    private fun releaseAnim() {
        scroller.takeUnless { it.isFinished }?.abortAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseAnim()
        handler?.removeCallbacks(mRunnable)
    }

}