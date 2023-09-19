package com.suheng.structure.view.kt.generic

import android.util.Log

class People3(var age: Int = 0) : People() {
    override fun printName() {
        Log.d("Wbj", "this People3")
    }

    fun printName3() {
        Log.d("Wbj", "printName3 People3")
    }

    //https://blog.51cto.com/zhaoyanjun/4048007
    operator fun plus(people: People3): People3 = People3(people.age + this.age) //重载加号
    operator fun compareTo(people: People3): Int = this.age.compareTo(people.age) //重载大于号：a.compareTo(b) > 0
}