package com.suheng.structure.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suheng.structure.view.R

class KotlinActivity : AppCompatActivity() {

    //顶层变量
    var aa = 11
    var s1 = "a is $aa" //字符串

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        this.main()
        println("sum = " + sum(3, 9))
        this.printSum(2, 46)
        this.printSum2(2, 3)

        //局部变量
        //定义常量：val，常量只能为其赋值一次
        val a: Int = 1
        //a = 6 //不能再次赋值
        val b = 2 //自动推断是"Int"类型
        val c: Int //如果没有初始值，则类型不能省略
        c = 3
        //定义变量：var
        var x: Int = 5
        x = 7
        x += 1

        println(s1)
        aa = 22
        val s2 = "${s1.replace("is", "was")}, but now is $aa"
        println(s2)

        println("max of $x and $aa is ${this.maxOf2(x, aa)}")

        printProduct("3", "5")
        printProduct("", "6")
    }

    fun main() {
        println("hello world!")
        Log.d("Wbj", "---hello world!")
    }

    private fun sum(a: Int, b: Int) = a + b //自动推断返回类型是"Int"

    private fun printSum(a: Int, b: Int): Unit { //无返回值
        println("sum of $a and $b is ${a + b}")
    }

    private fun printSum2(a: Int, b: Int) { //无返回值，简写
        println("sum of $a and $b is ${sum(a, b)}")
    }

    //条件表达式
    private fun maxOf(a: Int, b: Int): Int {
        return if (a > b) {
            a
        } else {
            b
        }
    }

    //条件表达式，简写
    private fun maxOf2(a: Int, b: Int) = if (a > b) a else b

    private fun parseInt(str: String): Int? { //返回值可空
        if (str.isEmpty()) {
            return null
        }

        return str.toInt()
    }

    private fun printProduct(str1: String, str2: String) {
        val x = this.parseInt(str1)
        val y = this.parseInt(str2)

        //println(x * y)　//直接使用`x * y`会导致编译错误，因为它们可能为null
        if (x != null && y != null) { //先使用非空再使用
            println(x * y)
        } else {
            println("$str1 is $x, $str2 is $y")
        }
        println("another use null value: ${x!! * y!!}") //或这么使用，当为它们空时不执行
    }

}