package com.suheng.structure.view.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountViewModel : ViewModel() {

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    val mCountLive: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(10)
    }

    private val mRunnable = object : Runnable {
        override fun run() {
            mCountLive.value = mCountLive.value?.plus(1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (mHandler.hasCallbacks(this)) {
                    mHandler.removeCallbacks(this)
                }
            } else {
                mHandler.removeCallbacks(this)
            }
            mHandler.postDelayed(this, 1000)
        }
    }

    fun startObserver() {
        mHandler.postDelayed(mRunnable, 1000)
    }

}