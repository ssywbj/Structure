package com.suheng.structure.view.kt

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun TextView.textChangedFlow(): Flow<CharSequence> = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //trySend(s)
            Log.d("Wbj", "onTextChanged: s:$s, start: $start, before: $before, count: $count")
            trySendBlocking(s)
        }

        override fun afterTextChanged(s: Editable) {
            Log.i("Wbj", "afterTextChanged: s:$s")
        }
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}