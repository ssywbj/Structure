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
    private var mWidthMode: Int = 0

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
        //Log.v(Singleton.TAG, "mBitmap.width = ${mBitmap.width}, mBitmap.height = ${mBitmap.height}")
        mWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        Log.v(
            Singleton.TAG,
            "widthMeasureSpec = $widthMeasureSpec, heightMeasureSpec = $heightMeasureSpec," +
                    " widthMode = $mWidthMode, heightMode = $mHeightMode"
        )
        /*Log.v(
            Singleton.TAG,
            "MeasureSpec.AT_MOST = ${MeasureSpec.AT_MOST}, MeasureSpec.EXACTLY = ${MeasureSpec.EXACTLY}," +
                    " MeasureSpec.UNSPECIFIED = ${MeasureSpec.UNSPECIFIED}"
        )*/

        //测量模式AT_MOST、EXACTLY、UNSPECIFIED，取决于其所在的父布局中对子View测量模式的定义：
        //在LinearLayout、FrameLayout等不可以滑动的ViewGroup中，AT_MOST对应wrap_content，EXACTLY对应match_parent或xxdp；
        //在NestedScrollView、HorizontalScrollView、RecyclerView等可以滑动的ViewGroup中，不管设置的是wrap_content、match_parent
        //或xxdp，测量模式都是MeasureSpec.UNSPECIFIED（以下有说明原因）。
        val width: Int = if (mWidthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else { //MeasureSpec.AT_MOST、MeasureSpec.UNSPECIFIED
            mBitmap.width
            /*if (mWidthMode == MeasureSpec.AT_MOST) {
                mBitmap.width
            } else {
                //MeasureSpec.UNSPECIFIED：位于HorizontalScrollView、RecyclerView等可以左右滑动的ViewGroup中，
                //因为可以左右滑动以展示全部的内容（有多少都可以显示得下），所以不再限制子View的宽度，让子View自己计算出相应的值
                MeasureSpec.getSize(heightMeasureSpec) * 10
            }*/
        }

        val height: Int = if (mHeightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            mBitmap.height
            /*if (heightMode == MeasureSpec.AT_MOST) {
                mBitmap.height
            } else {
                //MeasureSpec.UNSPECIFIED：位于NestedScrollView、RecyclerView等可以上下滑动的ViewGroup中，
                //因为可以上下滑动以展示全部的内容（有多少都可以显示得下），所以不再限制子View的高度，让子View自己计算出相应的值
                MeasureSpec.getSize(heightMeasureSpec) * 10
            }*/
        }

        Log.v(Singleton.TAG, "width = $width, height = $height")
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.v(Singleton.TAG, "w = $w, h = $h, oldw = $oldw, oldh = $oldh")
        mRect.set(
            0, 0,
            if (mHeightMode == MeasureSpec.UNSPECIFIED) mBitmap.width else w,
            if (mWidthMode == MeasureSpec.UNSPECIFIED) mBitmap.height else h
        )

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.drawBitmap(mBitmap, null, mRect, null)
        canvas.restore()
    }

}
