package com.suheng.structure.view.kt.generic

import com.suheng.structure.view.kt.Polygon2

interface List2<T> { //泛型类
    fun get(index: Int): T
}

fun <T> lastElement(list: List2<T>): T = list.get(0) //泛型方法

interface List3<T : Polygon2> { //泛型约束
    fun get(index: Int): T
}