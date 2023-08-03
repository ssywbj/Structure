package com.suheng.structure.view.kt.generic

interface Consumer<in T> {
    fun consume(item: T)
}

