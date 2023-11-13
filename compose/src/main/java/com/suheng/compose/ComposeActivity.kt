package com.suheng.compose

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(6.dp).verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        Text(
            text = "Hello $name!",
            color = Color(0, 0xFF, 0),
            fontSize = 23.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.background(Color.Yellow).padding(all = 20.dp).width(100.dp)
                .clickable { context.startActivity(Intent(context, ListGridActivity::class.java)) }
        )

        Spacer(Modifier.background(Color.Red).size(10.dp))

        Text(
            modifier = Modifier.height(15.dp).background(Color.Green).padding(horizontal = 6.dp),
            text = "Hello Hello Hello",
            fontFamily = FontFamily.Cursive
        )

        Spacer(Modifier.background(Color.Red).size(8.dp))

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

        Spacer(Modifier.background(Color.Red).size(6.dp))

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
                        BorderStroke(5.dp, Color.Gray),
                        RectangleShape
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
                modifier = Modifier.fillMaxWidth().fillMaxWidth() //fillMaxXxx：占满剩下的空间
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
        Canvas(modifier = Modifier.requiredSize(150.dp, 200.dp).background(Color.Gray)) {
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
            val text = "Txp${String.format("%.2f", textArea)}"
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
fun defaultPreview() {
    structureTheme {
        greeting("Android")
    }
}