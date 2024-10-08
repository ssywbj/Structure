package com.suheng.opengl

import android.app.ActivityManager
import android.content.Context
import android.util.Log
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

fun isHomeScreen(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningTasks = activityManager.getRunningTasks(1)
    if (runningTasks != null && runningTasks.isNotEmpty()) {
        val taskInfo = runningTasks[0]
        val componentName = taskInfo.topActivity
        Log.v("OpenGLWallpaper", "top topActivity: $componentName")
    }
    return false
}