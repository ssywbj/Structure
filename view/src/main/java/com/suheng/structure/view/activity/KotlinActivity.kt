package com.suheng.structure.view.activity

import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.Derived2
import com.suheng.structure.view.kt.MapObject
import com.suheng.structure.view.kt.MyClass
import com.suheng.structure.view.kt.MyOtherClass
import com.suheng.structure.view.kt.MyOtherClass2
import com.suheng.structure.view.kt.Person
import com.suheng.structure.view.kt.Person6
import com.suheng.structure.view.kt.Square
import com.suheng.structure.view.kt.Square3
import com.suheng.structure.view.kt.Student4
import com.suheng.structure.view.kt.also2
import com.suheng.structure.view.kt.apply2
import com.suheng.structure.view.kt.generic.American
import com.suheng.structure.view.kt.generic.Burger
import com.suheng.structure.view.kt.generic.BurgerStore
import com.suheng.structure.view.kt.generic.Consumer
import com.suheng.structure.view.kt.generic.Everybody
import com.suheng.structure.view.kt.generic.FastFoodStore
import com.suheng.structure.view.kt.generic.Food
import com.suheng.structure.view.kt.generic.FoodStore
import com.suheng.structure.view.kt.generic.ModernPeople
import com.suheng.structure.view.kt.generic.People
import com.suheng.structure.view.kt.generic.People2
import com.suheng.structure.view.kt.generic.People3
import com.suheng.structure.view.kt.generic.Production
import com.suheng.structure.view.kt.lastChar
import com.suheng.structure.view.kt.lastChar2
import com.suheng.structure.view.kt.lastTwoChar
import com.suheng.structure.view.kt.let2
import com.suheng.structure.view.kt.run2
import com.suheng.structure.view.kt.with2
import kotlinx.android.synthetic.main.activity_kotlin.btnAsync
import kotlinx.android.synthetic.main.activity_kotlin.btnAsyncLazy
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

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

        Wei.age = 23
        println("Wei: ${Wei.age}, ${Wei.name}")

        this.ifNotNull()

        this.testTry()
        println("arrayOfMinusOnes(10): ${this.arrayOfMinusOnes(10)}")

        this.testTurtle()

        this.testApply()

        this.nullableAlso()

        //this.calcTaxes()

        val person = Person("Wbj")
        //person.name //name在主构造方法没有用var或val声明，是私有属性，外部访问不到
        //Person(person)
        Person("Wbj2", Person("11111"))

        val derived2 = Derived2("wbj", "world")
        println("derived2, name: ${derived2.name}, size: ${derived2.size}")
        //derived2.size
        //derived2.name
        derived2.draw()

        val square = Square("Wbj Square")
        square.draw()

        //this.coroutines2()
        //this.coroutines3()
        this.coroutines5()

        var operation = this.operation(2, 4, ::plus)
        Log.d("Wbj", "operation, plus: $operation")
        operation = this.operation(2, 4, ::minus)
        Log.d("Wbj", "operation, minus: $operation")
        operation = this.operation(3, 6) { num_a, num_b ->
            num_a + num_b
        }
        Log.d("Wbj", "operation, Lambda, plus: $operation")
        operation = this.operation(3, 6) { num_a, num_b ->
            num_a - num_b
        }
        Log.d("Wbj", "operation, Lambda, minus: $operation")

        Log.d("Wbj", "operation, funType: $funType")
        Log.d("Wbj", "operation, funType: ${funType(1, 4)}")
        funType = ::minus
        Log.d("Wbj", "operation, funType: $funType")
        Log.d("Wbj", "operation, funType: ${funType(1, 4)}")
        Log.d("Wbj", "operation, funType: $funType2")
        Log.d("Wbj", "operation, funType: ${funType2(11, 4)}")
        funType2 = { funA1: Int, funA2: Int -> funA1 - funA2 }
        Log.d("Wbj", "operation, funType: $funType2")
        Log.d("Wbj", "operation, funType: ${funType2(11, 4)}")

        val returnFunType = this.returnFunType(0)
        Log.d("Wbj", "operation, returnFunType: ${returnFunType(1, 4)}")
        Log.d("Wbj", "operation, returnFunType: ${this.returnFunType(1)(1, 4)}")

        intArrayOf(2, 1, 3).forEach(action2)
        intArrayOf(2, 1, 3).forEach(::printInt) //直接把自定义的printInt方法传进来
        intArrayOf(2, 1, 3).forEach(::println) //直接把标准库的println方法传进来
        intArrayOf(2, 1, 3).forEach(action)
        //对action简化2：如果Lambda表达式只有一个参数，那么可以直接用it来代替，并且不需要声明参数名
        intArrayOf(2, 1, 3).forEach({ Log.d("Wbj", "simple2 value, $it") })
        //对action简化3：如果Lambda参数是函数的最后一个参数，那么可以将Lambda表达式移到函数括号的外面
        intArrayOf(2, 1, 3).forEach() { Log.d("Wbj", "simple3 value, $it") }
        //对action简化4：如果Lambda表达式是函数的唯一一个参数，那么可以将函数的括号省略
        intArrayOf(2, 1, 3).forEach { Log.d("Wbj", "simple4 value, $it") }

        val production1: Production<Food> = FoodStore()
        production1.produce()
        val production2: Production<Food> = FastFoodStore()
        production2.produce()
        val production3: Production<Food> = BurgerStore()
        production3.produce()

        /*val production1: Production<Burger> = FoodStore() //Error
        val production2: Production<Burger> = FastFoodStore() //Error
        val production3: Production<Burger> = InOutBurger()*/

        val consumer1: Consumer<Burger> = Everybody()
        consumer1.consume(Burger())
        val consumer2: Consumer<Burger> = ModernPeople()
        consumer2.consume(Burger())
        val consumer3: Consumer<Burger> = American()
        consumer3.consume(Burger())

        /*val consumer1: Consumer<Food> = Everybody()
        val consumer2: Consumer<Food> = ModernPeople() //Error
        val consumer3: Consumer<Food> = American() //Error*/

        Log.d("Wbj", "lastTwoChar fun: ${"hello world!".lastTwoChar()}")
        Log.d("Wbj", "lastChar attr: ${"hello world!".lastChar}")
        Log.d("Wbj", "lastChar attr: ${"hello world!".lastChar2}")

        val takeIf = "hello world!".takeIf { it.startsWith("kko") }
        val takeUnless = "hello world!".takeUnless { it.startsWith("kko") }
        Log.d("Wbj", "takeIf: $takeIf, takeUnless: $takeUnless")
        repeat(3) {
            Log.d("Wbj", "repeat time: $it")
        }

        val str = "hello wo rld!"
        str.filter { !it.isWhitespace() }
            .let { Log.d("Wbj", "filter: $it") } //return string
        str.filter { !it.isWhitespace() }.groupBy { it }  //return map
            .let { Log.d("Wbj", "filter groupBy: $it") }
        str.filter { !it.isWhitespace() }.groupBy { it }
            .map { it.key to it.value.size } //return list
            .let { Log.d("Wbj", "filter groupBy map: $it, element 2: ${it[1]}") }
        str.filter { !it.isWhitespace() }.toList().sorted().groupBy { it }
            .map { it.key to it.value.size }
            .let { Log.d("Wbj", "filter toList sorted groupBy, map: $it, element 2: ${it[1]}") }
        str.toSortedSet().let { Log.d("Wbj", "toSortedSet: $it") }

        people3 = getPeople<People2>()
        people3!!.printName()
        (people3 as People2).printName2()
        people3 = getPeople<People3>()
        people3!!.printName()
        (people3 as People3).printName3()
        getPeople2<People2>().printName2()
        getPeople2<People3>().printName3()
        p1 = people3
        p2 = people3

        btnAsync.setOnClickListener {
            lifecycleScope.launch {
                val currentTimeMillis = System.currentTimeMillis()
                Log.d(
                    "Wbj",
                    "avatar, companyLogo, start: ${Thread.currentThread().name}, currentTimeMillis: $currentTimeMillis"
                )
                val avatar = getAvatar()
                val companyLogo = getCompanyLogo()
                Log.d(
                    "Wbj",
                    "avatar: $avatar, companyLogo: $companyLogo, end: ${Thread.currentThread().name}" +
                            ", take time: ${1.0 * (System.currentTimeMillis() - currentTimeMillis) / 1000}s"
                )

                Log.i("Wbj", "---sync start---")
                measureTimeMillis {
                    val avatar2 = getAvatar()
                    val companyLogo2 = getCompanyLogo()
                    Log.i(
                        "Wbj",
                        "avatar2: $avatar2, companyLogo2: $companyLogo2, end: ${Thread.currentThread().name}"
                    )
                }.also {
                    Log.i("Wbj", "sync take time: ${1.0 * it / 1000}s")
                }

                Log.w("Wbj", "---async await start---")
                measureTimeMillis {
                    val avatar2 = async { getAvatar() }
                    val companyLogo2 = async { getCompanyLogo() }
                    Log.w(
                        "Wbj",
                        "avatar2: ${avatar2.await()}, companyLogo2: ${companyLogo2.await()}, end: ${Thread.currentThread().name}"
                    )
                }.also {
                    Log.w("Wbj", "async take time: ${1.0 * it / 1000}s")
                }

                deferredAvatar2 = async(start = CoroutineStart.LAZY) { getAvatar() }
                deferredCompanyLogo2 = async(start = CoroutineStart.LAZY) { getCompanyLogo() }
                /*val await = deferredAvatar2!!.await()
                val await1 = deferredCompanyLogo2!!.await()
                Log.w(
                    "Wbj",
                    "lazy avatar2: $await, companyLogo2: $await1, end: ${Thread.currentThread().name}"
                )*/
            }
        }

        btnAsyncLazy.setOnClickListener {
            lifecycleScope.launch {
                Log.w("Wbj", "---lazy async await start---")
                measureTimeMillis {
                    deferredAvatar2?.start()
                    deferredCompanyLogo2?.start()
                    Log.w(
                        "Wbj",
                        "avatar2: ${deferredAvatar2?.await()}, companyLogo2: ${deferredCompanyLogo2?.await()}, end: ${Thread.currentThread().name}"
                    )
                }.also {
                    Log.w("Wbj", "lazy async take time: ${1.0 * it / 1000}s")
                }
            }

            Square("Wbj Square").draw()
            MyOtherClass().draw()
            MyOtherClass2(Square("Delegate class")).draw()
            MyOtherClass2(Square3()).draw()
            var myProperty = MyClass().myProperty
            Log.i("Wbj", "myProperty: $myProperty")
            MyClass().myProperty = "set delegate from out"
            myProperty = MyClass().myProperty
            Log.i("Wbj", "myProperty: $myProperty")

            val mapObj = MapObject(mutableMapOf())
            mapObj.myProperty = "Hello"
            mapObj.myOtherProperty = 12
            Log.i("Wbj", "mapObj myProperty: ${mapObj.myProperty}")
            Log.i("Wbj", "mapObj myOtherProperty: ${mapObj.myOtherProperty}")
            Log.i("Wbj", "mapObj map: ${mapObj.map}")

            val psn = Person6()
            psn.name = "wbj"
            psn.lastname = "wei"
            Log.i(
                "Wbj",
                "person name: ${psn.name}, lastname: ${psn.lastname}, updateCount: ${psn.updateCount}, person: $psn"
            )
            val student = Student4()
            student.name = "sbj"
            student.lastname = "sei"
            Log.i(
                "Wbj",
                "student name: ${student.name}, lastname: ${student.lastname}, updateCount: ${student.updateCount}, student: $student}"
            )
        }

        with2(btnAsyncLazy) {
            isEnabled
            "DDDDD"
        }.also { Log.i("Wbj", "with2Result: $it") }
        btnAsyncLazy?.let2 {
            it.isEnabled
        }.also {
            Log.i("Wbj", "let2Result: $it")
        }
        btnAsyncLazy?.run2 {
            textScaleX
        }.also {
            Log.i("Wbj", "let2Result: $it")
        }
        btnAsyncLazy.apply2 {
            text = "$text, 1"
        }.also {
            Log.i("Wbj", "apply2Result: $it")
        }
        btnAsyncLazy?.run2 {
            textScaleX
        }.also2 {
            Log.i("Wbj", "also2 log: $it")
        }
        btnAsyncLazy.apply2 {
            text = "$text, 1"
        }.also2 {
            Log.i("Wbj", "also2 log: $it")
        }

        try {
            assets.open("use_func.txt")
        } catch (e: IOException) { //try...catch...表达式
            Log.e("Wbj", "read assets file error: $e")
            null
        }?.use {
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024 * 4)
            var len: Int = it.read(buffer)
            while (len != -1) {
                outputStream.write(buffer, 0, len)
                len = it.read(buffer)
            }
            String(outputStream.use { output ->
                output.toByteArray()
            })
        }.also {
            Log.i("Wbj", "read result: $it")
        }

        val people31 = People3(10)
        val people32 = People3(20)
        val people33 = people31 + people32
        Log.i("Wbj", "people33: ${people33.age}, > result: ${people31 > people32}")

        val flow = flow<String> {
            for (i in 1..3) {
                delay(1000)
                Log.d("Wbj", "emit thread:　${Thread.currentThread().name}")
                emit("emit value: $i")
            }
            //throw IllegalStateException("IllegalStateException") //异常会回调到catch，也会回调onCompletion
            //deferredAvatar2!!.await() //异常会回调到catch，也会回调onCompletion
            //什么都不发送(emit)会回调onEmpty
            //正常发送、不发送和异常情况都会回调到onStart和onCompletion
        }.onStart { Log.i("Wbj", "onStart onStart:　${Thread.currentThread().name}") }
            .onCompletion { Log.i("Wbj", "onCompletion onCompletion:　${Thread.currentThread().name}") }
            .onEmpty { Log.d("Wbj", "onEmpty onEmpty:　${Thread.currentThread().name}") }
            .catch { Log.d("Wbj", "catch catch:　${Thread.currentThread().name}") }

        lifecycleScope.launch(Dispatchers.IO) {
            flow.collect {
                Log.v("Wbj", "flow result: $it, collect thread: ${Thread.currentThread().name}")
            }
        }

        letterToInt("abc").also {
            if (it == null) {
                Log.e("Wbj", "runBlocking try...catch fail")
            } else {
                Log.d("Wbj", "runBlocking try...catch, number: $it")
            }
        }

        letterToInt2("abc").getOrNull().also {
            if (it == null) {
                Log.e("Wbj", "runBlocking getOrNull fail")
            } else {
                Log.d("Wbj", "runBlocking getOrNull, number: $it")
            }
        }

        letterToInt2("abcd").exceptionOrNull().also {
            Log.e("Wbj", "runBlocking exceptionOrNull: $it")
        }

        letterToInt2("abcde").onSuccess { Log.d("Wbj", "runBlocking result: $it") }
            .onFailure { Log.e("Wbj", "runBlocking result, fail: $it") }
    }

    var people3: People? = null
    var p1: People? = null
    var p2: People? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Wbj", "people3: $people3")
        people3 = null
        Log.d("Wbj", "people3: $people3")
        Log.d("Wbj", "p1: $p1")
        Log.d("Wbj", "p2: $p2")
    }

    private var deferredAvatar2: Deferred<Int>? = null
    private var deferredCompanyLogo2: Deferred<Int>? = null

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

    private val fruits = listOf("banana", "avocado", "apple", "kiwifruit")

    private fun demoCollection() { //集合
        when {
            "orange" in items -> println("juicy")
            "apple" in items -> println("apple is fine too")
        }

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

    //创建单例
    object Wei {
        const val name = "Wbj"
        var age = 3
    }

    //if null、if not null写法
    private fun ifNotNull() {
        val file = File("test").listFiles()
        println("ifNotNull,file: $file")

        if (file == null) {
            println("ifNotNull,file.size: null")
        } else {
            println("ifNotNull,file.size: ${file.size}")
        }
        println("ifNotNull,file.size: ${file?.size}") //非空调用：语法“?.”，等同以上写法

        val fileSize = file?.size ?: -1 //三目运算符，语法”?:“，如果file为空那么fileSize取值-1，否则取值file.size
        println("ifNotNull,file.size: ${file?.size ?: "is empty"}, fileSize: $fileSize") //if null，执行一个语句

        file?.let { //if not null，执行一段代码：如果file不为空，会执行里面的代码
            println("ifNotNull,file?.let1")
            println("ifNotNull,file?.let2")
        }

        val firstOrNull = fruits.firstOrNull()
        val frts = firstOrNull ?: "no fruit" //在可能为空的集合取出第一个元素
        println("firstOrNull, frts: $frts")
        val mapped = firstOrNull?.let { //如果该值或其描述结果为空，那么返回defaultValue，否则返回运算的值
            println("fruits.firstOrNull, then describe(it)")
            describe(it)
        } ?: "mapped defaultValue"
        println("firstOrNull, mapped: $mapped")
    }

    //"try/catch"表达式
    private fun testTry() {
        val result = try {
            this.describe(13)
        } catch (e: java.lang.ArithmeticException) {
            throw IllegalStateException(e)
        }

        println("try...catch, result: $result")
    }

    //返回类型为Unit的方法的Builder风格用法
    private fun arrayOfMinusOnes(size: Int): IntArray {
        return IntArray(size).apply { fill(-1) }
    }

    //单表达式函数
    private fun theAnswer() = 3
    private fun theAnswer2() = this.maxOf2(7, 8)

    class Turtle {
        fun penDown() {
            println("Turtle, penDown")
        }

        fun penUp() {
            println("Turtle, penUp")
        }

        fun turn(degrees: Double) {
            println("Turtle, turn: $degrees")
        }

        fun forward(pixels: Double) {
            println("Turtle, forward: $pixels")
        }
    }

    //对一个对象实例调用多个方法(with)
    private fun testTurtle() {
        val myTurtle = Turtle()
        with(myTurtle) { //with
            penDown()
            for (i in 1..4) {
                forward(100.0)
                turn(90.0)
            }
            penUp()
        }
    }

    //配置对象的属性(apply):这对于配置未出现在对象构造函数中的属性非常有用
    private fun testApply() {
        val rect = Rect().apply { //apply
            left = 3
            top = theAnswer()
            right = theAnswer2()
        }

        println("testApply, rect: $rect")

        val stream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.newInputStream(Paths.get("/some/file.txt"))
            } catch (e: Exception) {
                //throw Exception("===/some/file.txt====")
                println("reader.readText(), Exception")
            }
        } else {
            TODO("VERSION.SDK_INT < O") //将代码标记为不完整
        }

        if (stream == null) {
            println("reader.readText(), stream 111111")
        } else {
            println("reader.readText(), stream 222222")
            /*stream.buffered().reader().use { reader ->
                println("reader.readText()：${reader.readText()}")
            }*/
        }
    }

    private fun nullableAlso() {
        val b: Boolean? = true //使用可空布尔值:Boolean?
        if (b == true) {
            println("nullableBoolean, b: $b")
        } else {
            println("nullableBoolean, b is null or false")
        }

        //交换两个变量:also
        var c = 11
        var d = 22
        println("exchange c: $c, d:$d")
        c = d.also { d = c }
        println("exchange c: $c, d:$d")
    }

    //TODO(String)：将代码标记为不完整
    private fun calcTaxes(): BigDecimal = TODO("Waiting for feedback from accounting")
    //习惯用法：end

    private fun coroutines() {
        GlobalScope.launch {
            delay(3000L)
            Log.d("Wbj_", "GlobalScope, World")
        }
        Log.d("Wbj_", "GlobalScope, Hello")
        //Thread.sleep(2000L)
        runBlocking {
            delay(1000L)
        }
    }

    private fun coroutines2() = runBlocking {
        GlobalScope.launch {
            delay(3000L)
            Log.d("Wbj_", "GlobalScope, World")
        }
        Log.d("Wbj_", "GlobalScope, Hello")
        delay(2000L)
        Log.d("Wbj_", "GlobalScope, Hello333")
    }

    private fun coroutines3() = runBlocking {
        val job = GlobalScope.launch {
            delay(3000L)
            Log.d("Wbj_", "GlobalScope, World")
        }
        Log.d("Wbj_", "GlobalScope, Hello")
        job.join()
        Log.d("Wbj_", "GlobalScope, Hello222")
    }

    private fun coroutines4() = runBlocking {
        launch {
            delay(3000L)
            Log.d("Wbj_", "GlobalScope, World")
        }
        Log.d("Wbj_", "GlobalScope, Hello")
    }

    private fun coroutines5() = runBlocking {
        /*val job = launch {
            delay(3000L)
            Log.d("Wbj_", "GlobalScope, World")
        }
        delay(2000)
        Log.d("Wbj_", "GlobalScope, Hello")
        //job.cancel()
        //job.join()
        job.cancelAndJoin()*/

        val job = launch {
            try {
                repeat(1000) { i ->
                    Log.d("Wbj_", "job: I'm sleeping $i ...")
                    delay(2000L)
                }
            } finally {
                Log.d("Wbj_", "job: I'm running finally")
            }
        }
        delay(1300L) // 延迟一段时间
        Log.d("Wbj_", "main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并且等待它结束
        Log.d("Wbj_", "main: Now I can quit.")
    }

    fun plus(a: Int, b: Int): Int = a + b

    fun minus(a: Int, b: Int): Int {
        return a - b
    }

    //参数是函数类型的高阶函数
    private fun operation(a: Int, b: Int, func: (Int, Int) -> Int): Int {
        return func(a, b)
    }

    //返回值是函数类型的高阶函数
    private fun returnFunType(type: Int): (Int, Int) -> Int {
        return if (type == 0) {
            ::plus
        } else {
            ::minus
        }
    }

    //函数类型变量
    private var funType: (Int, Int) -> Int = ::plus
    private var funType2: (Int, Int) -> Int = { a: Int, b: Int -> a + b }

    //private val action: (Int) -> Unit = { value: Int -> Log.d("Wbj", "action value, $value") }
    //对action简化1：Kotlin有类型推到机制，Int可以去掉
    private val action: (Int) -> Unit = { value -> Log.d("Wbj", "action value, $value") }
    private val action2: (Int) -> Unit = ::printInt
    private fun printInt(value: Int) {
        Log.d("Wbj", "action2 value, $value")
    }

    private inline fun <reified T : People> getPeople(): People {
        return T::class.java.newInstance()
    }

    private inline fun <reified T : People> getPeople2(): T {
        return T::class.java.newInstance()
    }

    private suspend fun getAvatar(): Int = withContext(Dispatchers.IO) {
        //delay(2000) //delay是一个特殊的挂起函数，它不会造成线程阻塞，但是会挂起协程，并且只能在协程中使用。
        Thread.sleep(2000) //sleep会阻塞线程。因此要把它放在子线程中，不然会阻塞主线程。
        Log.v("Wbj", "getAvatar thread: ${Thread.currentThread().name}")
        return@withContext 1
    }

    private suspend fun getCompanyLogo(): Int = withContext(Dispatchers.IO) {
        //delay(2000)
        Thread.sleep(3000)
        Log.v("Wbj", "getCompanyLogo thread: ${Thread.currentThread().name}")
        return@withContext 2
    }

    inline fun lineInNoCross(block: () -> Unit) {
        block()
    }

    inline fun lineInNoCross(noinline block: () -> Unit, block2: () -> Unit): () -> Unit {
        block()
        block2()
        //return block2 //compile wrong
        return block
    }

    private fun letterToInt(input: String): Int? {
        return try {
            input.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun letterToInt2(input: String): Result<Int> {
        return runCatching { input.toInt() }
    }

}