package com.example.katoapp.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartData(
    val label: String,
    val value: Float
)

private data class ChartConfig(
    val color: Color
)

@Composable
fun DonutChart(
    modifier: Modifier = Modifier ,
    data: List<ChartData> ,
    totalValue: Int
) {
    val animationProgress = remember { Animatable(0f) }

    //warna chart
    val colorMap = mapOf(
        "Teks" to ChartConfig(Color(0xFF009DDC)),
        "Gambar" to ChartConfig(Color(0xFFFCB827)),
        "Video" to ChartConfig(Color(0xFF973D97)),
        "Suara" to ChartConfig(Color(0xFFE03A3E))
    )

    fun getConfig(label: String): ChartConfig {
        return colorMap[label] ?: ChartConfig(Color.Gray)
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
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //title
            Text(
                text = "Kategori Prompt\n"+
                        "yang disalin",
                style = MaterialTheme.typography.titleMedium,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(34.dp))

            //chart
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .rotate(-90f)
                ) {
                    val chartTotal = data.sumOf { it.value.toDouble() }.toFloat()
                    val safetyTotal = if (chartTotal == 0f) 1f else chartTotal
                    var currentStartAngle = 0f

                    data.forEach { item ->
                        val config = getConfig(item.label)
                        val sweepAngle = (item.value / safetyTotal) * 360f * animationProgress.value

                        drawArc(
                            color = config.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(
                                width = 30.dp.toPx(),
                                cap = StrokeCap.Butt
                            )
                        )
                        currentStartAngle += sweepAngle
                    }
                }

                //total
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalValue.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            //category
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        rowItems.forEach { item ->
                            ChartLegendItem(
                                color = getConfig(item.label).color,
                                label = item.label,
                                value = item.value.toInt(),
//                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartLegendItem(
    color: Color,
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .width(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun View() {
    val dummyData = listOf(
        ChartData("Teks", 60f),
        ChartData("Gambar", 40f),
        ChartData("Video", 30f),
        ChartData("Suara", 20f)
    )

    Box(modifier = Modifier.padding(16.dp)) {
        DonutChart(
            totalValue = 150,
            data = dummyData
        )
    }
}



