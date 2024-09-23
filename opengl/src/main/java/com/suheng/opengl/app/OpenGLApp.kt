package com.suheng.opengl.app

import android.app.Application
import kotlin.properties.Delegates

class OpenGLApp : Application() {

    companion object {
        private var appCxt: OpenGLApp by Delegates.notNull()
        fun getInstance(): OpenGLApp = appCxt
    }

    override fun onCreate() {
        super.onCreate()
        appCxt = this
    }

}