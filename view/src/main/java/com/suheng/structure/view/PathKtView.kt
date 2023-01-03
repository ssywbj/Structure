package com.suheng.structure.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View

class PathKtView : View {
    private val mBitmap: Bitmap

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        //mBitmap = BitmapManager.get(context, drawable.vector_delete, R.color.colorPrimary)

        val drawable = Drawable.createFromXml(resources, resources.getXml(R.xml.vector_delete))
         mBitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(mBitmap)
        canvas.drawFilter = PaintFlagsDrawFilter(
            0,
            Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
        )
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
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
