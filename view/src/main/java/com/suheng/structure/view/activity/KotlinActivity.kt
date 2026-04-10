package com.suheng.structure.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import android.media.MediaRouter2
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.suheng.structure.view.R
import com.suheng.structure.view.drawable.Halo
import com.suheng.structure.view.kt.darkModeCtx
import com.suheng.structure.view.kt.delegate.BundleHandler
import com.suheng.structure.view.kt.delegate.BundleHandlerImpl
import com.suheng.structure.view.kt.generic.People
import com.suheng.structure.view.kt.lightModeCtx
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigDecimal

class KotlinActivity : AppCompatActivity(), BundleHandler by BundleHandlerImpl() {

    companion object {
        const val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTheme(R.style.ThemeBrandA)
        setTheme(R.style.ThemeBrandB)
        setContentView(R.layout.activity_kotlin)
        bundleHandler(this, savedInstanceState)
        Log.d(TAG, "bundleSum: ${bundleSum(10, 23)}" +
                    ", ::minus: ${returnFunType(10)(10, 10)}" +
                    ", ::plus: ${returnFunType(0)(10, 10)}"
        )

        findViewById<TextView>(R.id.text_night).apply {
            /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))*/

            val attributes = context.darkModeCtx().obtainStyledAttributes(R.style.ThemeBrandB, intArrayOf(R.attr.brand_color))
            val color = attributes.getColor(0, Color.GRAY)
            attributes.recycle()

            /*val ctxA = ContextThemeWrapper(context.darkModeCtx(), R.style.ThemeBrandB)
            //val ctxA = ContextThemeWrapper(context.darkModeCtx(), context.theme)
            val color = ctxA.obtainStyledAttributes(intArrayOf(R.attr.brand_color)).let {
                val c = it.getColor(0, Color.GRAY)
                it.recycle()
                c
            }*/

            //setTextColor(ContextCompat.getColor(context.darkModeCtx(), R.color.colorPrimaryDark))
            setTextColor(color)

            this@KotlinActivity.findViewById<TextView>(R.id.text_ui_mode).setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )

            setOnClickListener {
                testSync(lifecycleScope)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    MediaRouter2.getInstance(this@KotlinActivity).showSystemOutputSwitcher()
                }
            }
        }

        findViewById<TextView>(R.id.text_light).apply {
            setOnClickListener {
                testProdCus(lifecycleScope)
            }
            setTextColor(ContextCompat.getColor(context.lightModeCtx(), R.color.colorPrimaryDark))
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel)
            setOnClickListener {
                (background as? Halo)?.start()
            }
            post {
                background = Halo(context)
                (background as? Halo)?.setAnimatorListener(object :
                    AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        layoutPanel.alpha = 1f
                    }
                })
            }
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout2).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel2)
            setOnClickListener {
                (background as? Halo)?.start()
            }
            post {
                background = Halo(context, showBottomBitmap = false)
                (background as? Halo)?.setAnimatorListener(object :
                    AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        layoutPanel.alpha = 1f
                    }
                })
            }
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout3).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel3)
            setOnClickListener {
                (background as? Halo)?.start()
            }
            post {
                background =
                    Halo(context, showBottomBitmap = false, showEdgeRadial = false)
                /*layoutParams?.let {
                    it.width = resources.getDimensionPixelOffset(R.dimen.sunlight_width)
                    it.height = resources.getDimensionPixelOffset(R.dimen.sunlight_height)
                    layoutParams = it
                }*/
                (background as? Halo)?.setAnimatorListener(object :
                    AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        layoutPanel.alpha = 1f
                    }
                })
            }
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout4).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel4)
            setOnClickListener {
                (background as? Halo)?.start()
            }
            val radius = resources.getDimensionPixelOffset(R.dimen.sunlight_extend_round)
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
                }
            }
            post {
                background = Halo(
                    context,
                    showBottomBitmap = false,
                    showEdgeRadial = false,
                    showBlurBg = false
                )
                //layoutPanel.alpha = 0f
                (background as? Halo)?.setAnimatorListener(object :
                    AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        layoutPanel.alpha = 1f
                    }
                })
            }
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout5).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel5)
            setOnClickListener {
                (background as? Halo)?.start()
            }
            val radius = resources.getDimensionPixelOffset(R.dimen.sunlight_extend_round)
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
                }
            }
            post {
                background = Halo(
                    context,
                    showBottomBitmap = false,
                    showEdgeRadial = false,
                    showBlurBg = false,
                    showLinearColor = false
                )
                //layoutPanel.alpha = 0f
                (background as? Halo)?.setAnimatorListener(object :
                    AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        layoutPanel.alpha = 1f
                    }
                })
            }
        }

        findViewById<ViewGroup>(R.id.sun_iv1_layout6).apply {
            val layoutPanel = findViewById<View>(R.id.sunlight_panel6)
            val animView = findViewById<AnimView>(R.id.animView6).apply {
                setLoop(1)
                setScaleType(ScaleType.FIT_XY)
                setAnimListener(object : IAnimListener {
                    override fun onVideoStart() {
                        layoutPanel.alpha = 0.5f
                    }

                    override fun onVideoRender(
                        frameIndex: Int, config: AnimConfig?
                    ) {
                    }

                    override fun onVideoComplete() {
                        layoutPanel.alpha = 1f
                    }

                    override fun onVideoDestroy() {
                    }

                    override fun onFailed(errorType: Int, errorMsg: String?) {
                    }
                })
            }
            layoutPanel.setOnClickListener {
                animView.startPlay(assets, "demo.mp4")
            }
            val radius = resources.getDimensionPixelOffset(R.dimen.sunlight_extend_round)
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
                }
            }
        }
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

    //TODO(String)：将代码标记为不完整
    private fun todo(): BigDecimal = TODO("todo()")
    //习惯用法：end

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