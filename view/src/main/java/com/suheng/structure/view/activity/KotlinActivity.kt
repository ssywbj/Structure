package com.suheng.structure.view.activity

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suheng.structure.view.R

class KotlinActivity : AppCompatActivity() {

    //顶层变量
    private var aa = 11
    private var s1 = "a is $aa" //字符串

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

        println("getStringLength: " + getStringLength("355"))
        println("getStringLength: " + getStringLength(9))
        println("getStringLength2: " + getStringLength2("19"))

        this.forWhile()

        describe(1)
        println("describe(4659697576999): ${describe(4659697576999)}")
        println("describe2(465576): ${describe2(465576)}")

        this.inOperator()

        this.demoCollection()
        this.demoClass()

        this.accustomUse()
        this.foo() //都使用默认值
        this.foo(b = "B") //b属性不使用属性值
        this.foo(45) //a属性不使用属性值：a位置参数列表的第一位，不用像b一样需要显示指定参数名称
        this.foo(4, "C") //a、b属性都不使用属性值
        this.filterList()
        this.mapDemo()
        println("lazy attr1: $lazyAttr")
        println("lazy attr1: $lazyAttr")
        this.lazyAttr2()
    }

    private fun main() {
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
        //println("another use null value: ${x!! * y!!}") //或这么使用，当它们为空时不执行。但好像有时候会报空指针异常？
    }

    //is运算符检测表达式是否某类型的实例。如果一个不可变的局部变量或属性已经判断出为某类型，那么检测后的分支中可以直接当作该类型使用，无需显式转换。
    private fun getStringLength(obj: Any): Int? {
        if (obj is String) {
            return obj.length //`obj`在该条件分支内自动转换成`String`
        }

        return null
    }

    private fun getStringLength2(obj: Any): Int? {
        if (obj !is String) {
            return null
        }

        return obj.length
    }

    private fun getStringLength3(obj: Any): Int? {
        if (obj is String && obj.length > 0) { //甚至`obj`在`&&`右边自动转换成`String`类型
            return obj.length
        }

        return null
    }

    private val items = listOf("apple", "banana", "kiwifruit")

    private fun forWhile() { //for、while表达式
        for (item in items) {
            println("for fruit: $item")
        }

        for (index in items.indices) {
            println("for fruit indices: $index, ${items[index]}")
        }

        var index = 0
        while (index < items.size) {
            println("while fruit $index, ${items[index]}")
            index++
        }
    }

    private fun describe(obj: Any): String = when (obj) { //when表达式
        1 -> { //代码块
            val i = 5;
            println("when obj $i")
            "One"
        }
        "Hello" -> "Greeting"
        is Long -> "Long"
        !is String -> "Not a string"
        else -> "Unknown" //
    }

    private fun describe2(obj: Any): String {
        return when (obj) {
            1 -> {
                val i = 5;
                println("when obj $i")
                "One"
            }
            "Hello" -> "Greeting"
            is Long -> "Long"
            !is String -> "Not a string"
            else -> "Unknown"
        }
    }

    private fun inOperator() { //区间运算符
        println("------fits in range------")
        val x = 10
        val y = 9
        if (x in 1..y + 1) {
            println("fits in range")
        }

        val list = listOf("a", "b", "c")
        val indicator = 3;
        if (indicator in list.indices) {
            println("for fruit indices: $indicator, ${list[indicator]}")
        } else {
            println("$indicator is out of items.indices")
        }
        if (-1 !in 0..list.lastIndex) {
            println("-1 is out of range")
        }

        for (k in 0..10 step 2) { //从0开始,最大到10（..，闭区间：包括10）,等差数列,差值为2
            print("$k ")
        }
        println()
        print("until: ")
        for (j in 0 until 10 step 2) { //从0开始,最大到10（until，半开区间：不包括10）,等差数列,差值为2
            print("$j ")
        }

        println()
        for (i in 9 downTo 0 step 3) { //从9开始,最小到0,每差数列,差值为-3
            print("$i, ")
        }
        println()
        println("-------fits in range-------")
    }

    private fun demoCollection() { //集合
        when {
            "orange" in items -> println("juicy")
            "apple" in items -> println("apple is fine too")
        }

        val fruits = listOf("banana", "avocado", "apple", "kiwifruit")
        //fruits.filter { it.startsWith("a") }.forEach { println(it) }
        //fruits.filter { it.startsWith("a") }.map { it.uppercase() }.forEach { println(it) }
        fruits.filter { it.startsWith("a") }.sortedBy { it }.map { it.uppercase() }
            .forEach { println(it) }
    }

    private lateinit var mRectF: RectF //lateinit含义：先定义，后面再初始化
    private val mRectF2 = RectF()

    private fun demoClass() { //类
        val rect = Rect(5, 2, 10, 4)
        mRectF = RectF(5f, 2f, 10f, 4f)
        mRectF2.set(5.1f, 2.1f, 10.1f, 4.1f)
        println("rect: $rect, rectF: ${mRectF.toShortString()}, rectF2: $mRectF2")
    }

    private fun accustomUse() {
        val customer = Customer("wbj", "123@qq.com")
        customer.name = "wbj123" //对于var定义的变量还有setter方法
        //customer.email = "456@qq.com"  //val定义的没有setter方法
        println("customer: $customer")
    }

    //习惯用法：start
    //创建DTOs（POJOs/POCOs）
    data class Customer(var name: String, val email: String)

    //函数的默认参数
    private fun foo(a: Int = 3, b: String = "A") {
        println("foo, a: $a, b: $b")
    }

    //过滤list
    private fun filterList() {
        val list = listOf(-1, 0, 1, 2, 3)
        val positives = list.filter { x -> x > 0 }
        print("filter list 1: ")
        for (i in positives) {
            print("$i ")
        }
        println()

        print("filter list 2: ")
        for (i in list.filter { it > 0 }) {
            print("$i ")
        }
        println()
    }

    //map
    private fun mapDemo() {
        //val map = mapOf("a" to 1, "b" to 2, "c" to 3) //key、value不可变，大小固定
        val map = mutableMapOf("a" to 1, "b" to 2, "c" to 3) //key、value均可变，大小不固定
        println("map, $map")

        println("map[\"a\"] = ${map["a"]}")
        map["a"] = 11
        println("map[\"a\"] = ${map["a"]}")

        for (entry in map) {
            println("map entry, key: ${entry.key}, value: ${entry.value}, key、value: $entry")
        }

        for ((k, v) in map) {
            println("map (k, v), key: $k, value: $v, key、value: $k、$v")
        }

        for (key in map.keys) {
            println("map key: $key")
        }

        for (value in map.values) {
            println("map value: $value")
        }
    }

    //延迟属性
    private val lazyAttr: String by lazy {
        println("lazy, lazy, lazy!")
        this.describe2("lazyAttr")
    }

    private fun lazyAttr2() {
        //延迟属性
        val lazyAttr2: String by lazy {
            println("computed!")
            "lazy attr2: lazyAttr2"
        }

        println(lazyAttr2)
        println(lazyAttr2)
    }
    //习惯用法：end
}