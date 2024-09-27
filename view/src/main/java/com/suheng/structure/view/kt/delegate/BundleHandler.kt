package com.suheng.structure.view.kt.delegate

import android.app.Activity
import android.os.Bundle

interface BundleHandler {

    val bundleSum: (Int, Int) -> Int

    fun bundleHandler(activity: Activity, bundle: Bundle?)
}