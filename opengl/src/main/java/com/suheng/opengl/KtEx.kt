package com.suheng.opengl

import android.opengl.GLES20
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Proxy
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.CharBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
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

fun Buffer.captureBytes() = when (this) {
    is DoubleBuffer, is LongBuffer -> 8
    is FloatBuffer, is IntBuffer -> 4
    is ShortBuffer, is CharBuffer -> 2
    else -> 1
}

fun Buffer.limitSize() = limit() * captureBytes()

fun Buffer.glBufferData(target: Int, usage: Int) = GLES20.glBufferData(
    target, limitSize(), this, usage
)

fun glBufferData(target: Int, usage: Int, vararg buffers: Buffer) {
    var offset = 0
    val pairs = mutableListOf<Pair<Int, Int>>()
    val size = buffers.sumOf {
        it.limitSize().apply {
            pairs.add(offset to this)
            offset = this
        }
    }
    GLES20.glBufferData(target, size, null, usage)
    buffers.forEachIndexed { index, buffer ->
        pairs[index].let { (offset, size) ->
            GLES20.glBufferSubData(target, offset, size, buffer)
        }
    }
}