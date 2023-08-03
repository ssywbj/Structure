package com.suheng.structure.view.kt.generic

interface Production<out T> {
    fun produce(): T
}

