package com.suheng.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        val listOf = listOf(
                            "The simplest solution would be to add a Spacer with Modifier.weight(1f) between",
                            "what you want to achieve, an example how this might look like",
                            "For example following code",
                            "As I said remaining"
                        ) + ((0..100).map { mit -> mit.toString() })
                        lazyList(listOf)
                    }
                }
            }
        }
    }

    @Composable
    fun lazyList(dataList: List<String>) {
        val context = LocalContext.current
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { Text(text = "First item") }

            items(5) { index ->
                Text(text = "Item: $index")
            }

            items(items = dataList) { data ->
                Row(
                    modifier = Modifier.fillParentMaxWidth().clickable {
                        Toast.makeText(context, " $data", Toast.LENGTH_SHORT).show()
                    }.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(R.drawable.iz0rltfp),
                        contentDescription = "beauty icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(end = 8.dp).requiredSize(54.dp)
                            .clip(CircleShape)
                    )

                    Column {
                        Text(
                            text = data,
                            fontSize = 18.sp,
                            color = Color.Black,
                        )

                        Text(
                            text = data,
                            fontSize = 16.sp,
                            color = Color(0xFF999999),
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "fab icon",
                    )
                }

            }

        }
    }

}