package com.suheng.structure.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.PathParser
import com.suheng.structure.view.R.drawable
import com.suheng.structure.view.kt.NAME_SPACE1
import org.xmlpull.v1.XmlPullParser

class PathKtView2 : View {
    companion object {
        private const val NAME_SPACE = "http://schemas.android.com/apk/res/android"
    }

    object Singleton {
        const val NAME_SPACE2 = "http://schemas.android.com/apk/res/android"
    }

    private val mRect = Rect()
    private val mBitmap: Bitmap
    private lateinit var mPath: Path
    private val mPaint: Paint
    private var mScale = 1.0f

    private val mRectPath = RectF()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mBitmap = BitmapManager.get(context, drawable.vector_delete, R.color.colorPrimary)

        mPaint = Paint()
        //mPaint.color = ContextCompat.getColor(context, R.color.colorAccent)

        val parser = resources.getXml(R.xml.vector_delete)
        //mPath = PathParser.createPathFromPathData(parser.getAttributeValue(NAME_SPACE, "pathData"))

        var left = -1f
        var right = -1f
        var top = -1f
        var bottom = -1f

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                if ("vector" == tagName) {
                    val width = parser.getAttributeValue(NAME_SPACE, "width")
                    val height = parser.getAttributeValue(Singleton.NAME_SPACE2, "height")
                    val viewportWidth =
                        parser.getAttributeValue(NAME_SPACE, "viewportWidth")
                    val viewportHeight =
                        parser.getAttributeValue(NAME_SPACE, "viewportHeight")
                    Log.d(
                        "Wbj",
                        "vector: $width, $height, $viewportWidth, $viewportHeight"
                    )
                }

                if ("path" == tagName) {
                    mPaint.color =
                        Color.parseColor(parser.getAttributeValue(NAME_SPACE, "fillColor"))

                    mPath = PathParser.createPathFromPathData(
                        parser.getAttributeValue(NAME_SPACE1, "pathData")
                    )

                    mRectPath.setEmpty()
                    mPath.computeBounds(mRectPath, true)
                    left = if (left == -1f) mRectPath.left else left.coerceAtMost(mRectPath.left)
                    top = if (top == -1f) mRectPath.top else top.coerceAtMost(mRectPath.top)
                    right =
                        if (right == -1f) mRectPath.right else right.coerceAtLeast(mRectPath.right)
                    bottom =
                        if (bottom == -1f) mRectPath.bottom else bottom.coerceAtLeast(mRectPath.bottom)
                }
            }

            parser.next()
        }

        Log.v("Wbj", "rectF.toShortString() = ${mRectPath.toShortString()}")
        Log.v("Wbj", "left = $left, top = $top, right = $right, bottom = $bottom")

        parser.close()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /*mRect.left = -w / 2
        mRect.right = -mRect.left
        mRect.top = -h / 2
        mRect.right = -mRect.top*/
        mRect.set(0, 0, w, h)

        //mScale = w / 150f
        mScale = 2.38f
        //mScale = w / rectF.width()
        Log.v("Wbj", "onSizeChanged: w = $w, h = $h, oldw = $oldw, oldh = $oldh, mScale = $mScale")

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
        //canvas.drawBitmap(mBitmap, null, mRect, null)

        //canvas.translate(mRect.exactCenterX(), mRect.exactCenterY())
        //canvas.scale(mScale, mScale)
        canvas.drawPath(mPath, mPaint)
        canvas.restore()
    }

}
