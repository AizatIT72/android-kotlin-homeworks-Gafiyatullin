package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedWeatherBackground(
    gradient: WeatherGradient,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val top by animateColorAsState(
        targetValue = gradient.top,
        animationSpec = tween(900),
        label = "grad_top",
    )
    val middle by animateColorAsState(
        targetValue = gradient.middle,
        animationSpec = tween(900),
        label = "grad_middle",
    )
    val bottom by animateColorAsState(
        targetValue = gradient.bottom,
        animationSpec = tween(900),
        label = "grad_bottom",
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer_shift",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val h = size.height
                val w = size.width
                val startY = -h * 0.2f + h * 0.4f * shift
                val endY = h + h * 0.2f * (1f - shift)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(top, middle, bottom),
                        start = Offset(w * shift * 0.3f, startY),
                        end = Offset(w * (1f - shift * 0.3f), endY),
                    ),
                )
            },
    ) {
        content()
    }
}

fun glassColor(isDark: Boolean): Color =
    if (isDark) Color.White.copy(alpha = 0.12f)
    else Color.White.copy(alpha = 0.30f)

fun onGradientColor(isDark: Boolean): Color =
    if (isDark) Color.White else Color(0xFF0E2235)

fun onGradientSecondaryColor(isDark: Boolean): Color =
    if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF0E2235).copy(alpha = 0.7f)