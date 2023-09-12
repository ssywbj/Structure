package com.suheng.structure.view

import androidx.lifecycle.ViewModel

abstract class ModelView : ViewModel() {

    fun onAttachedToWindow() {
    }

    fun onVisibilityAggregated(isVisible: Boolean) {
    }

    fun onDetachedFromWindow() {
    }

}