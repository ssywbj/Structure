package com.suheng.structure.view.wheel

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class CoroutineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CoroutineScope by MainScope()/*使用协程方式1*/ {

    companion object {
        private const val TAG = "CoroutineView"
    }

    /*使用协程方式2*/
    private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        //作用
        //阻塞当前线程：runBlocking会阻塞调用它的线程，直到其内部的协程执行完毕。这与其他协程启动函数（如launch和async）不同，后者不会阻塞当前线程。
        //提供协程作用域：runBlocking会创建一个新的协程作用域，这个作用域内可以启动其他协程。
        //使用场景
        //主函数：在main函数中使用runBlocking可以运行协程代码，因为main函数不能直接使用suspend关键字。
        //测试：在单元测试中，使用runBlocking可以确保测试代码在协程执行完毕后再继续执行。
        //注：无论使用Dispatchers.IO、Dispatchers.Default还是其他调度器，调用runBlocking的线程都会被阻塞。调度器影响的是其内部协程的调度，而不是它本身的行为。
        /*runBlocking(Dispatchers.IO) { //阻塞主线程
            val fValue = getFirstValue()
            val sValue = getSecondValue()
            Log.d(TAG, "fValue: $fValue, sValue:$sValue, thread: ${Thread.currentThread().name}")
        }*/

        val flow = flow {
            emit(getFirstValue())
            emit(getThirdValue())
        }

        defaultScope.launch {
            flow.collect { value ->
                Log.v(TAG, "defaultScope value: $value, thread: ${Thread.currentThread().name}")
            }
        }

        launch(Dispatchers.IO) {
            measureTimeMillis {
                //顺序执行：先getFirstValue()，后getSecondValue()
                flow.collect { value ->
                    Log.d(TAG, "sync value: $value, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "sync value take time: $it")
            }
        }

        val sFlow = flow {
            emit(getSecondValue("a"))
        }

        val tFlow = flow {
            emit(getThirdValue(1))
        }

        val s2Flow = flow {
            emit(getSecondValue("a"))
            emit(getSecondValue("b"))
        }
        val t2Flow = flow {
            emit(getThirdValue(1))
            emit(getThirdValue(2))
            emit(getThirdValue(3))
        }
        Log.w(TAG, "combine flow begin: ${System.currentTimeMillis()}")
        //并发执行：getFirstValue()、getSecondValue()同时进行并等待它们都完成
        //合并流：以最晚一个结束的支流而结束的流
        val combine = tFlow.combine(sFlow) { third, second -> third to second }
        launch(Dispatchers.IO) {
            measureTimeMillis {
                combine.collect { (tValue, sValue) ->
                    Log.d(TAG, "combine two flow: $tValue $sValue, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "combine two flow take time: $it")
            }
        }

        val combines = combine(s2Flow, t2Flow) { second, third -> second to third }
        launch(Dispatchers.IO) {
            measureTimeMillis {
                combines.collect { (sValue, tValue) ->
                    Log.d(TAG, "---combine two flow: $sValue $tValue, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "---combine two flow take time: $it")
            }
        }

        val combineThree =
            combine(flow, sFlow, tFlow) { first, second, third -> Triple(first, second, third) }
        launch(Dispatchers.IO) {
            measureTimeMillis {
                combineThree.collect { triple ->
                    Log.i(TAG, "3333combine three flow: $triple, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "3333combine three flow take time: $it")
            }
        }

        Log.w(TAG, "zip flow begin: ${System.currentTimeMillis()}")
        //并发执行：getFirstValue()、getSecondValue()同时进行并等待它们都完成
        //压缩流：以最早一个结束的支流而结束的流
        val zipFow = tFlow.zip(sFlow) { third, second -> third to second }
        launch(Dispatchers.IO) {
            measureTimeMillis {
                zipFow.collect { (tValue, sValue) ->
                    Log.d(TAG, "zipFow two flow: $tValue $sValue, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "zip two flow take time: $it")
            }
        }
        val zip2Fow = s2Flow.zip(t2Flow) { second, third -> second to third }
        launch(Dispatchers.IO) {
            measureTimeMillis {
                zip2Fow.collect { (tValue, sValue) ->
                    Log.d(TAG, "2222zip two flow: $tValue $sValue, thread: ${Thread.currentThread().name}")
                }
            }.also {
                Log.w(TAG, "2222zip two flow take time: $it")
            }
        }

        Log.w(TAG, "flatMapConcat flow begin: ${System.currentTimeMillis()}")
        val flatMapConcat = flow {
            emit(getThirdValue(5))
            emit(getFirstValue())
        }.flatMapConcat {
            flow { emit(getSecondValue("concat preview flow value: $it")) }
        }
        launch {
            measureTimeMillis {
                flatMapConcat.collect { value ->
                    Log.v(TAG, "flatMapConcat: $value")
                }
            }.also {
                Log.w(TAG, "flatMapConcat flow take time: $it")
            }
        }

        Log.w(TAG, "sync exec begin: ${System.currentTimeMillis()}")
        launch {
            measureTimeMillis {
                val sValue = getSecondValue("100")
                val tValue = getThirdValue(sValue.toInt())
                Log.v(TAG, "sync exec: $sValue, $tValue")
            }.also {
                Log.w(TAG, "sync exec take time: $it")
            }
        }

        //https://juejin.cn/post/7377025870629814298
        launch {
            Log.w(TAG, "collectLatest exec begin: ${System.currentTimeMillis()}")
            flow.collectLatest {
                Log.v(TAG, "collectLatest value: $it")
                delay(4000)
                Log.d(TAG, "collectLatest value---: $it")
            }

            flow.collect {
                Log.i(TAG, "compared collectLatest value: $it")
                delay(4000)
                Log.i(TAG, "compared collectLatest value---: $it")
            }

            val flow2 = (1..5).asFlow().onEach { delay(300) }
            flow2.collectLatest { value ->
                Log.v(TAG, "collectLatest Processing $value")
                try {
                    // 模拟异步操作：消费时长大于生产时长，新生产值到达时都会取消当前的操作去处理新值。
                    // 又因为每一个消费时长都大于生产时长，所以最终能处理完成的是最后一个发送的值。
                    // 如果生产时长都大于消费时长，那么还是和collect一样，都能正常处理完成。
                    delay(500)
                    Log.d(TAG, "collectLatest Processed $value")
                } catch (e: CancellationException) {
                    Log.e(TAG, "collectLatest Process cancel $value")
                }
            }
        }

        launch {
            Log.w(TAG, "transform exec begin: ${System.currentTimeMillis()}")
            flow {
                emit(getFirstValue())
                emit(getSecondValue("transform"))
                emit(getThirdValue())
            }.transform {
                Log.v(TAG, "transform origin value: $it")
                when (it) {
                    is String -> {
                        emit(it.substring(2))
                        /*flow.collect { value ->
                            Log.v(TAG, "inner transform: $value")
                        }*/
                        emit(flow)
                    }
                    is Int -> {
                        emit(it * it)
                        emit(it + it)
                    }

                    else -> emit("$it, $it")
                }
            }.collect {
                //Log.d(TAG, "transform new value: $it")
                if (it is Flow<*>) {
                    it.collect { value ->
                        Log.v(TAG, "inner transform: $value")
                    }
                } else {
                    Log.d(TAG, "transform new value: $it")
                }
            }
        }

        launch {
            Log.w(TAG, "map operator exec begin: ${System.currentTimeMillis()}")
            flow {
                emit(getFirstValue())
                emit(getSecondValue("map operator"))
                emit(getThirdValue())
            }.map {
                Log.v(TAG, "map operator origin value: $it")
                when (it) {
                    is String -> it.substring(2)
                    is Int -> {
                        it * it
                        it + it
                    }

                    else -> "$it, $it"
                }
            }.collect {
                Log.d(TAG, "map operator new value: $it")
            }
        }

        Log.w(TAG, "async exec begin: ${System.currentTimeMillis()}")
        val sAsync = async { getSecondValue("a") }
        val tAsync = async { getThirdValue(1) }
        launch {
            measureTimeMillis {
                val sValue = sAsync.await()
                val tValue = tAsync.await()
                Log.v(TAG, "async exec: $sValue, $tValue")
            }.also {
                Log.w(TAG, "async exec take time: $it")
            }
        }

        post {
            Log.w(TAG, "lazy async exec begin: ${System.currentTimeMillis()}")
            sAsyncLazy.start() //注：不调用start()的LAZY类型async是同步执行，不是异步执行
            tAsyncLazy.start()
            launch {
                measureTimeMillis {
                    val sValue = sAsyncLazy.await()
                    val tValue = tAsyncLazy.await()
                    Log.v(TAG, "lazy async exec: $sValue, $tValue")
                }.also {
                    Log.w(TAG, "lazy async exec take time: $it")
                }
            }
        }

        //https://juejin.cn/post/7089808716135923742
        val fAsync = async { getFirstValue() }
        launch {
            Log.w(TAG, "select async exec begin: ${System.currentTimeMillis()}")
            val result = select {
                sAsync.onAwait {
                    Log.v(TAG, "select sAsync result: $it")
                    "$it,$it"
                } //{}里最后一行为返回值
                tAsync.onAwait { it }
                fAsync.onAwait {
                    Log.v(TAG, "select fAsync result: $it")
                    it * it
                }
            }
            Log.v(TAG, "select async result: $result")
        }
    }

    private val sAsyncLazy = async(start = CoroutineStart.LAZY) { getSecondValue("a") }
    private val tAsyncLazy = async(start = CoroutineStart.LAZY) { getThirdValue(1) }

    private suspend fun getFirstValue(): Int {
        delay(1000)
        return 10
    }

    private suspend fun getSecondValue(value: String): String {
        delay(2000)
        return value
    }

    private suspend fun getThirdValue(value: Int = 0): Float {
        delay(3000)
        return if (value == 0) 1.1f else value * 1.111f
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancel()
        defaultScope.cancel()
    }

    private var validWidth: Int = 0 //异步初始化方法1：使用MutableStateFlow状态流
    private val valueFlow = MutableStateFlow<Int?>(null)
    private val job = launch {
        valueFlow.filterNotNull().collect { value ->
            validWidth = value
            Log.i(TAG, "mutableValidWidth: $validWidth, job thread: ${Thread.currentThread().name}")
            printValidWidth()
        }
    }

    private fun getValidWidth() {
        post {
            valueFlow.value = (width - 10).run {
                Log.d(TAG, "getValidWidth: $this")
                if (this > 0) this else null
            }
        }
    }

    private fun printValidWidth() {
        if (validWidth == 0) {
            launch(Dispatchers.IO) {
                getValidWidth()
                job.join()
                Log.w(TAG, "printValidWidth: $validWidth, behind job.join() process")
            }
        } else {
            Log.i(TAG, "printValidWidth: $validWidth, thread: ${Thread.currentThread().name}")
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            printValidWidth()
            printValidWidth2()
            printValidWidth3()
        }
    }

    private var validWidth2: Int? = null //异步初始化方法2：使用suspendCancellableCoroutine挂起函数

    private suspend fun getValidWidth2(): Int? = withContext(Dispatchers.IO) {
        Log.d(TAG, "withContext getValidWidth2, thread: ${Thread.currentThread().name}")
        //delay(3000) //模拟耗时操作
        //return@withContext 1000

        suspendCancellableCoroutine { continuation ->
            post {
                val tmpWidth = (width - 10).run {
                    Log.d(TAG, "init getValidWidth2: $this, thread: ${Thread.currentThread().name}")
                    if (this > 0) this else null
                }
                continuation.resume(tmpWidth) //在匿名内部类的回调中把数值发送出来
            }
        }
    }

    private fun printValidWidth2() {
        /*launch(Dispatchers.Main) {
            if (validWidth2 == null) {
                validWidth2 = getValidWidth2()
            }
            Log.w(TAG, "printValidWidth2: $validWidth2, thread: ${Thread.currentThread().name}")
        }*/

        if (validWidth2 == null) {
            launch {
                validWidth2 = getValidWidth2()
                Log.i(TAG, "printValidWidth2: $validWidth2, thread: ${Thread.currentThread().name}")
            }
        } else {
            Log.i(TAG, "printValidWidth2: $validWidth2")
        }
    }

    private var validWidth3: Int? = null //异步初始化方法3：使用callbackFlow回调流

    private fun getValidWidth3(): Flow<Int?> = callbackFlow {
        //delay(3000) //模拟耗时操作
        post {
            val tmpWidth = (width - 10).run {
                Log.d(TAG, "init getValidWidth3: $this, thread: ${Thread.currentThread().name}")
                if (this > 0) this else null
            }
            //trySend(tmpWidth)
            trySendBlocking(tmpWidth)
        }
        //awaitClose()
        awaitClose { Log.d(TAG, "init getValidWidth3, awaitClose") }
    }

    private fun printValidWidth3() {
        if (validWidth3 == null) {
            launch {
                getValidWidth3().collect {
                    validWidth3 = it
                    Log.i(TAG, "printValidWidth3: $validWidth3, thread: ${Thread.currentThread().name}")
                }
            }
        } else {
            Log.i(TAG, "printValidWidth3: $validWidth3")
        }
        //https://juejin.cn/post/6989782281191686180
        //https://www.jetbrains.com/lp/compose-multiplatform/
        //https://juejin.cn/post/6924609524548501517
        //https://github.com/SaberAlpha/kotlinpractice
    }

}