package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View

class PathKtView : View {
    private val mBitmap: Bitmap
    private val mRect = Rect()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        //mBitmap = BitmapManager.get(context, R.drawable.vector_delete, R.color.colorPrimary)

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
        //Log.v("Wbj", "mBitmap.width = ${mBitmap.width}, mBitmap.height = ${mBitmap.height}")
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        Log.v(
            "PathKtView",
            "widthMeasureSpec = $widthMeasureSpec, heightMeasureSpec = $heightMeasureSpec," +
                    " widthMode = $widthMode, heightMode = $heightMode"
        )
        Log.v(
            "PathKtView",
            "MeasureSpec.AT_MOST = ${MeasureSpec.AT_MOST}, MeasureSpec.EXACTLY = ${MeasureSpec.EXACTLY}," +
                    " MeasureSpec.UNSPECIFIED = ${MeasureSpec.UNSPECIFIED}"
        )

        /*if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mBitmap.width, mBitmap.height) //固定宽高
        }*/

        var width = 0
        if (widthMode == MeasureSpec.AT_MOST) { //wrap_content
            width = mBitmap.width
        } else if (widthMode == MeasureSpec.EXACTLY) { //xxdp、match_parent
            width = MeasureSpec.getSize(widthMeasureSpec)
        }

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            //mBitmap.height
            if (heightMode == MeasureSpec.AT_MOST) {
                mBitmap.height
            } else {
                MeasureSpec.getSize(heightMeasureSpec)
            }
        }

        Log.v("PathKtView", "width = $width, height = $height")
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRect.set(0, 0, w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawBitmap(mBitmap, 0f, 0f, null)
        canvas.drawBitmap(mBitmap, null, mRect, null)
    }

}
