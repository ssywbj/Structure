package com.suheng.compose

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suheng.compose.ui.theme.StructureTheme
import java.util.Locale
import kotlin.math.roundToInt

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StructureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                    //SharedElements()

                    /*var isShowMusicCard by remember {
                        mutableStateOf(true)
                    }
                    if (isShowMusicCard) {
                        MusicCard(onShowDeviceCard = { isShowMusicCard = false })
                    } else {
                        DeviceCard(onShowMusicCard = { isShowMusicCard = true })
                    }*/
                    //MediaWidget()
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElements() {
    var showDetails by remember {
        mutableStateOf(false)
    }

    /*if (showDetails) {
        DetailsScreen(onBack = { showDetails = false })
    } else {
        MainScreen(onShowDetails = { showDetails = true })
    }*/

    SharedTransitionLayout {
        /*if (showDetails) {
            DetailsScreen(onBack = { showDetails = false })
        } else {
            MainScreen(onShowDetails = { showDetails = true })
        }*/
        AnimatedContent(targetState = showDetails, label = "") { inDetails ->
            if (inDetails) {
                DetailsScreen(
                    onBack = { showDetails = false },
                    animatedVisibilityScope = this@AnimatedContent
                )
            } else {
                MainScreen(
                    onShowDetails = { showDetails = true },
                    animatedVisibilityScope = this@AnimatedContent
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
//private fun MainScreen(onShowDetails: () -> Unit, modifier: Modifier = Modifier) {
private fun SharedTransitionScope.MainScreen(
    onShowDetails: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Row(
            modifier = modifier
                .padding(all = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "bounds"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = RemeasureToBounds
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(color = Color.Red, shape = RoundedCornerShape(8.dp))
                .clickable { onShowDetails() }
                .padding(all = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao), contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(size = 100.dp)
            )
            Text(
                text = stringResource(R.string.share_text1),
                fontSize = 21.sp,
                modifier = Modifier/*.sharedElement(
                    state = rememberSharedContentState(key = "title"),
                    animatedVisibilityScope = animatedVisibilityScope
                )*/.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "title"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
//private fun DetailsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
private fun SharedTransitionScope.DetailsScreen(
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "bounds"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = RemeasureToBounds
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(color = Color.Green, shape = RoundedCornerShape(8.dp))
                .clickable { onBack() }
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao), contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(200.dp)
            )
            Text(
                text = stringResource(R.string.share_text1),
                fontSize = 28.sp,
                modifier = Modifier/*.sharedElement(
                    state = rememberSharedContentState(key = "title"),
                    animatedVisibilityScope = animatedVisibilityScope
                )*/.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "title"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            )
            Text(text = stringResource(R.string.share_text2))
        }
    }
}

private val PADDING_HORIZONTAL = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Greeting(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = PADDING_HORIZONTAL)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        Text(
            text = "Hello $name!",
            color = Color(0, 0xFF, 0),
            fontSize = 23.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.Yellow)
                .padding(all = 20.dp)
                .width(100.dp)
                .clickable { context.startActivity(Intent(context, ListGridActivity::class.java)) }
        )

        Spacer(
            Modifier
                .background(Color.Red)
                .size(10.dp)
        )

        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
        val density = LocalDensity.current
        val maxOverScrollPx = with(density) { 80.dp.toPx() }
        // 从收起态展开：超过该距离（或甩动）就吸附打开
        val openThresholdPx = with(density) { 24.dp.toPx() }
        // 从展开态复位：往回拖到该距离以内就吸附关闭（越大越容易复位）
        // 复位所需拖动距离 ≈ maxOverScrollPx - closeThresholdPx
        val closeThresholdPx = with(density) { 56.dp.toPx() }
        val overTranslateX = remember { mutableFloatStateOf(0f) }
        val isRevealed = remember { mutableStateOf(false) }
        val nestedScrollConnection = remember(
            maxOverScrollPx,
            openThresholdPx,
            closeThresholdPx,
            pagerState
        ) {
            object : NestedScrollConnection {
                private fun consumeHorizontal(delta: Float): Float {
                    if (delta == 0f) return 0f
                    val current = overTranslateX.floatValue
                    val min = if (current < 0f || !pagerState.canScrollForward) -maxOverScrollPx else 0f
                    val max = if (current > 0f || !pagerState.canScrollBackward) maxOverScrollPx else 0f
                    // 1.2x 放大跟手量，边界过滑更灵敏
                    val new = (current + delta * 1.2f).coerceIn(min, max)
                    val used = new - current
                    if (used != 0f) overTranslateX.floatValue = new
                    // 按实际消耗的手势量回报，避免子组件再二次消费
                    return if (used == 0f) 0f else delta
                }

                private fun isDrag(source: NestedScrollSource): Boolean {
                    return source == NestedScrollSource.UserInput ||
                            source == NestedScrollSource.Drag
                }

                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (!isDrag(source) || overTranslateX.floatValue == 0f) return Offset.Zero
                    return Offset(consumeHorizontal(available.x), 0f)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (!isDrag(source)) return Offset.Zero
                    return Offset(consumeHorizontal(available.x), 0f)
                }

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                    val current = overTranslateX.floatValue
                    val flingOpenLeft = available.x < -400f
                    val flingOpenRight = available.x > 400f
                    val flingClose = when {
                        current < 0f -> available.x > 400f
                        current > 0f -> available.x < -400f
                        else -> false
                    }
                    val target = when {
                        // 已展开：往回拖过 closeThreshold，或反向甩动 → 复位
                        isRevealed.value -> when {
                            flingClose -> 0f
                            current < 0f && current > -closeThresholdPx -> 0f
                            current > 0f && current < closeThresholdPx -> 0f
                            current < 0f -> -maxOverScrollPx
                            current > 0f -> maxOverScrollPx
                            else -> 0f
                        }
                        // 未展开：超过 openThreshold，或甩动 → 打开
                        flingOpenLeft || current < -openThresholdPx -> -maxOverScrollPx
                        flingOpenRight || current > openThresholdPx -> maxOverScrollPx
                        else -> 0f
                    }
                    isRevealed.value = target != 0f
                    Animatable(current).animateTo(
                        targetValue = target,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) {
                        overTranslateX.floatValue = value
                    }
                    return Velocity(available.x, 0f)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .nestedScroll(nestedScrollConnection)
        ) {
            val btnHorizontalMargin = 26.dp
            Text(
                text = "删除",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = btnHorizontalMargin)
                    .requiredSize(50.dp)
                    .background(Color.Red)
                    .align(Alignment.CenterStart)
                    .clickable {
                        Toast.makeText(context, "删除", Toast.LENGTH_SHORT).show()
                    }
            )

            Text(
                text = "删除",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(end = btnHorizontalMargin)
                    .requiredSize(50.dp)
                    .background(Color.Blue)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                    }
            )

            HorizontalPager(
                pagerState,
                contentPadding = PaddingValues(horizontal = PADDING_HORIZONTAL),
                pageSpacing = PADDING_HORIZONTAL,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(200.dp)
                    //.clip(RoundedCornerShape(20.dp))
                    .offset { IntOffset(overTranslateX.floatValue.roundToInt(), 0) }
            ) { pageIndex ->
                Text(
                    text = "HorizontalPager-$pageIndex",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                )
            }
        }

        Spacer(
            Modifier
                .background(Color.Red)
                .size(10.dp)
        )

        Text(
            modifier = Modifier
                .height(15.dp)
                .background(Color.Green)
                .padding(horizontal = 6.dp),
            text = "Hello Hello Hello",
            fontFamily = FontFamily.Cursive
        )

        Spacer(
            Modifier
                .background(Color.Red)
                .size(8.dp)
        )

        val clickText = remember { mutableStateOf("ClickableText") }
        ClickableText(
            text = AnnotatedString(clickText.value),
            style = TextStyle(
                color = Color.DarkGray,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            clickText.value = "$it"
            Toast.makeText(context, "${clickText.value}, it: $it", Toast.LENGTH_SHORT).show()
        }

        Spacer(
            Modifier
                .background(Color.Red)
                .size(6.dp)
        )

        SelectionContainer {
            Text(
                text = "Selectable Text",
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(
            Modifier
                .background(Color.Red)
                .size(6.dp)
        )

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

        val btnText = remember { mutableStateOf("Click") }
        Button(onClick = {
            Toast.makeText(context, btnText.value, Toast.LENGTH_SHORT).show()
            btnText.value = btnText.value + "1"
        }) {
            Text(btnText.value)
        }

        Button(
            onClick = { context.startActivity(Intent(context, MixUiActivity::class.java)) },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Green),
            shape = CircleShape,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 20.dp, pressedElevation = 5.dp, disabledElevation = 0.dp
            )
        ) {
            Text("Button")
        }

        Spacer(Modifier.size(10.dp))

        Button(
            onClick = {},
            enabled = false,
            contentPadding = PaddingValues(horizontal = 4.dp),
            border = BorderStroke(2.dp, Color.Red)
        ) {
            Text("disabled")
        }

        Image(
            painter = painterResource(R.drawable.letter_a),
            contentDescription = null
        )

        Spacer(Modifier.size(10.dp))

        Row {
            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(10.dp)),
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.clip(CutCornerShape(10.dp)),
                alpha = 0.4f
            )
        }

        Spacer(Modifier.size(4.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.clip(CircleShape)
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.border(BorderStroke(6.dp, Color.Green))
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.border(
                    BorderStroke(
                        6.dp,
                        Brush.horizontalGradient(listOf(Color.Red, Color.Green))
                    )
                )
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.border(
                    BorderStroke(
                        6.dp,
                        Brush.sweepGradient(
                            listOf(
                                Color.Red,
                                Color(0xFF00FF00),
                                Color(0, 0, 0, 0xFF),
                                Color(0xFF0000FF),
                                Color.Red
                            )
                        )
                    )
                )
            )

            Image(
                painter = painterResource(R.drawable.girl_gaitubao),
                contentDescription = null,
                modifier = Modifier.blur(10.dp, BlurredEdgeTreatment(RoundedCornerShape(10.dp)))
            )
        }

        Spacer(Modifier.size(4.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Box {
                Row {
                    Image(
                        painter = painterResource(R.drawable.girl_gaitubao),
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    1f, 0f, 0f, 0f, 0f,
                                    0f, 0f, 0f, 0f, 0f,
                                    0f, 0f, 0f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    )

                    Image(
                        painter = painterResource(R.drawable.girl_gaitubao),
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    0f, 0f, 0f, 0f, 0f,
                                    0f, 1f, 0f, 0f, 0f,
                                    0f, 0f, 1f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    )

                    Image(
                        painter = painterResource(R.drawable.girl_gaitubao),
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    0f, 1f, 0f, 0f, 0f,
                                    1f, 0f, 0f, 0f, 0f,
                                    0f, 0f, 1f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    )

                    Image(
                        painter = painterResource(R.drawable.girl_gaitubao),
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    1f, 0f, 0f, 0f, 100f,
                                    0f, 1f, 0f, 0f, 0f,
                                    0f, 0f, 1f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    )
                }

                Text(
                    text = "Color Style",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.size(6.dp))

            var contrast = 0.6f
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            contrast, 0f, 0f, 0f, 0f,
                            0f, contrast, 0f, 0f, 0f,
                            0f, 0f, contrast, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            )

            contrast = 1.7f
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            contrast, 0f, 0f, 0f, 0f,
                            0f, contrast, 0f, 0f, 0f,
                            0f, 0f, contrast, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            )

            Spacer(Modifier.size(6.dp))

            var brightness = -50f
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, brightness,
                            0f, 1f, 0f, 0f, brightness,
                            0f, 0f, 1f, 0f, brightness,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            )

            brightness = 100f
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, brightness,
                            0f, 1f, 0f, 0f, brightness,
                            0f, 0f, 1f, 0f, brightness,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            )

            Spacer(Modifier.size(6.dp))

            val alpha = 0.4f
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0f, 1f, 0f, 0f,
                            0f, 0f, 0f, alpha, 0f
                        )
                    )
                )
            )

            Spacer(Modifier.size(6.dp))

            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            )
        }

        Spacer(Modifier.size(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        BorderStroke(5.dp, Color.Gray), RectangleShape
                    )
            )

            Spacer(Modifier.size(4.dp))

            val modifier =
                Modifier.border(BorderStroke(5.dp, Color.Gray), RectangleShape)
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(90.dp)
            )

            Spacer(Modifier.size(4.dp))

            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = modifier.size(100.dp)
            )

            Spacer(Modifier.size(4.dp))

            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = modifier.size(120.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Card(elevation = 6.dp) {
                Column(modifier = Modifier.padding(all = 2.dp)) {
                    Text("AB CDE", fontWeight = FontWeight.W700)
                    Text("+0 12345678")
                    Text("XYZ city.", color = Color.Gray)
                }
            }

            Spacer(Modifier.size(6.dp))

            Card(
                elevation = 2.dp,
                contentColor = Color.Blue,
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.girl_gaitubao),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Column(modifier = Modifier.padding(all = 4.dp)) {
                        Text("AB CDE", fontWeight = FontWeight.W700)
                        Text("+0 12345678")
                        Text("XYZ city.", color = Color.Red)
                    }
                }
            }

            Spacer(Modifier.size(6.dp))

            Card(
                elevation = 0.dp,
                backgroundColor = Color.Green,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth() //fillMaxXxx：占满剩下的空间
            ) {
                Column(modifier = Modifier.padding(all = 10.dp)) {
                    Text("AB CDE", fontWeight = FontWeight.W700)
                    Text("+0 12345678")
                    Text("XYZ city.", color = Color.Red)
                }
            }
        }

        Spacer(Modifier.size(10.dp))

        val textMeasurer = rememberTextMeasurer()
        //Canvas(modifier = Modifier.width(150.dp).height(200.dp).background(Color.Gray)) {
        Canvas(
            modifier = Modifier
                .requiredSize(150.dp, 200.dp)
                .background(Color.Gray)
        ) {
            val quadrantSize = size / 2f
            drawCircle(color = Color.Green)
            drawRect(color = Color.Magenta, size = (quadrantSize))

            val strokeWidth = 10.dp.toPx()
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(Color.Red, Color.Green, Color.Red),
                ),
                style = Stroke(
                    width = strokeWidth
                ),
                radius = (size.minDimension - strokeWidth) / 2f
            )

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLUE
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    18f,
                    context.resources.displayMetrics
                )
            }
            val textArea = paint.descent() + paint.ascent()
            val text = "Txp${String.format(Locale.getDefault(), "%.2f", textArea)}"
            val nativeCanvas = drawContext.canvas.nativeCanvas
            nativeCanvas.save()
            nativeCanvas.translate(size.width / 2f, size.height / 2f)
            //nativeCanvas.drawText(text, -paint.measureText(text) / 2f, -textArea / 2f, paint) //居中
            nativeCanvas.drawText(text, -paint.measureText(text) / 2f, textArea * 1.2f, paint)
            nativeCanvas.restore()

            translate(size.width / 2f, size.height / 2f) {
                val textStyle = TextStyle(fontSize = 18.sp, color = Color.White)
                val textSize = textMeasurer.measure(text, style = textStyle).size
                drawText(
                    textMeasurer = textMeasurer,
                    text,
                    //topLeft = Offset(-textSize.center.x.toFloat(), -textSize.center.y.toFloat()),
                    topLeft = Offset(-textSize.width / 2f, -textSize.height / 2f),
                    //style = textStyle.copy(color = Color.White),
                    style = textStyle,
                )

                drawLine(
                    start = Offset(0f, -size.height / 2),
                    end = Offset(0f, size.height),
                    color = Color.Black
                )
            }

            drawLine(
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                color = Color.LightGray
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StructureTheme {
        Greeting("Android")
    }
}