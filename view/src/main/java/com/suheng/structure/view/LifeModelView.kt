package com.suheng.structure.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.*

open class LifeModelView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr), LifecycleOwner {

    protected var modelView: ModelView? = null

    //private val mRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val mRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mRegistry.currentState = Lifecycle.State.CREATED
        //mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
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
        modelView?.onVisibilityAggregated(isVisible)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRegistry.currentState = Lifecycle.State.DESTROYED
        //mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        modelView?.onDetached()
        Log.v("Wbj", "modelView: $modelView")
        modelView = null
        Log.v("Wbj", "modelView: $modelView")
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }

    protected inline fun <reified T : ModelView> modelView(): T? {
        if (modelView == null) {
            modelView = ViewTreeViewModelStoreOwner.get(this)?.let {
                ViewModelProvider(it)[T::class.java]
            }?.also { it.onAttached() }
        }
        return modelView as T?
    }

}