package ru.itis.android.homework7.presentation.piechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private const val REQUIRED_TOTAL = 100
private const val FULL_CIRCLE = 360f
private const val START_ANGLE_TOP = -90f
private const val ANGLE_TOP_OFFSET = 90f
private const val NO_SELECTION = -1

private const val SELECTED_BRIGHTNESS = 0.35f
private const val SELECTED_STROKE_SCALE = 1.18f
private const val LABEL_TEXT_RATIO = 0.38f
private const val MIN_SECTOR_SWEEP = 1f

private val DEFAULT_GAP_DEGREES = 4f
private val DEFAULT_STROKE_WIDTH = 48.dp

@Composable
fun PieChartView(
    data: List<Pair<Int, Int>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    gapDegrees: Float = DEFAULT_GAP_DEGREES,
    strokeWidth: Dp = DEFAULT_STROKE_WIDTH,
) {
    val total = data.sumOf { it.second }
    require(total == REQUIRED_TOTAL) {
        "Сумма процентов должна быть равна $REQUIRED_TOTAL, сейчас = $total"
    }
    require(colors.size >= data.size) {
        "Цветов (${colors.size}) меньше, чем секторов (${data.size})"
    }
    for (i in 1 until colors.size) {
        require(colors[i] != colors[i - 1]) {
            "Соседние цвета не должны совпадать (индексы ${i - 1} и $i)"
        }
    }

    var selectedIndex by remember { mutableStateOf(NO_SELECTION) }

    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(
        modifier = modifier
            .pointerInput(data, strokePx) {
                detectTapGestures { tapOffset ->
                    selectedIndex = hitTestSector(
                        tap = tapOffset,
                        canvasSize = size.toSize(),
                        data = data,
                        gapDegrees = gapDegrees,
                        strokePx = strokePx,
                    )
                }
            },
    ) {
        drawDonut(
            data = data,
            colors = colors,
            selectedIndex = selectedIndex,
            gapDegrees = gapDegrees,
            strokePx = strokePx,
        )
    }
}

private fun DrawScope.drawDonut(
    data: List<Pair<Int, Int>>,
    colors: List<Color>,
    selectedIndex: Int,
    gapDegrees: Float,
    strokePx: Float,
) {
    val diameter = min(size.width, size.height) - strokePx
    val topLeft = Offset(
        x = (size.width - diameter) / 2f,
        y = (size.height - diameter) / 2f,
    )
    val arcSize = Size(diameter, diameter)
    val radius = diameter / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    var startAngle = START_ANGLE_TOP

    data.forEachIndexed { index, (_, percent) ->
        val fullSweep = percent / REQUIRED_TOTAL.toFloat() * FULL_CIRCLE
        val sweep = (fullSweep - gapDegrees).coerceAtLeast(MIN_SECTOR_SWEEP)
        val drawStart = startAngle + gapDegrees / 2f

        val baseColor = colors[index]
        val color = if (index == selectedIndex) {
            lerp(baseColor, Color.White, SELECTED_BRIGHTNESS)
        } else {
            baseColor
        }
        val thisStroke = if (index == selectedIndex) strokePx * SELECTED_STROKE_SCALE else strokePx

        drawArc(
            color = color,
            startAngle = drawStart,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = thisStroke),
        )

        val midAngle = drawStart + sweep / 2f
        val midRad = Math.toRadians(midAngle.toDouble())
        val labelX = center.x + (radius * cos(midRad)).toFloat()
        val labelY = center.y + (radius * sin(midRad)).toFloat()

        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                this.color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = strokePx * LABEL_TEXT_RATIO
                isAntiAlias = true
                isFakeBoldText = true
            }
            val yOffset = (paint.descent() + paint.ascent()) / 2f
            drawText("$percent%", labelX, labelY - yOffset, paint)
        }

        startAngle += fullSweep
    }
}

private fun hitTestSector(
    tap: Offset,
    canvasSize: Size,
    data: List<Pair<Int, Int>>,
    gapDegrees: Float,
    strokePx: Float,
): Int {
    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val diameter = min(canvasSize.width, canvasSize.height) - strokePx
    val radius = diameter / 2f

    val dx = tap.x - center.x
    val dy = tap.y - center.y
    val dist = sqrt(dx * dx + dy * dy)

    val inner = radius - strokePx / 2f
    val outer = radius + strokePx / 2f
    if (dist < inner || dist > outer) return NO_SELECTION

    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    angle += ANGLE_TOP_OFFSET
    if (angle < 0f) angle += FULL_CIRCLE

    var startAngle = 0f
    data.forEachIndexed { index, (_, percent) ->
        val fullSweep = percent / REQUIRED_TOTAL.toFloat() * FULL_CIRCLE
        val usableStart = startAngle + gapDegrees / 2f
        val usableEnd = startAngle + fullSweep - gapDegrees / 2f
        if (angle in usableStart..usableEnd) {
            return index
        }
        startAngle += fullSweep
    }
    return NO_SELECTION
}