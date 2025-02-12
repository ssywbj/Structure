package com.suheng.structure.view.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountViewModel : ViewModel() {

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    val mCountLive: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(10)
    }

    private var mRunnable: Runnable? = null

    init {
        this.create()
        this.start()
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

    private fun start() {
        mRunnable?.let {
            mHandler.postDelayed(it, 1000)
        }
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

    override fun onCleared() {
        super.onCleared()
        this.stop()
        mRunnable = null
    }

}