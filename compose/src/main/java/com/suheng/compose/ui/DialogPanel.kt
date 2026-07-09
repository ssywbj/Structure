package com.suheng.compose.ui

import android.app.appsearch.Migrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Slider
//import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suheng.compose.R

@Composable
fun DialogPanel() {
    var isShowMusicCard by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .requiredSize(300.dp, 464.dp)
            //.clip(RoundedCornerShape(16.dp))
            .background(
                if (isShowMusicCard) Color.Green else Color.Red, shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        if (isShowMusicCard) {
            MusicCard(onShowDeviceCard = { isShowMusicCard = false })
        } else {
            DeviceCard(onShowMusicCard = { isShowMusicCard = true })
        }
    }
}

@Composable
fun MusicCard(onShowDeviceCard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onShowDeviceCard)
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.girl_gaitubao),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size = 120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Text(
                text = stringResource(R.string.share_text1),
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)
            )
        }

        Text(
            text = stringResource(R.string.share_text2), fontSize = 16.sp, color = Color.Gray,
            modifier = Modifier.padding(top = 10.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCard(onShowMusicCard: () -> Unit) {
    var volume by remember { mutableFloatStateOf(50f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onShowMusicCard)
    ) {
        Image(
            painter = painterResource(id = R.drawable.girl_gaitubao),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
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
            val maxVolume = 100f
            /*Slider(
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
                }
            )

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

/*
Row {
    var volume by remember { mutableFloatStateOf(10f) }
    val minVolume = 0f
    val maxVolume = 100f

    Text(
        text = "${volume.toInt()}",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier
    )
    Slider(
        value = volume,
        onValueChange = { volume = it },
        valueRange = minVolume..maxVolume,
        modifier = Modifier
    )
    Text(
        text = "${maxVolume.toInt()}",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier
    )
}*/
