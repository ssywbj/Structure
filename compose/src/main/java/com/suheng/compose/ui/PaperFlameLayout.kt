package com.suheng.compose.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout

class PaperFlameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)/*, AttachedSurfaceControl*/ {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("Wbj", "onAttachedToWindow")
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        Log.d("Wbj", "draw")
    }

    /*override fun buildReparentTransaction(child: SurfaceControl): SurfaceControl.Transaction? {
        return SurfaceControl.Transaction().setLayer(child, 0)
    }

    override fun applyTransactionOnDraw(t: SurfaceControl.Transaction): Boolean {
        return true
    }*/

}