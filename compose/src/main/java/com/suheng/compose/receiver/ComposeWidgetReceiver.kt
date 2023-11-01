package com.suheng.compose.receiver

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

//https://betterprogramming.pub/building-a-composewidget-using-jetpack-glance-2a65227f9cf2
class ComposeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()
}

class GlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            uiContent()
        }
    }

}

@Composable
private fun uiContent() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Where to?",
            modifier = GlanceModifier.padding(12.dp),
            style = TextStyle(color = ColorProvider(Color.Red))
        )
        Row(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                text = "Home",
                onClick = { },
            )
            Spacer(modifier = GlanceModifier.size(3.dp))
            Button(
                text = "Work",
                onClick = { },
            )
        }
    }
}