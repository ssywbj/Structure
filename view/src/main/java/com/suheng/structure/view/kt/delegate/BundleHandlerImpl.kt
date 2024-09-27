package com.suheng.structure.view.kt.delegate

import android.app.Activity
import android.os.Bundle
import android.util.Log

class BundleHandlerImpl : BundleHandler {
    companion object {
        const val TAG = "BundleHandler"
    }

    private fun bundleAdd(a: Int, b: Int): Int = a + b

    override val bundleSum: (Int, Int) -> Int = ::bundleAdd

    override fun bundleHandler(activity: Activity, bundle: Bundle?) {
        Log.d(TAG, "bundle: $bundle, activity: $activity")
    }
}