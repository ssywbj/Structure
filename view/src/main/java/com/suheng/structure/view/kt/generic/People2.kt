package com.suheng.structure.view.kt.generic

import android.util.Log

class People2 : People() {
    override fun printName() {
        Log.d("Wbj", "this People2")
    }

    fun printName2() {
        Log.d("Wbj", "printName2 People2")
    }
}