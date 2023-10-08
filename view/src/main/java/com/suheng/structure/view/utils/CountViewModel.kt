package com.suheng.structure.view.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.suheng.structure.view.ModelView

class CountViewModel : ModelView(), DefaultLifecycleObserver/*, LifecycleEventObserver*/ {

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    val mCountLive: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(10)
    }

    private var mRunnable: Runnable? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d("Wbj", "CountViewModel, onCreate")
        this.create()
    }

    fun create() {
        if (mRunnable == null) {
            mRunnable = object : Runnable {
                override fun run() {
                    mCountLive.value = mCountLive.value?.plus(1)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (mHandler.hasCallbacks(this)) {
                            mHandler.removeCallbacks(this)
                        }
                    } else {
                        mHandler.removeCallbacks(this)
                    }
                    Log.d("Wbj", "CountViewModel: " + mCountLive.value)
                    mHandler.postDelayed(this, 1000)
                }
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("Wbj", "CountViewModel, onStart")
        this.start()
    }

    private fun start() {
        mRunnable?.let {
            mHandler.postDelayed(it, 1000)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("Wbj", "CountViewModel, onStop")
        this.stop()
    }

    private fun stop() {
        mRunnable?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (mHandler.hasCallbacks(it)) {
                    mHandler.removeCallbacks(it)
                }
            } else {
                mHandler.removeCallbacks(it)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Log.d("Wbj", "CountViewModel, onDestroy")
        this.destroy()
    }

    private fun destroy() {
        mRunnable = null
    }

    /*override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d("Wbj", "Event = $event, LifecycleOwner = $source")
    }*/

}