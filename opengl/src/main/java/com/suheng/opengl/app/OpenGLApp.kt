package com.suheng.opengl.app

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.suheng.opengl.noOpDelegate
import kotlin.properties.Delegates

class OpenGLApp : Application() {

    companion object {
        private const val TAG: String = "OpenGLApp"
        private var appCxt: OpenGLApp by Delegates.notNull()
        fun getInstance(): OpenGLApp = appCxt
    }

    override fun onCreate() {
        super.onCreate()
        appCxt = this
        registerActivityLifecycleCallbacks(activityLifecycleCallback)
        registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                Log.v(
                    TAG, "onConfigurationChanged, orientation: ${newConfig.orientation}: " +
                            ", navigationHidden: ${newConfig.navigationHidden}, navigation: ${newConfig.navigation}"
                )
            }

            override fun onLowMemory() {
                Log.v(TAG, "onLowMemory")
            }
        })
    }

    private val activityLifecycleCallback = object : ActivityLifecycleCallbacks by noOpDelegate() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "onActivityCreated: $activity")
        }

        override fun onActivityStopped(activity: Activity) {
            Log.d(TAG, "onActivityStopped: $activity")
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onActivityDestroyed: $activity")
        }
    }

}