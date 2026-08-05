package com.suheng.compose.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Slider
//import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suheng.compose.R

private const val KEY_SHARED_ELEMENT_IMAGE = "shared_element_image"
private const val KEY_SHARED_PANEL_BOUNDS = "shared_panel_bounds"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MediaWidget() {
    var showDialog by remember { mutableStateOf(false) }
    var showMusicCard by remember { mutableStateOf(true) }
    var dialogAnimType by remember { mutableIntStateOf(0) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = showDialog,
            label = "MediaWidgetAnimated",
        ) { dialogVisible ->
            Log.d("Wbj", "showDialog: $showDialog, dialogVisible: $dialogVisible")
            if (dialogVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            showDialog = false
                            dialogAnimType = 2
                        },
                    contentAlignment = Alignment.Center
                ) {
                    DialogPanel(
                        showMusicCard = showMusicCard,
                        animType = dialogAnimType,
                        outAnimatedScope = this@AnimatedContent,
                    )
                }
            } else {
                SmartMediaPanel(
                    animatedVisibilityScope = this@AnimatedContent,
                    onAlbumClick = {
                        showDialog = true
                        dialogAnimType = 1
                        showMusicCard = true
                    },
                    onDeviceClick = {
                        showDialog = true
                        dialogAnimType = 1
                        showMusicCard = false
                    })

            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SmartMediaPanel(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAlbumClick: () -> Unit,
    onDeviceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .requiredSize(130.dp)
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = KEY_SHARED_PANEL_BOUNDS),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            )
            .background(Color.Blue, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AlbumImage(
                modifier = Modifier
                    .size(56.dp)
                    .clickable(onClick = onAlbumClick),
                animatedVisibilityScope = animatedVisibilityScope,
                radius = 8,
                isDialogPanelCardSwitchAnimation = false
            )

            Image(
                painter = painterResource(id = android.R.drawable.ic_input_get),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onDeviceClick)
            )
        }

        MediaControlRow(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MediaControlRow(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_media_previous),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(22.dp)
        )

        Image(
            painter = painterResource(id = android.R.drawable.ic_media_play),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(28.dp)
        )

        Image(
            painter = painterResource(id = android.R.drawable.ic_media_next),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(22.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DialogPanel(
    showMusicCard: Boolean = true,
    animType: Int = 0,
    outAnimatedScope: AnimatedVisibilityScope,
) {
    var isShowMusicCard by remember { mutableStateOf(showMusicCard) }
    var invokeOutAnimatedScope by remember(animType) { mutableStateOf(true) }
    var isCardSwitchAnimation by remember { mutableStateOf(!invokeOutAnimatedScope) }

    val bgColor by animateColorAsState(
        targetValue = if (isShowMusicCard) Color.Red else Color.Green,
        animationSpec = tween(300),
        label = "BgColorAnim",
    )

    Box(
        modifier = Modifier
            .requiredSize(300.dp, 464.dp)
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = KEY_SHARED_PANEL_BOUNDS),
                animatedVisibilityScope = outAnimatedScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            )
            .background(
                bgColor/*if (isShowMusicCard) Color.Green else Color.Red*/,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
            .clickable(enabled = false, onClick = {})
    ) {
        AnimatedContent(
            targetState = isShowMusicCard,
            label = "DialogPanelAnimated",
        ) { musicCardVisible ->
            Log.i("Wbj", "isShowMusicCard: $isShowMusicCard, musicCardVisible: $musicCardVisible, animType: $animType, invokeOutAnimatedScope: $invokeOutAnimatedScope")
            if (musicCardVisible) {
                MusicCard(
                    onShowDeviceCard = {
                        isCardSwitchAnimation = true
                        invokeOutAnimatedScope = false
                        isShowMusicCard = false
                    },
                    animatedVisibilityScope = if (invokeOutAnimatedScope) outAnimatedScope else this,
                    isCardSwitchAnimation = isCardSwitchAnimation
                )
            } else {
                DeviceCard(
                    onShowMusicCard = {
                        isCardSwitchAnimation = true
                        invokeOutAnimatedScope = false
                        isShowMusicCard = true
                    },
                    animatedVisibilityScope = if (invokeOutAnimatedScope) outAnimatedScope else this,
                    isCardSwitchAnimation = isCardSwitchAnimation
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DeviceCard(
    onShowMusicCard: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isCardSwitchAnimation: Boolean,
) {
    var isVisible by remember { mutableStateOf(!isCardSwitchAnimation) }
    var isRunning by remember { mutableStateOf(false) }

    val durationMsOut = 400
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (isVisible) 250 else durationMsOut, easing = LinearEasing),
        label = "DeviceAlpha"
    )

    /*val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }*/

    val titleOffsetX by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 160.dp,
        animationSpec = if (isVisible) spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ) else tween(durationMsOut, easing = LinearEasing),
        label = "DeviceTitleOffsetX",
        finishedListener = { fraction ->
            Log.v("Wbj", "DeviceCard finishedListener fraction: $fraction")
            isRunning = false
        }
    )

    //val describeOffsetY = 324.dp
    val describeOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 324.dp,
        animationSpec = if (isVisible) spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ) else tween(durationMsOut, easing = LinearEasing),
        label = "DeviceDescribeOffsetY",
    )

    Log.d("Wbj", "DeviceCard isVisible: $isVisible, alpha: $alpha, offset: $titleOffsetX, $describeOffsetY, isRunning: $isRunning")

    LaunchedEffect(Unit) {
        Log.d("Wbj", "DeviceCard LaunchedEffect: $isCardSwitchAnimation")
        if (isCardSwitchAnimation) {
            isVisible = true
            isRunning = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            AlbumImage(
                modifier = Modifier
                    .size(size = 120.dp)
                    .clickable {
                        if (isRunning) {
                            Log.w("Wbj", "DeviceCard Switch Animation is Running")
                            return@clickable
                        }
                        if (isVisible) {
                            isVisible = false
                            isRunning = true
                            onShowMusicCard()
                        }
                    },
                animatedVisibilityScope = animatedVisibilityScope,
                radius = 8,
                isDialogPanelCardSwitchAnimation = isCardSwitchAnimation
            )

            Text(
                text = stringResource(R.string.share_text1),
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)
                    .offset(x = titleOffsetX)
                    .alpha(alpha)
            )
        }

        Text(
            text = stringResource(R.string.share_text2),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 10.dp)
                .alpha(alpha)
                .offset(y = describeOffsetY)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MusicCard(
    onShowDeviceCard: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isCardSwitchAnimation: Boolean,
) {
    var volume by remember { mutableFloatStateOf(10f) }
    var isVisible by remember { mutableStateOf(!isCardSwitchAnimation) }
    var isRunning by remember { mutableStateOf(false) }

    val durationMsOut = 400
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (isVisible) 200 else durationMsOut, easing = LinearEasing),
        label = "MusicAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = if (isVisible) spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ) else tween(
            durationMillis = if (isVisible) 200 else durationMsOut,
            easing = LinearEasing
        ),
        label = "MusicScale",
        finishedListener = { fraction ->
            Log.v("Wbj", "MusicCard finishedListener fraction: $fraction")
            isRunning = false
        }
    )

    LaunchedEffect(Unit) {
        Log.i("Wbj", "MusicCard LaunchedEffect: $isCardSwitchAnimation")
        if (isCardSwitchAnimation) {
            isVisible = true
            isRunning = true
        }
    }

    Log.i("Wbj", "MusicCard isVisible: $isVisible, alpha: $alpha, scale: $scale, isRunning: $isRunning")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AlbumImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable {
                    if (isRunning) {
                        Log.w("Wbj", "MusicCard Switch Animation is Running")
                        return@clickable
                    }
                    if (isVisible) {
                        isVisible = false
                        isRunning = true
                        onShowDeviceCard()
                    }
                },
            animatedVisibilityScope = animatedVisibilityScope,
            radius = 16,
            isDialogPanelCardSwitchAnimation = isCardSwitchAnimation
        )

        Text(
            text = stringResource(R.string.share_text1),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 10.dp)
                .background(Color.Cyan)
                .alpha(alpha)
                .scale(scale)
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .alpha(alpha)
                .scale(scale)
        ) {
            val textWidth = 26.dp
            Text(
                text = "${volume.toInt()}",
                fontSize = 15.sp,
                color = Color.Black,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .width(textWidth)
                    .background(Color.Blue),
            )

            val minVolume = 0f
            val maxVolume = 100f
            /*Slider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = minVolume..maxVolume,
                modifier = Modifier.weight(1f)
            )*/
            val trackHeight = 8
            Slider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = minVolume..maxVolume,
                modifier = Modifier
                    .weight(1f)
                    .height(trackHeight.dp),
                thumb = {},
                track = { sliderState ->
                    //val fraction = sliderState.coercedValueAsFraction
                    val fraction = ((volume - minVolume) / (maxVolume - minVolume)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trackHeight.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFBDBDBD))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                //.background(Color.Yellow, CircleShape)
                                .background(Color.Yellow)
                        )
                    }
                })

            Text(
                text = "${maxVolume.toInt()}",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .width(textWidth)
                    .background(Color.Blue),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.AlbumImage(
    modifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    radius: Int,
    isDialogPanelCardSwitchAnimation: Boolean = false,
) {
    val sharedContentState = rememberSharedContentState(KEY_SHARED_ELEMENT_IMAGE)

    Image(
        painter = painterResource(id = R.drawable.girl_gaitubao),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(radius.dp))
            .let {
                if (isDialogPanelCardSwitchAnimation) {
                    it.sharedElement(
                        state = sharedContentState,
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(radius.dp)),
                        boundsTransform = { _, _ ->
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                                visibilityThreshold = Rect.VisibilityThreshold
                            )
                        }
                    )
                } else {
                    it.sharedElement(
                        state = sharedContentState,
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(radius.dp))
                    )
                }
            }
    )
}