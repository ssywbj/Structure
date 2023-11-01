package com.suheng.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import com.suheng.compose.ui.theme.structureTheme

class ListGridActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            structureTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lazy List, Grid") },
                        )
                    },
                    bottomBar = {
                        BottomAppBar {
                            Text("Bottom Bar")
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            Toast.makeText(this, "fab", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "fab icon"
                            )
                        }
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        //greeting("Android")
                    }
                }
            }
        }
    }

}