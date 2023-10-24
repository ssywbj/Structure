package com.suheng.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suheng.compose.ui.theme.structureTheme

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            structureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    greeting("Android")
                }
            }
        }

    }
}

@Composable
fun greeting(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hello $name!",
            color = Color(0, 0xFF, 0),
            fontSize = 23.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.background(Color.Yellow).padding(all = 20.dp).width(100.dp)
        )

        Spacer(Modifier.background(Color.Red).size(10.dp))

        Text(
            modifier = Modifier.height(15.dp).background(Color.Green).padding(horizontal = 6.dp),
            text = "Hello Hello Hello",
            fontFamily = FontFamily.Cursive
        )

        Spacer(Modifier.background(Color.Red).size(8.dp))

        SelectionContainer {
            Text(
                text = "Selectable Text",
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.background(Color.Red).size(6.dp))

        val inputText = remember { mutableStateOf("Input Text") }
        OutlinedTextField(
            value = inputText.value,
            onValueChange = { inputText.value = it },
            label = { Text("OutlinedTextField") },
        )

        Text(text = inputText.value)

        TextField(
            value = inputText.value,
            onValueChange = { inputText.value = it },
            label = { Text("TextField") },
        )

    }

}

@Preview(showBackground = true)
@Composable
fun defaultPreview() {
    structureTheme {
        greeting("Android")
    }
}