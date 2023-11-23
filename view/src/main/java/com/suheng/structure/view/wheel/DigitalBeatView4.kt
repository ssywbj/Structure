package com.suheng.structure.view.wheel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.content.ContextCompat
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.save
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
        private const val START_SCALE = 0f
        private const val END_SCALE = 1f
    }

    private var secondWidth = 0
    private var itemHeight = 0
    private var offsetSecond = 0
    private var currentSecond = 0
    private var outsideOffsetX = 0
    private var bitmapManager: DigitalBeatBitmapManager
    private var scaleRatio = 0.0f
    private var itemPaddingHorizontal = 10 * 3f
    private var scroller: OverScroller
    private val listRect = ArrayList<Rect>(SECOND_NUMBERS_INSIDE)

    private var velocityTracker: VelocityTracker
    private var maximumVelocity = 0
    private var minimumVelocity = 0

    private var downX = 0
    private var scrolledIndex = -1
    private var offsetX = 0


    init {
        bitmapManager = DigitalBeatBitmapManager(context)
        currentSecond = Calendar.getInstance()[Calendar.SECOND]
        scroller = OverScroller(context, LinearInterpolator())

        velocityTracker = VelocityTracker.obtain()
        val viewConfiguration = ViewConfiguration.get(context)
        maximumVelocity = viewConfiguration.scaledMaximumFlingVelocity //滑动的最大速度
        minimumVelocity = viewConfiguration.scaledMinimumFlingVelocity //滑动的最小速度
        Log.v(
            TAG, "maximumVelocity：" + maximumVelocity + ", minimumVelocity: "
                    + minimumVelocity + ", scaledTouchSlop: " + viewConfiguration.scaledTouchSlop
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        secondWidth = w / SECOND_NUMBERS_INSIDE
        outsideOffsetX = secondWidth * SECOND_NUMBERS_OUTSIDE //减掉屏幕外数字的宽度，让数字从屏幕外开始绘制
        val secondAvailableWidth = secondWidth - itemPaddingHorizontal * 2
        ContextCompat.getDrawable(context, R.drawable.number_second_0)?.let {
            val originSecondWidth = it.intrinsicWidth * 2
            scaleRatio = secondAvailableWidth / originSecondWidth
            itemHeight = (it.intrinsicHeight * scaleRatio).toInt()
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseAnim()
        velocityTracker.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                offsetX = 0
                velocityTracker.addMovement(event)
            }
            MotionEvent.ACTION_MOVE -> {
                offsetX = downX - x
                //Log.d(TAG, "offsetX: $offsetX")
                scrollTo(offsetX + scroller.finalX, 0)
                velocityTracker.addMovement(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                val xVelocity = velocityTracker.xVelocity.toInt()
                velocityTracker.clear()
                Log.i(
                    TAG,
                    "offsetX: $offsetX, fling xVelocity：$xVelocity, width: $width, scrollX: $scrollX"
                )
                if (abs(xVelocity) > 5 * width) {
                    scroller.fling(scrollX, 0, -xVelocity, 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0)
                } else {
                    Log.w(TAG, "up, scrollX: $scrollX, finalX: ${scroller.finalX}")
                    scroller.startScroll(scrollX, 0, -offsetX, 0, 500)
                    invalidate()
                }
            }
        }

        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        val scrollOffset = scroller.computeScrollOffset()
        //Log.i(TAG, "computeScroll, finished: ${scroller.isFinished}, scrollOffset: $scrollOffset")
        if (scrollOffset) {
            val currX = scroller.currX //滚动中的水平方向相对于原点的偏移量，即当前的X坐标。
            /*Log.e(
                TAG,
                "computeScroll, currX: $currX, isFinished: ${scroller.isFinished}, computeScrollOffset: ${scroller.computeScrollOffset()}"
            )*/
            scrollTo(currX, 0)
            invalidate()

            /*if (scroller.isFinished) {
                scrollBy(secondWidth / 3, 0)
            }*/
        } else {
            //scrolledIndex = -1
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.CYAN)
        drawSeconds(canvas)
    }

    private fun drawSeconds(canvas: Canvas) {
        if (scroller.isFinished) {
            val scrollOffset = scroller.computeScrollOffset()
            Log.e(
                TAG,
                "drawSeconds, scrollOffset: $scrollOffset, scrollX: $scrollX, currVelocity: ${scroller.currVelocity}"
            )
        }

        var scrollIndex = -1
        for (rect in listRect) {
            val absScrollX = abs(scrollX)
            val xCoordinate = absScrollX % width
            if (rect.contains(xCoordinate, rect.top)) {
                val pages = absScrollX / width
                scrollIndex = listRect.indexOf(rect) + pages * SECOND_NUMBERS_INSIDE
                /*Log.i(
                    TAG,
                    "scrollX: $scrollX, xCoordinate: $xCoordinate, page: $pages, scrollIndex: $scrollIndex, scrolledIndex: $$scrolledIndex"
                )*/
                break
            }
        }

        if (scrollIndex != -1 && scrolledIndex != scrollIndex) {
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
                    "units: $units, currentSecond: $currentSecond, offsetSecond: $offsetSecond, scrollX: $scrollX"
                )
            }

            scrolledIndex = scrollIndex
        }

        var offsetX = 0
        currentSecond %= SECOND_SCALES
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

            canvas.save {
                val scaleRatio = 1f
                scale(1f, scaleRatio)
                //translate(0f, height / 2f / scaleRatio)
                drawBitmap(
                    bitmap,
                    -outsideOffsetX + offsetSecond + offsetX + itemPaddingHorizontal,
                    height / 2f / scaleRatio - itemHeight / 2f,
                    //-itemHeight / 2f,
                    null
                )
            }

            offsetX += secondWidth
        }

        canvas.save {
            translate(scrollX.toFloat(), 0f)
            drawLine(0f, height / 2f, width.toFloat(), height / 2f, paintLine)
            drawLine(width / 2f, 0f, width / 2f, height.toFloat(), paintLine)
        }
    }

    private val paintLine = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
    }

    //https://github.com/commandiron/WheelPickerCompose
    //https://github.com/open-android/WheelPicker
    //https://github.com/AigeStudio/WheelPicker

    //https://www.gcssloop.com/customview/Matrix_Basic.html
    //https://www.gcssloop.com/customview/Matrix_Method.html
    //https://www.gcssloop.com/customview/matrix-3d-camera.html
    //https://github.com/xanderwang/elasticity
    private fun releaseAnim() {
        scroller.takeUnless { it.isFinished }?.abortAnimation()
    }

}