package com.rpfcoding.stockmarketwithopencsv.presentation.company_info

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rpfcoding.stockmarketwithopencsv.domain.model.IntraDayInfo
import kotlin.math.round
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    intraDayList: List<IntraDayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green
) {

    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }

    val upperValue = remember(intraDayList) {
        (intraDayList.maxOfOrNull { it.close }?.plus(1)?.roundToInt()) ?: 0
    }

    val lowerValue = remember(intraDayList) {
        (intraDayList.minOfOrNull { it.close }?.roundToInt()) ?: 0
    }

    val density = LocalDensity.current

    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / intraDayList.size
        (intraDayList.indices step 2).forEach { i ->
            val info = intraDayList[i]
            val hour = info.date.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }

        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }

        var lastX = 0f

        val strokePath = Path().apply {
            val height = size.height
            for(i in intraDayList.indices) {
                val intraDay = intraDayList[i]
                val nextIntraDay = intraDayList.getOrNull(i + 1) ?: intraDayList.last()

                val leftRatio = (intraDay.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextIntraDay.close - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()

                if(i == 0) {
                    moveTo(x1, y1)
                }

                lastX = (x1 + x2) / 2f

                quadraticBezierTo(
                    x1,
                    y1,
                    lastX,
                    (y1 + y2) / 2f
                )
            }
        }

        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}