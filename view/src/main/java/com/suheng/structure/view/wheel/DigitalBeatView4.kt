package com.suheng.structure.view.wheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.content.ContextCompat
import com.suheng.structure.view.R
import java.util.*
import kotlin.math.abs

class DigitalBeatView4 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = DigitalBeatView4::class.java.simpleName
        private const val SECOND_SCALES = 60 //秒刻度数
        private const val SECOND_NUMBERS_INSIDE = 5 //屏蔽内显示5个
        private const val SECOND_NUMBERS_OUTSIDE = 1 //屏蔽外两侧各显示1个
        private const val SECOND_MIDDLE_OFFSET =
            SECOND_NUMBERS_INSIDE / 2 + SECOND_NUMBERS_OUTSIDE //以中间刻度为基准，两侧显示的个数
    }

    private var secondWidth = 0
    private var offsetSecond = 0
    private var currentSecond = 0
    private var outsideOffsetX = 0
    private var bitmapManager: DigitalBeatBitmapManager
    private var scaleRatio = 0.0f
    private var itemPaddingHorizontal = 10 * 3f
    private var scroller: OverScroller
    private val listRect = ArrayList<Rect>(SECOND_NUMBERS_INSIDE)

    init {
        bitmapManager = DigitalBeatBitmapManager(context)
        currentSecond = Calendar.getInstance()[Calendar.SECOND]
        scroller = OverScroller(context, LinearInterpolator())
    }

    private var downX = 0
    private var scrolledIndex = -1
    private var offsetX = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                offsetX = 0
            }
            MotionEvent.ACTION_MOVE -> {
                offsetX = downX - x
                //Log.d(TAG, "offsetX: $offsetX")
                scrollTo(offsetX, 0)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scroller.startScroll(scrollX, 0, -offsetX, 0, 500)
                invalidate()
            }
        }

        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        val scrollOffset = scroller.computeScrollOffset()
        //Log.i(TAG, "computeScroll, finished: $finished, scrollOffset: $scrollOffset")
        if (scrollOffset) {
            val currX = scroller.currX //滚动中的水平方向相对于原点的偏移量，即当前的X坐标。
            scrollTo(currX, 0)
            invalidate()
        } else {
            //scrolledIndex = -1
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        secondWidth = w / SECOND_NUMBERS_INSIDE
        outsideOffsetX = secondWidth * SECOND_NUMBERS_OUTSIDE //减掉屏幕外数字的宽度，让数字从屏幕外开始绘制
        val secondAvailableWidth = secondWidth - itemPaddingHorizontal * 2
        ContextCompat.getDrawable(context, R.drawable.number_second_0)?.let {
            val originSecondWidth = it.intrinsicWidth * 2
            scaleRatio = secondAvailableWidth / originSecondWidth
            Log.d(
                TAG,
                "w: $w, h: $h, secondWidth: $secondWidth, secondAvailableWidth: $secondAvailableWidth" +
                        ", originSecondWidth: $originSecondWidth, scaleRatio: $scaleRatio, outsideOffsetX: $outsideOffsetX"
            )
        }

        listRect.clear()
        for (i in 0 until SECOND_NUMBERS_INSIDE) {
            listRect.add(Rect(i * secondWidth, 0, (i + 1) * secondWidth, h))
        }
        listRect.forEach { Log.d(TAG, "rect: ${it.toShortString()}") }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
        } else {
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
        //val scrollOffset =
        /*Log.e(
            TAG,
            "drawSeconds, isFinished: ${scroller.isFinished}, scrollOffset: $scrollOffset, scrollX: $scrollX"
        )*/

        var scrollIndex = -1
        for (rect in listRect) {
            if (rect.contains(abs(scrollX), rect.top)) {
                scrollIndex = listRect.indexOf(rect)
                //Log.i(TAG, "scrollX: $scrollX, scrollIndex: $scrollIndex")
                break
            }
        }
        if (scrollIndex != -1 && scrolledIndex != scrollIndex) {
            Log.i(
                TAG,
                "scrolledIndex: $scrolledIndex, scrollIndex: $scrollIndex, scrollX: $scrollX"
            )
            if (scrolledIndex != -1) {
                val units = scrollIndex - scrolledIndex
                if (scrollX < 0) {
                    currentSecond -= units
                    offsetSecond -= units * secondWidth
                } else {
                    currentSecond += units
                    offsetSecond += units * secondWidth
                }
                Log.w(
                    TAG,
                    "units: $units, currentSecond: $currentSecond, offsetSecond: $offsetSecond"
                )
            }
            scrolledIndex = scrollIndex
        }

        var offsetX = 0
        val startSecond = currentSecond - SECOND_MIDDLE_OFFSET
        val endSecond = currentSecond + SECOND_MIDDLE_OFFSET
        for (second in startSecond..endSecond) { //1.屏幕内显示5个，屏幕外两侧各显示一个，一共7个；2.当前秒数在中间，它的前后各有3个数字
            val number = (second + SECOND_SCALES) % SECOND_SCALES
            val bitmap =
                bitmapManager.getSecondBitmap(
                    number,
                    R.color.os_text_primary_color,
                    scaleRatio
                )
            canvas.drawBitmap(
                bitmap,
                -outsideOffsetX + offsetSecond + offsetX + itemPaddingHorizontal,
                0f,
                null
            )
            offsetX += secondWidth
        }
        /*Log.w(
            TAG,
            "drawSeconds: $sb, startSecond: $startSecond, endSecond: $endSecond, currentSecond: $currentSecond"
        )*/
    }

    private fun releaseAnim() {
        scroller.takeUnless { it.isFinished }?.abortAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseAnim()
    }

}