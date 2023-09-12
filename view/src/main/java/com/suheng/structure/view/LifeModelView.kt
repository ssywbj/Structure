package com.suheng.structure.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.*

open class LifeModelView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr), LifecycleOwner {

    protected var viewModel: ModelView? = null

    //private val mRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val mRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mRegistry.currentState = Lifecycle.State.CREATED
        //viewModel?.onAttachedToWindow()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } else {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        viewModel?.onVisibilityAggregated(isVisible)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRegistry.currentState = Lifecycle.State.DESTROYED
        //viewModel?.onDetachedFromWindow()
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }

    protected inline fun <reified T : ModelView> viewModel(): T? {
        if (viewModel == null) {
            viewModel = ViewTreeViewModelStoreOwner.get(this)?.let {
                ViewModelProvider(it)[T::class.java]
            }
        }
        return viewModel as T?
    }

}