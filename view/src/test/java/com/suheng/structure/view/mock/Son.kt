package com.suheng.structure.view.mock

object Son {

    var jj: Int = 10
    private var jjPrivate: Int = 20

    fun test5(): Int {
        return 5
    }

    @JvmStatic
    fun ok(): String {
        return "UtilKotlin.ok()"
    }

    private fun privateResult(): Int = 5
    private fun privateResult2(max: Int) = (0..max).random()

    fun publicResult2() = privateResult2(7)
    fun publicResult() = privateResult()

    fun add(a: Int, b: Int) = a + b
}