package com.suheng.structure.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.suheng.structure.view.R.drawable

class PathKtView : View {
    private val mBitmap: Bitmap

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mBitmap = BitmapManager.get(context, drawable.vector_delete, R.color.colorPrimary)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mBitmap.width, mBitmap.height)
        Log.v("Wbj", "mBitmap.width = ${mBitmap.width}, mBitmap.height = ${mBitmap.height}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
    }

}
