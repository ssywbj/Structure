package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.*
import com.suheng.structure.view.utils.CountViewModel
import java.util.*

class TimeDemoView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), LifecycleOwner {

    private var mViewModel: CountViewModel? = null

    private val mPaint = Paint()
    private val mRectSmall = Rect()

    init {
        mPaint.isAntiAlias = true
        mPaint.typeface = Typeface.DEFAULT_BOLD
        mPaint.color = Color.BLACK
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mRegistry.currentState = Lifecycle.State.CREATED
        mViewModel = findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it).get(CountViewModel::class.java)
        }

        mViewModel?.mCountLive?.observe(this) { invalidate() }
        mViewModel?.startObserver()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (visibility == VISIBLE) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRegistry.currentState = Lifecycle.State.DESTROYED
    }

    private fun View.findViewTreeViewModelStoreOwner(): ViewModelStoreOwner? =
        ViewTreeViewModelStoreOwner.get(this)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaint.textSize = 40f
        val text = "88"
        val rect = Rect()
        mPaint.getTextBounds(text, 0, text.length, rect)
        val left = ((w - rect.width()) / 2f).toInt()
        var top = 15
        mRectSmall[left, top, left + rect.width()] = rect.height().let { top += it; top }
        mPaint.textSize = 60f
        mPaint.getTextBounds(text, 0, text.length, rect)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val second = Calendar.getInstance().get(Calendar.SECOND);
        //val second: Int = mViewModel.mCountLive.getValue()
        //val text = "Text"
        val text = mViewModel?.mCountLive?.value.toString()
        mPaint.textSize = 40f
        canvas.drawText(text, mRectSmall.left.toFloat(), mRectSmall.bottom.toFloat(), mPaint)
    }

    private val mRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }

}