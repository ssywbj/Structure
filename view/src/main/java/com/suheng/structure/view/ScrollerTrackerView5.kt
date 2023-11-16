package com.suheng.structure.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import com.suheng.structure.view.kt.save
import kotlin.math.abs

class ScrollerTrackerView5 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = ScrollerTrackerView5::class.java.simpleName
    }

    private var index = 0
    private var offsetX = 0
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            22f,
            resources.displayMetrics
        )
    }
    private var downX = 0
    private var maximumVelocity = 0
    private var minimumVelocity = 0
    private var scroller: OverScroller
    private var velocityTracker: VelocityTracker

    init {
        setBackgroundColor(Color.GRAY)

        scroller = OverScroller(context)

        //https://juejin.cn/post/6844903791066628110
        //https://blog.csdn.net/shensky711/article/details/115624628
        //https://www.paonet.com/a/7777777777777702145
        velocityTracker = VelocityTracker.obtain()

        val viewConfiguration = ViewConfiguration.get(context)
        maximumVelocity = viewConfiguration.scaledMaximumFlingVelocity //滑动的最大速度
        minimumVelocity = viewConfiguration.scaledMinimumFlingVelocity //滑动的最小速度
        Log.v(
            TAG, "maximumVelocity：" + maximumVelocity + ", minimumVelocity: "
                    + minimumVelocity + ", scaledTouchSlop: " + viewConfiguration.scaledTouchSlop
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas, (index - 1) * width, index - 1)
        drawText(canvas, index * width, index)
        drawText(canvas, (index + 1) * width, index + 1)
    }

    private fun drawText(canvas: Canvas, startX: Int, index: Int) {
        canvas.save {
            canvas.translate(startX.toFloat(), 0f)
            val text = "页面：$index"
            canvas.drawText(text, 10f, 100f, mPaint)
            Log.w(TAG, "drawText, startX：$startX, index: $index")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        velocityTracker.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> downX = x
            MotionEvent.ACTION_MOVE -> this.smoothScrollTo(downX - x + offsetX)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //计算速度
                //velocityTracker.computeCurrentVelocity(1000)
                velocityTracker.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                val xVelocity = velocityTracker.xVelocity.toInt()
                Log.i(TAG, "up, fling xVelocity：$xVelocity, width: $width")

                //if (abs(xVelocity / 1000) > minimumVelocity) {
                //val abs = abs(xVelocity)
                if (abs(xVelocity) > 10000) {
                    val i = xVelocity / width
                    index = if (xVelocity > 0) i else -i
                    offsetX = index * width
                    Log.w(TAG, "up, scroller fling")
                    scroller.fling(scrollX, 0, xVelocity, 0, 0, Int.MAX_VALUE, 0, 0)
                    //smoothScrollTo(offsetX, true)
                } else {
                    val dux = downX - x
                    val isChangePage = abs(dux) >= width / 4 //判断抬手时可以换页的条件：滑动的距离达到视图1/4的宽度
                    if (isChangePage) { //切换页面
                        if (dux > 0) { //往左滑，滑出右侧界面
                            index++
                        }
                        if (dux < 0) { //往右滑，滑出左侧界面
                            index--
                        }
                    }
                    offsetX = index * width
                    Log.v(
                        TAG,
                        "up, index: $index, width: $width, dux: $dux, isChangePage: $isChangePage"
                    )
                    smoothScrollTo(offsetX, true)
                }

                velocityTracker.clear() //清除监视器事件
            }
        }

        return true
    }

    private fun smoothScrollTo(offsetX: Int, isUp: Boolean = false) {
        val currX = scroller.currX
        val startX = scroller.finalX
        val dx = offsetX - startX
        //if (isUp) {
        Log.v(TAG, "smoothScrollTo, offsetX: $offsetX, startX: $startX, dx: $dx, currX: $currX")
        //}

        //startX：开始点的x坐标；startY：开始点的y坐标；dx：水平方向的偏移量，正数会将内容向左滚动；dy：垂直方向的偏移量，正数会将内容向上滚动。
        scroller.startScroll(startX, 0, dx, 0, 500)

        invalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        val finished = scroller.isFinished
        //计算滚动中的新坐标，会配合着getCurrX()和getCurrY()。如果返回true，说明动画未完成；若返回false，说明动画已经完成或是被终止。
        val scrollOffset = scroller.computeScrollOffset()
        //Log.i(TAG, "computeScroll, finished: $finished, scrollOffset: $scrollOffset")
        if (scrollOffset) {
            val currX = scroller.currX //滚动中的水平方向相对于原点的偏移量，即当前的X坐标。
            val finalX = scroller.finalX //最终滚动到的X坐标，最终是Scroller.getCurrX()=Scroller.getFinalX()
            scrollTo(currX, 0)
            invalidate()
            Log.d(TAG, "computeScroll, currX: $currX, finalX: $finalX")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scroller.takeUnless { it.isFinished }?.abortAnimation()
        velocityTracker.clear()
        velocityTracker.recycle()
    }

}