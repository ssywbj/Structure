package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View

class PathKtView : View {
    private object Singleton {
        const val TAG = "PathKtView"
    }

    private val mBitmap: Bitmap
    private val mRect = Rect()
    private var mHeightMode: Int = 0

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
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        Log.v(
            Singleton.TAG,
            "widthMeasureSpec = $widthMeasureSpec, heightMeasureSpec = $heightMeasureSpec," +
                    " widthMode = $widthMode, heightMode = $mHeightMode"
        )
        /*Log.v(
            Singleton.TAG,
            "MeasureSpec.AT_MOST = ${MeasureSpec.AT_MOST}, MeasureSpec.EXACTLY = ${MeasureSpec.EXACTLY}," +
                    " MeasureSpec.UNSPECIFIED = ${MeasureSpec.UNSPECIFIED}"
        )*/

        //测量模式AT_MOST、EXACTLY、UNSPECIFIED，取决于其所在的父布局中对子View测量模式的定义
        var width = 0
        if (widthMode == MeasureSpec.AT_MOST) { //wrap_content
            width = mBitmap.width
        } else if (widthMode == MeasureSpec.EXACTLY) { //xxdp、match_parent
            width = MeasureSpec.getSize(widthMeasureSpec)
        }

        val height: Int = if (mHeightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            mBitmap.height
            /*if (heightMode == MeasureSpec.AT_MOST) {
                mBitmap.height
            } else {
                //MeasureSpec.UNSPECIFIED：位于NestedScrollView、RecyclerView等可以上下滑动的ViewGroup中，
                //因为可以上下滑动以展示全部的内容，所以不再限制子View的高度，让子View自己计算出相应的高度
                MeasureSpec.getSize(heightMeasureSpec)
            }*/
        }

        Log.v(Singleton.TAG, "width = $width, height = $height")
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.v(Singleton.TAG, "w = $w, h = $h, oldw = $oldw, oldh = $oldh")
        if (mHeightMode == MeasureSpec.UNSPECIFIED) {
            mRect.set(0, 0, mBitmap.width, h)
        } else {
            mRect.set(0, 0, w, h)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.drawBitmap(mBitmap, null, mRect, null)
        canvas.restore()
    }

}
