package com.suheng.structure.view

import android.util.Log
import androidx.lifecycle.ViewModel

abstract class ModelView : ViewModel() {

    fun onAttached() {
        Log.v("Wbj", "mv, onAttached")
    }

    fun onVisibilityAggregated(isVisible: Boolean) {
        Log.d("Wbj", "mv, onVisibilityAggregated: $isVisible")
    }

    fun onDetached() {
        Log.d("Wbj", "mv, onDetached")
    }

}