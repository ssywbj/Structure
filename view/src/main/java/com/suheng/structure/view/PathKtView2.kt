package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.PathParser
import com.suheng.structure.view.kt.NAME_SPACE1
import org.xmlpull.v1.XmlPullParser

class PathKtView2 : View {
    companion object {
        private const val NAME_SPACE = "http://schemas.android.com/apk/res/android"
    }

    object Singleton {
        const val NAME_SPACE2 = "http://schemas.android.com/apk/res/android"
        const val TAG = "PathKtView2"
    }

    private val mBitmap: Bitmap
    private lateinit var mPath: Path
    private val mPaint: Paint
    private var mScaleX = 1.0f
    private var mScaleY = 1.0f

    private lateinit var mViewportWidth: String
    private lateinit var mViewportHeight: String

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mBitmap = BitmapManager.get(context, R.drawable.vector_delete, R.color.colorPrimary)

        /*val drawable = Drawable.createFromXml(resources, resources.getXml(R.xml.vector_delete))
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
        drawable.draw(canvas)*/

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        val parser = resources.getXml(R.xml.vector_delete)
        //mPath = PathParser.createPathFromPathData(parser.getAttributeValue(NAME_SPACE, "pathData"))

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                if ("vector" == tagName) {
                    val width = parser.getAttributeValue(NAME_SPACE, "width")
                    val height = parser.getAttributeValue(Singleton.NAME_SPACE2, "height")
                    mViewportWidth =
                        parser.getAttributeValue(NAME_SPACE, "viewportWidth")
                    mViewportHeight =
                        parser.getAttributeValue(NAME_SPACE, "viewportHeight")
                    Log.d(
                        Singleton.TAG,
                        "vector: width = $width, height = $height, viewportWidth = $mViewportWidth, viewportHeight = $mViewportHeight"
                    )
                }

                if ("path" == tagName) {
                    mPaint.color =
                        Color.parseColor(parser.getAttributeValue(NAME_SPACE, "fillColor"))

                    mPath = PathParser.createPathFromPathData(
                        parser.getAttributeValue(NAME_SPACE1, "pathData")
                    )
                }
            }

            parser.next()
        }

        parser.close()
    }

    private var mWidthMode: Int = 0
    private var mHeightMode: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = if (mWidthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            144
        }

        val height = if (mHeightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            144
        }

        Log.v(
            Singleton.TAG,
            "width = $width, height = $height, mWidthMode = $mWidthMode, mHeightMode = $mHeightMode"
        )
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScaleX =
            if (mHeightMode == MeasureSpec.UNSPECIFIED) 2f else w.toFloat() / mViewportWidth.toFloat()
        mScaleY =
            if (mWidthMode == MeasureSpec.UNSPECIFIED) 2f else h.toFloat() / mViewportHeight.toFloat()
        Log.v(
            Singleton.TAG,
            "onSizeChanged: w = $w, h = $h, oldw = $oldw, oldh = $oldh, mScaleX = $mScaleX, mScaleY = $mScaleY"
        )

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
        //canvas.drawBitmap(mBitmap, null, mRect, null) //绘制位图当宽高缩放时有锯齿
        canvas.save()
        canvas.scale(mScaleX, mScaleY)
        canvas.drawPath(mPath, mPaint) //绘制路径当宽高缩放时无锯齿
        canvas.restore()
    }

}
