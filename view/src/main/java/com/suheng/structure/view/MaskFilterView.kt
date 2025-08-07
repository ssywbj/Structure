package com.suheng.structure.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.suheng.structure.view.drawable.SunlightDrawable

class MaskFilterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        background = SunlightDrawable(context)
        Log.i("Wbj", "onAttachedToWindow, background: $background")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i("Wbj", "onDetachedFromWindow")
        (background as? SunlightDrawable)?.cancel()
    }

}