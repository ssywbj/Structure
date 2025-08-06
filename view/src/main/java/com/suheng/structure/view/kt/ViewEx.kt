package com.suheng.structure.view.kt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

fun TextView.textChangedFlow(): Flow<String> = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //trySend(s)
            //Log.d("Wbj", "onTextChanged: s:$s, start: $start, before: $before, count: $count")
            trySendBlocking(s.toString())
        }

        override fun afterTextChanged(s: Editable) {
            //Log.i("Wbj", "afterTextChanged: s:$s")
        }
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}

fun View.onClickFlow() = callbackFlow {
    setOnClickListener { trySendBlocking(Unit) }
    awaitClose { setOnClickListener(null) }
}

fun View.getBound(dest: Rect) {
    val location = IntArray(2)
    getLocationOnScreen(location)
    val x = location[0]
    val y = location[1]
    val width = this.width
    val height = this.height
    Log.d("ViewKt", "x: $x, y: $y, width: $width, height: $height")
    dest.set(x, y, x + width, y + height)
}

fun View.getBound(): Rect {
    return Rect().also { getBound(it) }
}

fun View.toBitmap(scaleFactor: Float = 1f): Bitmap? {
    val width = this.width
    val height = this.height
    if (width <= 0 || height <= 0 || scaleFactor <= 0f) {
        Log.w("ViewKt", "width: $width, height: $height, scaleFactor: $scaleFactor")
        return null
    }
    val bmpWidth = (width / scaleFactor).toInt()
    val bmpHeight = (height / scaleFactor).toInt()
    val scale = 1 / scaleFactor
    if (bmpWidth <= 0 || bmpHeight <= 0) {
        Log.w("ViewKt", "bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
        return null
    }
    Log.d("ViewKt", "width: $width, height: $height, bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")

    val bitmap = createBitmap(bmpWidth, bmpHeight)
    val canvas = Canvas(bitmap)
    canvas.scale(scale, scale)
    draw(canvas)
    return bitmap
}

fun View.areaBitmap(area: Rect, scaleFactor: Float = 1f): Bitmap? {
    val full = this.getBound()
    Log.d("ViewKt", "before setIntersect, area: $area")
    if (area.setIntersect(full, area)) {
        Log.d("ViewKt", " after setIntersect, area: $area")
        if (scaleFactor == 0f) {
            Log.e("ViewKt", "scaleFactor is zero")
            return null
        }
        val bmpWidth = (area.width() / scaleFactor).toInt()
        val bmpHeight = (area.height() / scaleFactor).toInt()
        if (bmpWidth <= 0 || bmpHeight <= 0) {
            Log.e("ViewKt", "bmpWidth: $bmpWidth, bmpHeight: $bmpHeight")
            return null
        }

        val dx = full.left - area.left
        val dy = full.top - area.top
        val sx = 1 / scaleFactor
        Log.d("ViewKt", "bmpWidth: $bmpWidth, bmpHeight: $bmpHeight, dx: $dx, dy: $dy, sx: $sx")

        val bitmap = createBitmap(bmpWidth, bmpHeight)
        val canvas = Canvas(bitmap)
        canvas.scale(sx, sx)
        canvas.translate(dx.toFloat(), dy.toFloat())
        draw(canvas)
        return bitmap
    } else {
        Log.w("ViewKt", "not intersect area")
        return null
    }
}

fun View.intersectBitmap(intersectView: View, scaleFactor: Float = 1f): Bitmap? {
    return this.areaBitmap(intersectView.getBound(), scaleFactor)
}

fun Bitmap.clipArea(area: Rect, isRecycle: Boolean = false): Bitmap {
    val bitmap = Bitmap.createBitmap(this, area.left, area.top, area.width(), area.height())
    if (isRecycle && !isRecycled) {
        recycle()
    }
    return bitmap
}

inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), noOpHandler) as T
}

val noOpHandler = InvocationHandler { _, _, _ ->
}