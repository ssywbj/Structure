package com.suheng.structure.view.kt

//扩展函数
fun String.lastTwoChar(): String {
    return substring(length - 2)
}

//扩展属性
val String.lastChar: Char get() = get(length - 1)
var String.lastChar2: Char
    get() = get(length - 1)
    set(value) {}