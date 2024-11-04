package com.suheng.opengl

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Proxy


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