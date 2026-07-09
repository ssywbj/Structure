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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suheng.compose.R

private const val KEY_SHARED_ELEMENT_IMAGE = "image"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DialogPanel() {
    var isShowMusicCard by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isShowMusicCard) Color.Green else Color.Red,
        animationSpec = tween(300),
        label = "BgColorAnim",
    )

    Box(
        modifier = Modifier
            .requiredSize(300.dp, 464.dp)
            .background(
                bgColor/*if (isShowMusicCard) Color.Green else Color.Red*/,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = isShowMusicCard,
                label = "DeviceMusicAnim",
                /*transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                        animationSpec = tween(220)
                    )
                },*/
            ) { isMusicCard ->
                //Log.i("Wbj", "isShowMusicCard: $isShowMusicCard, isMusicCard: $isMusicCard")
                if (isMusicCard) {
                    MusicCard(onShowDeviceCard = { isShowMusicCard = false }, this)
                } else {
                    DeviceCard(onShowMusicCard = { isShowMusicCard = true }, this)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MusicCard(
    onShowDeviceCard: () -> Unit, animatedVisibilityScope: AnimatedVisibilityScope
) {
    var isVisible by remember { mutableStateOf(false) }

    val durationMsOut = 400
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (isVisible) 250 else durationMsOut, easing = LinearEasing),
        label = "MusicAlpha"
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
        label = "MusicTitleOffsetX",
    )

    //val describeOffsetY = 324.dp
    val describeOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 324.dp,
        animationSpec = if (isVisible) spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ) else tween(durationMsOut, easing = LinearEasing),
        label = "MusicDescribeOffsetY",
    )

    Log.d("Wbj", "isVisible: $isVisible, $alpha, offset: $titleOffsetX, $describeOffsetY")

    LaunchedEffect(Unit) {
        Log.i("Wbj", "MusicCard LaunchedEffect")
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            //.clickable(onClick = onShowDeviceCard)
            .clickable {
                if (isVisible) {
                    isVisible = false
                    onShowDeviceCard()
                }
            }
    ) {
        Row {
            AlbumImage(Modifier.size(size = 120.dp), 8, animatedVisibilityScope)

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
fun SharedTransitionScope.DeviceCard(
    onShowMusicCard: () -> Unit, animatedVisibilityScope: AnimatedVisibilityScope
) {
    var volume by remember { mutableFloatStateOf(50f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onShowMusicCard)
    ) {
        AlbumImage(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f), 16, animatedVisibilityScope
        )

        Text(
            text = stringResource(R.string.share_text1),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 10.dp)
                .background(Color.Cyan)
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
            val maxVolume = 100f/*Slider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = minVolume..maxVolume,
                modifier = Modifier.weight(1f)
            )*/
            val trackHeight = 8
            val trackRadius = (trackHeight + 1) / 2
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
                            .background(Color(0xFFBDBDBD), RoundedCornerShape(trackRadius.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(trackHeight.dp)
                                .background(Color.Yellow, RoundedCornerShape(trackRadius.dp))
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
    radius: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Image(
        painter = painterResource(id = R.drawable.girl_gaitubao),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(radius.dp))
            .sharedElement(
                state = rememberSharedContentState(KEY_SHARED_ELEMENT_IMAGE),
                animatedVisibilityScope = animatedVisibilityScope,
                /*clipInOverlayDuringTransition = object : SharedTransitionScope.OverlayClip {
                    override fun getClipPath(
                        state: SharedTransitionScope.SharedContentState,
                        bounds: Rect,
                        layoutDirection: LayoutDirection,
                        density: Density
                    ): Path? {
                        return null
                    }
                },*/
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(radius.dp)),
                boundsTransform = { _, _ ->
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = Rect.VisibilityThreshold
                    )
                    //tween(1700)
                }
            )
    )
}