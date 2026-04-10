package com.suheng.structure.view.kt.delegate

import android.app.Activity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope

interface BundleHandler {

    val bundleSum: (Int, Int) -> Int

    fun bundleHandler(activity: Activity, bundle: Bundle?)

    fun testSync(scope: CoroutineScope)

    fun testProdCus(scope: CoroutineScope)
}