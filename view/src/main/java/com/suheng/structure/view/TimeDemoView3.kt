package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.Observer
import com.suheng.structure.view.utils.CountViewModel
import java.util.*

class TimeDemoView3 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LifeModelView(context, attrs, defStyleAttr) {

    private var mViewModel: CountViewModel? = null

    private val mPaint = Paint()
    private val mRectSmall = Rect()

    private val mPaint3 by lazy {
        Paint().apply {
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            color = Color.RED
        }
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.typeface = Typeface.DEFAULT_BOLD
        mPaint.color = Color.BLACK
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mViewModel = modelView<CountViewModel>()?.also {
            //it.create()
            lifecycle.addObserver(it)
            Log.v("Wbj", "CountViewModel lifecycle: $lifecycle, ViewModel: $it")
        }
    }

    private val observer = Observer<Int> {
        invalidate()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            mViewModel?.let {
                it.mCountLive.observe(this, observer)
                //it.start()
            }
        } else {
            mViewModel?.let {
                it.mCountLive.removeObserver(observer)
                //it.stop()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.v("Wbj", "CountViewModel: $mViewModel")
        mViewModel?.let {
            //it.destroy()
            lifecycle.removeObserver(it)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaint.textSize = 40f
        val text = "88"
        val rect = Rect()
        mPaint.getTextBounds(text, 0, text.length, rect)
        val left = ((w - rect.width()) / 2f).toInt()
        var top = 15
        mRectSmall[left, top, left + rect.width()] = rect.height().let { top += it; top }
        mPaint3.textSize = 60f
        mPaint3.getTextBounds(text, 0, text.length, rect)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val second = Calendar.getInstance().get(Calendar.SECOND);
        //val second: Int = mViewModel.mCountLive.getValue()
        //val text = "Text"
        val text = mViewModel?.mCountLive?.value.toString()
        //mPaint.textSize = 40f
        mPaint3.textSize = 40f
        canvas.drawText(text, mRectSmall.left.toFloat(), mRectSmall.bottom.toFloat(), mPaint3)
    }

}