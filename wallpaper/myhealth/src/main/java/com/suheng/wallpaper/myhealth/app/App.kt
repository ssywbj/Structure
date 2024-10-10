package com.suheng.wallpaper.myhealth.app

import android.app.Application
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        private var instance: App by Delegates.notNull()
        fun appCtx(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}