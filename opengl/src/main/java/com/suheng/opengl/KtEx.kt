package com.suheng.opengl

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Proxy
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer


inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass)) { _, _, _ -> } as T
}

fun countDownFlow(total: Int = Int.MAX_VALUE, timeMillis: Long = 1000) = flow {
    for (i in total downTo 0) {
        emit(i)
        delay(timeMillis)
    }
}

fun Any.identityHashCode() = System.identityHashCode(this)

fun ShortArray.asShortBuffer(): ShortBuffer =
    ByteBuffer.allocateDirect(size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        .put(this).apply { position(0) }

fun IntArray.asIntBuffer(): IntBuffer =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
        .put(this).apply { position(0) }

fun FloatArray.asFloatBuffer(): FloatBuffer =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        .put(this).apply { position(0) }