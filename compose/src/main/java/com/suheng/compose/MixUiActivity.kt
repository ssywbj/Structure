package com.suheng.compose

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.suheng.compose.databinding.ActivityMixUiBinding
import com.suheng.compose.databinding.InflateMixUiBinding

class MixUiActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMixUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent {
            MaterialTheme { mixUi() }
        }
    }

    @Composable
    private fun mixUi() {
        val state = remember { mutableStateOf(0) }
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                "TextCompose",
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            AndroidView(factory = { ctx ->
                TextView(ctx).apply {
                    text = "TextView"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    setOnClickListener {
                        text = "${text}${state.value}"
                        state.value++
                    }
                }
            }, modifier = Modifier.align(alignment = Alignment.End).padding(horizontal = 10.dp))

            AndroidView(factory = { ctx ->
                //layoutInflater.inflate(R.layout.activity_mix_ui,null,false)
                View.inflate(ctx, R.layout.inflate_mix_ui, null).apply {
                    findViewById<Button>(R.id.btn).run {
                        setOnClickListener { Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show() }
                    }
                }
            })

            AndroidViewBinding(InflateMixUiBinding::inflate) {
                imageView.setOnClickListener {
                    Toast.makeText(it.context, "Click Image", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}