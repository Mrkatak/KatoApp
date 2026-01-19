package com.example.katoapp.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BarChartCard(
    modifier: Modifier = Modifier,
    data: List<ChartData>
) {
    val animationProgress = remember { Animatable(0f) }

    //warna chart
    val colorMap = mapOf(
        "Teks" to Color(0xFF009DDC),
        "Gambar" to Color(0xFFFCB827),
        "Video" to Color(0xFF973D97),
        "Suara" to Color(0xFFE03A3E),
    )

    fun getColor(label: String): Color {
        return colorMap[label] ?: Color.Gray
    }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    val safeMax = if (maxValue == 0f) 1f else maxValue

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kategori Prompt\n" +
                        "yang dibuat",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            //chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    //vertical line
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height - 40.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    //horizontal line
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, size.height - 40.dp.toPx()),
                        end = Offset(size.width, size.height - 40.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // bar & angka
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.forEach { item ->
                        val barHeight = (item.value / safeMax) * 160f * animationProgress.value
                        val barColor = getColor(item.label)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            //angka
                            Text(
                                text = item.value.toInt().toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            //bar
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(barHeight.dp)
//                                    .clip(
//                                        RoundedCornerShape(
//                                            topStart = 6.dp,
//                                            topEnd = 6.dp)
//                                    )
                                    .background(barColor)
                            )
                        }
                    }
                }

                Spacer(modifier.height(16.dp))

                //point & label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    data.forEach { item ->
                        val barColor = getColor(item.label)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            //point
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(barColor)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // label
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }







        }





    }
}


@Preview(showBackground = true)
@Composable
private fun View() {
    Box(modifier = Modifier.padding(16.dp)) {
        BarChartCard(
            data = listOf(
                ChartData("Teks", 12f),
                ChartData("Gambar", 25f),
                ChartData("Video", 8f),
                ChartData("Suara", 15f)
            )
        )
    }
}