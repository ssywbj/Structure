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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class CoroutineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CoroutineScope by MainScope()/*使用协程方式1*/ {

    companion object {
        private const val TAG = "CoroutineView"
    }

    private val supervisorJob = SupervisorJob()

    /*使用协程方式2*/
    private val defaultScope = CoroutineScope(Dispatchers.Default + supervisorJob)

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
                    // 又因为每一个消费时长都大于生产时长，所以最终能处理完成的只有最后一个发送的值。
                    // 如果生产时长都大于消费时长，那么还是和collect一样，都能正常处理完成。
                    delay(500)
                    Log.d(TAG, "collectLatest Processed $value")
                } catch (e: CancellationException) {
                    Log.e(TAG, "collectLatest Process cancel $value")
                }
            }
        }

        launch {
            Log.w(TAG, "flatMapLatest exec begin: ${System.currentTimeMillis()}")
            val flow2 = (1..5).asFlow().onEach { delay(300) }
            //flatMapLatest是中间转换符，collectLatest是终端操作符。两者使用场景和作用位置不一样，功能一样。
            flow2.flatMapLatest { value ->
                flow {
                    emit("Processing $value")
                    delay(500)
                    emit("Processed $value") //转换成String后再发射
                }
            }.collect { result ->
                Log.i(TAG, "flatMapLatest result: $result")
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
            var retryCount = 0
            Log.w(TAG, "retry exec begin: ${System.currentTimeMillis()}")
            (1..3).asFlow().onEach {
                delay(1000)
                if (retryCount < 2/*4*/) {
                    if (it == 2) {
                        throw IOException("Test Error")
                    }
                }
            }.retry(3) { cause ->
                Log.w(TAG, "retry: $cause, retryCount: ${++retryCount}")
                cause is IOException //true时执行重试
                //return@retry false //false或次数到时重试结束，此时会走进catch代码块
                //if (retryCount == 2) false else true
            }.catch { //如果抛出异常，就要捕获异常，不然程序崩溃
                Log.e(TAG, "retry error: $it")
            }.onCompletion { Log.i(TAG, "retry onCompletion, retryCount: $retryCount") }.collect {
                Log.d(TAG, "retry value: $it")
            }

            retryCount = 0
            (1..3).asFlow().onEach {
                delay(1000)
                check(it == 1) {
                    "Value Error $it"
                }
            }.retryWhen { cause, attempt ->
                Log.w(TAG, "retryWhen: $cause, retryCount: ${++retryCount}")
                cause is IllegalStateException //true时执行重试
                //return@retry false //false或次数到时重试结束，此时会走进catch代码块
                //if (retryCount == 2) false else true
                attempt < 2 //attempt：尝试的次数，从零开始
            }.catch { //如果抛出异常，就要捕获异常，不然程序崩溃
                Log.e(TAG, "retryWhen error: $it")
            }.onCompletion { Log.i(TAG, "retryWhen onCompletion, retryCount: $retryCount") }
                .collect {
                    Log.d(TAG, "retryWhen value: $it")
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

    private val upFlow = (1..4).asFlow() //上游流
    private val emptyFlow = flow<Int> { } //空流：没有发射任何数据的流
    private var flowInvokeOrder = false

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            launch {
                //中游流
                val midFlow = upFlow.onEach {
                    delay(1000)

                    //上游的流抛出异常后，下游的流一定要捕获，即写上catch语句，不然程序报错
                    //check(it == 3) //检查值等于3时通过，不等于3时抛出异常
                    check(it != 3) //检查值不等于3时通过，等于3时抛出异常
                }.onStart {
                    Log.i(TAG, "upMidDownFlow, onStart")
                }.onEmpty { //空流的调用会走到onEmpty方法
                    Log.v(TAG, "upMidDownFlow, onEmpty")
                }
                //下游流：注意调用的顺序不一样，返回的流对象也不一样
                val downFlow = if (flowInvokeOrder) {
                    //cause：完成原因。正常完成为null，异常完成则给出原因。注：若onCompletion前异常被捕获，则也算是正常完成。
                    midFlow.onCompletion { cause ->
                        Log.i(TAG, "upMidDownFlow, onCompletion: $cause")
                    }.catch { //捕获异常
                        Log.e(TAG, "upMidDownFlow catch: $it")
                    }
                } else {
                    midFlow.catch {
                        Log.e(TAG, "upMidDownFlow catch: $it")
                    }.onCompletion { cause -> //因为onCompletion前是catch语句，当异常被捕获后，这里的cause就为null。
                        Log.i(TAG, "upMidDownFlow, onCompletion: $cause")
                    }
                }
                flowInvokeOrder = !flowInvokeOrder
                Log.w(
                    TAG,
                    "upMidDownFlow, upFlow: $upFlow\nmidFlow: $midFlow\ndownFlow: $downFlow\n"
                )
                downFlow.collect { //消费
                    Log.d(TAG, "upMidDownFlow collect: $it")
                }
            }

            printValidWidth1()
            printValidWidth2()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancel()
        defaultScope.cancel()
    }

    private var validWidth1: Int? = null //异步初始化方法1：使用suspendCancellableCoroutine挂起函数

    private val jobValidWidth1 = launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
        Log.v(TAG, "joining, validWidth1")
        validWidth1 = getValidWidth1()
    }

    private val deferredValidWidth1 = async(Dispatchers.IO, start = CoroutineStart.LAZY) {
        Log.v(TAG, "deferring, validWidth1")
        getValidWidth1() //最后一句为返回值
    }

    private suspend fun getValidWidth1(): Int? = withContext(Dispatchers.IO) {
        Log.d(TAG, "init getValidWidth1, ${Thread.currentThread().name}")
        delay(2000) //模拟耗时操作
        suspendCancellableCoroutine { continuation ->
            post {
                val tmpWidth = (width - 10).run {
                    Log.v(TAG, "post getValidWidth1: $this, ${Thread.currentThread().name}")
                    if (this > 0) this else null
                }
                continuation.resume(tmpWidth) //在匿名内部类的回调中把数值发送出来
            }
        }
    }

    /*private suspend fun getValidWidth1(): Int? = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "CancellableContinuation validWidth1, ${Thread.currentThread().name}")
        post {
            val tmpWidth = (width - 10).run {
                Log.i(TAG, "init validWidth1: $this, ${Thread.currentThread().name}")
                if (this > 0) this else null
            }
            continuation.resume(tmpWidth) //在匿名内部类的回调中把数值发送出来
        }
    }*/

    private fun printValidWidth1() {
        /*launch {
            if (validWidth1 == null) {
                validWidth1 = getValidWidth1() //挂起函数会阻塞后面语句的执行，当函数执行完后会继续往下执行
            }
            Log.w(TAG, "suspend after, validWidth1: $validWidth1, ${Thread.currentThread().name}")
        }*/

        launch(Dispatchers.Default) {
            if (validWidth1 == null) {
                jobValidWidth1.join() //join函数会阻塞后面语句的执行，当Job执行完后会继续往下执行
            }
            Log.w(TAG, "join after, validWidth1: $validWidth1, ${Thread.currentThread().name}")
        }

        if (validWidth1 == null) {
            jobValidWidth1.start() //start函数不会阻塞后面语句的执行，直接往下执行
        }
        Log.e(TAG, "start after, validWidth1: $validWidth1, ${Thread.currentThread().name}")

        /*launch(Dispatchers.IO) {
            if (validWidth1 == null) {
                validWidth1 = deferredValidWidth1.await() //await函数会阻塞后面语句的执行，当Deferred执行完后会继续往下执行
            }
            Log.w(TAG, "await after, validWidth1: $validWidth1, ${Thread.currentThread().name}")
        }*/
    }

    private var validWidth2: Int? = null //异步初始化方法2：使用callbackFlow回调流

    private fun getValidWidth2(): Flow<Int?> = callbackFlow {
        Log.d(TAG, "callbackFlow getValidWidth2, ${Thread.currentThread().name}")
        delay(2000) //模拟耗时操作
        post {
            val tmpWidth = (width - 10).run {
                Log.v(TAG, "init getValidWidth2: $this, ${Thread.currentThread().name}")
                if (this > 0) this else null
            }
            //trySend(tmpWidth)
            trySendBlocking(tmpWidth)
        }
        //awaitClose()
        awaitClose { Log.d(TAG, "validWidth2, awaitClose") }
    }

    private fun printValidWidth2() {
        /*val deferred = CompletableDeferred<Int?>()
         launch(Dispatchers.Main) {
            if (validWidth2 == null) {
                getValidWidth2().flowOn(Dispatchers.IO).collect {
                    validWidth2 = it
                    Log.d(TAG, "joining, printValidWidth2: $validWidth2, thread: ${Thread.currentThread().name}")
                    deferred.complete(validWidth2)
                }
                val await = deferred.await()
                Log.i(TAG, "join after, printValidWidth2: $await, thread: ${Thread.currentThread().name}")
            }
           Log.i(TAG, "join after, printValidWidth2: $validWidth2, thread: ${Thread.currentThread().name}")
        }*/

        defaultScope.launch(Dispatchers.Main) {
            if (validWidth2 == null) {
                Log.d(TAG, "callbackFlow getValidWidth2, thread: ${Thread.currentThread().name}")
                delay(2000) //模拟耗时操作
                post {
                    validWidth2 = (width - 10).run {
                        Log.v(TAG, "init getValidWidth2: $this, thread: ${Thread.currentThread().name}")
                        if (this > 0) this else null
                    }
                    supervisorJob.complete()
                }
                supervisorJob.join() //join函数会阻塞后面语句的执行，当job执行完后会继续往下执行
            }
            Log.i(TAG, "join after, printValidWidth2: $validWidth2, thread: ${Thread.currentThread().name}")
        }

    }

    //https://juejin.cn/post/6989782281191686180
    //https://www.jetbrains.com/lp/compose-multiplatform/
    //https://juejin.cn/post/6924609524548501517
    //https://github.com/SaberAlpha/kotlinpractice
}