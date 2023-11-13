package com.suheng.compose

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.suheng.compose.ui.theme.colorsPalette
import com.suheng.compose.ui.theme.structureTheme

class ListGridActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val primaryColor = colorsPalette().primary
            window.statusBarColor = primaryColor.toArgb()

            structureTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lazy List, Grid") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            backgroundColor = primaryColor
                        )
                    },
                    bottomBar = {
                        /*BottomAppBar {
                            Text("Bottom Bar")
                        }*/
                        val selectedIndex = remember { mutableStateOf(0) }
                        BottomNavigation {
                            BottomNavigationItem(
                                icon = { Icon(imageVector = Icons.Default.Home, "Home") },
                                label = { Text(text = "Home") },
                                onClick = { selectedIndex.value = 0 },
                                selected = (selectedIndex.value == 0)
                            )

                            BottomNavigationItem(
                                icon = { Icon(imageVector = Icons.Default.Favorite, "Favorite") },
                                label = { Text(text = "Favorite") },
                                onClick = { selectedIndex.value = 1 },
                                selected = (selectedIndex.value == 1)
                            )

                            BottomNavigationItem(
                                icon = { Icon(imageVector = Icons.Default.Person, "Profile") },
                                label = { Text(text = "Profile") },
                                onClick = { selectedIndex.value = 2 },
                                selected = (selectedIndex.value == 2)
                            )
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
                            "As I said remaining",
                            "1111111111111111111",
                        ) + ((0..8).map { mit -> mit.toString() })
                        //lazyList(listOf)

                        ((0..27).map { mit ->
                            if (mit % 10 == 0) {
                                AdtItem(title = "Title$mit", type = 1)
                            } else {
                                when (mit) {
                                    9 -> AdtItem(contentMore = (0..6).map { moreIndex ->
                                        "more$moreIndex"
                                    })
                                    in 11..19 -> AdtItem(content = "Content$mit", type = 2)
                                    else -> AdtItem(content = "Content$mit")
                                }
                            }
                        }).also { dataList ->
                            lazyListGrid(dataList)
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun lazyList(dataList: List<String>) {
        val context = LocalContext.current
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Text(
                    text = "Title",
                    fontSize = 19.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            item {
                LazyRow(modifier = Modifier.wrapContentHeight()) {
                    items(10) { index ->
                        Text(
                            text = "Row:$index",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.background(
                                color = Color(
                                    (0xFF * (1.0 * index / 10)).toInt(),
                                    0x00,
                                    0x00
                                )
                            ).padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            items(items = dataList) { data ->
                ConstraintLayout(modifier = Modifier.fillParentMaxWidth().clickable {
                    Toast.makeText(context, " $data", Toast.LENGTH_SHORT).show()
                }.padding(10.dp)) {

                    val (avatarRef, titleRef, subtitleRef, describeRef, progressRef, arrowRightRefs) = createRefs()
                    createVerticalChain(
                        titleRef,
                        subtitleRef,
                        progressRef,
                        chainStyle = ChainStyle.Spread
                    )

                    Image(painter = painterResource(id = R.drawable.iz0rltfp),
                        contentDescription = "beauty avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(end = 8.dp).requiredSize(56.dp)
                            .clip(CircleShape)
                            .constrainAs(avatarRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            }
                    )

                    Text(
                        text = data,
                        modifier = Modifier.constrainAs(titleRef) {
                            start.linkTo(avatarRef.end)
                            end.linkTo(describeRef.start)
                            width = Dimension.fillToConstraints
                        },
                        fontSize = 17.sp,
                    )

                    Text(
                        text = data,
                        modifier = Modifier.constrainAs(subtitleRef) {
                            top.linkTo(titleRef.bottom)
                            start.linkTo(titleRef.start)
                            end.linkTo(titleRef.end)
                            width = Dimension.fillToConstraints
                        },
                        style = TextStyle(color = Color(0xFF666666), fontSize = 15.sp)
                    )

                    LinearProgressIndicator(
                        progress = 0.7f,
                        modifier = Modifier.constrainAs(progressRef) {
                            top.linkTo(subtitleRef.bottom)
                            start.linkTo(titleRef.start)
                            end.linkTo(titleRef.end)
                            width = Dimension.fillToConstraints
                        }.padding(top = 8.dp)
                    )

                    Text(
                        text = "describe",
                        modifier = Modifier.constrainAs(describeRef) {
                            top.linkTo(parent.top)
                            end.linkTo(arrowRightRefs.start)
                            bottom.linkTo(parent.bottom)
                        }.padding(start = 12.dp),
                        style = TextStyle(color = Color(0xFF999999), fontSize = 13.sp)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "fab icon",
                        modifier = Modifier.constrainAs(arrowRightRefs) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                    )
                }
            }

        }
    }

    @Composable
    fun lazyListGrid(dataList: List<AdtItem>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            contentPadding = PaddingValues(1.dp)
        ) {
            //LazyVerticalGrid(columns = GridCells.Adaptive(200.dp), modifier = Modifier.fillMaxSize()) {

            itemsIndexed(
                items = dataList,
                key = { index, _ ->
                    index.also {
                        Log.d("Wbj", "lazyListGrid, index: $it")
                    }
                },
                span = { _, item ->
                    when (item.type) {
                        1 -> GridItemSpan(maxLineSpan)
                        2 -> GridItemSpan(maxLineSpan / 2)
                        else -> GridItemSpan(maxLineSpan / 3)
                    }
                },
                contentType = { _, item -> item.type },
                itemContent = { _, item ->
                    if (item.type == 1) {
                        Text(
                            text = item.title,
                            fontSize = 18.sp,
                            modifier = Modifier.background(color = Color.Red)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    } else {
                        if (item.contentMore == null) {
                            Text(
                                text = item.content,
                                fontSize = 16.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.requiredHeight(130.dp).background(
                                    color = Color.Blue
                                )
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier.requiredHeight(130.dp).background(
                                    color = Color.Gray
                                )
                            ) {
                                items(item.contentMore) { moreCtt ->
                                    Text(
                                        text = moreCtt,
                                        fontSize = 14.sp,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.background(color = Color.Black)
                                            .padding(4.dp)
                                    )
                                }
                            }

                        }
                    }
                })
        }
    }

}

data class AdtItem(
    val title: String = "",
    val content: String = "",
    val contentMore: List<String>? = null,
    val type: Int = 0,
)