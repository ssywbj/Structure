package com.suheng.structure.view.kt.delegate

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.suheng.structure.view.kt.countDownFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.Collections


class BundleHandlerImpl : BundleHandler {
    companion object {
        const val TAG = "BundleHandler"
    }

    private val numberList = arrayListOf<Int>()
    private val lockNumberList = Any()

    private fun bundleAdd(a: Int, b: Int): Int = a + b

    override val bundleSum: (Int, Int) -> Int = ::bundleAdd

    override fun bundleHandler(activity: Activity, bundle: Bundle?) {
        Log.d(TAG, "bundle: $bundle, activity: $activity")
    }

    override fun testSync(scope: CoroutineScope) {
        countDownFlow(timeMillis = 50).onEach {
            Log.i(TAG, "add: $it, thread: ${Thread.currentThread().name}")

            synchronized(lockNumberList) {
                numberList.add(it)
            }
        }.flowOn(Dispatchers.Unconfined).launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            val sb = StringBuilder()
            synchronized(lockNumberList) {
                numberList.forEach { sb.append(it).append(" ") }
            }

            Log.d(TAG, "forEach: $sb, thread: ${Thread.currentThread().name}")
        }.launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            Log.i(TAG, "remove: $it, thread: ${Thread.currentThread().name}")

            synchronized(lockNumberList) {
                numberList.remove(it)
            }
        }.flowOn(Dispatchers.IO).launchIn(scope)
    }

    override fun testProdCus(scope: CoroutineScope) {
        scope.launch {
            countDownFlow(timeMillis = 50).onEach {
                Log.i(TAG, "product add: $it, thread: ${Thread.currentThread().name}")
                synchronized(lockNumberList) {
                    numberList.add(it)
                }
            }.transform { emit(numberList) }.flowOn(Dispatchers.Unconfined).onEach {
                val sb = StringBuilder()
                synchronized(lockNumberList) {
                    val iterator = it.iterator()
                    while (iterator.hasNext()) {
                        val num = iterator.next()
                        if (num % 2 == 0 || num % 3 == 0) {
                            sb.append(num).append(" ")
                            iterator.remove()
                        }
                    }
                }
                Log.i(TAG, "custom remove: $sb, thread: ${Thread.currentThread().name}")
            }.map {
                val sb = StringBuilder()
                synchronized(lockNumberList) {
                    it.forEach { num -> sb.append(num).append(" ") }
                }
                sb
            }.flowOn(Dispatchers.IO).collect {
                it.deleteCharAt(it.lastIndex)
                Log.d(TAG, "collect: $it, thread: ${Thread.currentThread().name}")
            }
        }
    }

    private val linkedHashMap = LinkedHashMap<Int, String>()

    private val synchronizedMap = Collections.synchronizedMap(linkedHashMap) // decorate LinkedHashMap

    override fun linkedHashMap(scope: CoroutineScope) {
        countDownFlow(timeMillis = 50).onEach {
            //synchronized(linkedHashMap) { //wrong
            synchronized(synchronizedMap) {
                linkedHashMap[it] = it.toString()
            }
        }.flowOn(Dispatchers.Unconfined).launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            val sb = StringBuilder()
            //synchronized(linkedHashMap) { //wrong
            synchronized(synchronizedMap) {
                //linkedHashMap.filter { entry -> entry.key % 5 == 0 || entry.value.endsWith("6") }
                linkedHashMap.filter { (key, value) -> key % 5 == 0 || value.endsWith("6") }.entries.forEach {
                    sb.append(it).append(" ")
                }
            }
            Log.d(TAG, "entries.forEach: $sb")
        }.launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            //synchronized(linkedHashMap) { //wrong
            synchronized(synchronizedMap) {
                linkedHashMap.remove(it)
            }
        }.flowOn(Dispatchers.IO).launchIn(scope)
    }

    override fun synchronizedMap(scope: CoroutineScope) {
        countDownFlow(timeMillis = 50, isCountDown = false).onEach {
            if (it % 3 == 0 && synchronizedMap.containsKey(it)) {
                synchronizedMap.remove(it)
            }
            synchronizedMap[it] = it.toString()
        }.flowOn(Dispatchers.Unconfined).launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            val sb = StringBuilder()

            synchronizedMap.keys.toList()

            //synchronized(linkedHashMap) { //wrong
            synchronized(synchronizedMap) {
                synchronizedMap.values.toMutableSet()
                synchronizedMap.filter { (key, value) -> key % 5 == 0 || value.endsWith("6") }.entries.forEach {
                    sb.append(it).append(", ")
                }
            }

            synchronized(synchronizedMap) {
                synchronizedMap.filter { (key, value) ->
                    key % 5 == 0 || value.endsWith("6")
                }
            }.forEach {
                sb.append(it).append(" ")
            }
            Log.i(TAG, "synchronizedMap entries.forEach: $sb")
        }.launchIn(scope)

        countDownFlow(timeMillis = 50).onEach {
            synchronized(synchronizedMap) {
                synchronizedMap.keys.toSet()
                synchronizedMap.keys.toMutableSet()
            }

            synchronizedMap.remove(it)

            synchronizedMap.keys.toMutableList()

            val has100 = synchronized(synchronizedMap) {
                synchronizedMap.any { (key, data) -> key % 10 == 0 && data.length == 3 } }
            Log.d(TAG, "remove has100: $has100")
        }.flowOn(Dispatchers.IO).launchIn(scope)
    }

}