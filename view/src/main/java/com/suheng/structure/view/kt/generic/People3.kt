package com.suheng.structure.view.kt.generic

import android.util.Log

class People3 : People() {
    override fun printName() {
        Log.d("Wbj", "this People3")
    }

    fun printName3() {
        Log.d("Wbj", "printName3 People3")
    }
}