package com.suheng.structure.view.kt

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.reflect.KProperty

//扩展函数
fun String.lastTwoChar(): String {
    return substring(length - 2)
}

//扩展属性
val String.lastChar: Char get() = get(length - 1)
var String.lastChar2: Char
    get() = get(length - 1)
    set(value) {}

inline fun <T, R> with2(receiver: T, block: T.() -> R): R {
    return receiver.block()
}

inline fun <T, R> T.let2(block: (T) -> R): R {
    return block(this)
}

inline fun <T, R> T.run2(block: T.() -> R): R {
    return block()
}

inline fun <T> T.apply2(block: T.() -> Unit): T {
    block()
    return this
}

inline fun <T> T.also2(block: (T) -> Unit): T {
    block(this)
    return this
}

var formattedString: String = ""
operator fun Delegate.setValue(thisRef: Any, property: KProperty<*>, value: String) =
    format(this, value).also { formattedString = it }

operator fun Delegate.getValue(thisRef: Any, property: KProperty<*>) =
    formattedString + "-" + formattedString.length

inline fun Canvas.save(block: Canvas.() -> Unit) {
    save()
    block()
    restore()
}

inline fun Canvas.saveLayer(
    left: Float, top: Float, right: Float, bottom: Float, paint: Paint?,
    block: Canvas.() -> Unit
) {
    val saveLayer = saveLayer(left, top, right, bottom, paint)
    block()
    restoreToCount(saveLayer)
}

inline fun Canvas.saveLayer(bounds: RectF?, paint: Paint?, block: Canvas.() -> Unit) {
    val saveLayer = saveLayer(bounds, paint)
    block()
    restoreToCount(saveLayer)
}

inline fun Canvas.saveLayerAlpha(bounds: RectF?, alpha: Int, block: Canvas.() -> Unit) {
    val saveLayer = saveLayerAlpha(bounds, alpha)
    block()
    restoreToCount(saveLayer)
}

inline fun Canvas.saveLayerAlpha(
    left: Float, top: Float, right: Float, bottom: Float, alpha: Int,
    block: Canvas.() -> Unit
) {
    val saveLayer = saveLayerAlpha(left, top, right, bottom, alpha)
    block()
    restoreToCount(saveLayer)
}