package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class WeatherGradient(
    val top: Color,
    val middle: Color,
    val bottom: Color,
    val isDark: Boolean,
)

fun gradientForIcon(iconCode: String): WeatherGradient {
    val isNight = iconCode.endsWith("n")
    val prefix = iconCode.dropLast(1)

    if (isNight) {
        return WeatherGradient(
            top = Color(0xFF0B1026),
            middle = Color(0xFF1B2559),
            bottom = Color(0xFF2A3A7C),
            isDark = true,
        )
    }

    return when (prefix) {
        "01" -> WeatherGradient(
            top = Color(0xFF4A90E2),
            middle = Color(0xFF6FB1FC),
            bottom = Color(0xFFB8DFFF),
            isDark = false,
        )
        "02", "03", "04" -> WeatherGradient(
            top = Color(0xFF5C7CA8),
            middle = Color(0xFF8AA6C9),
            bottom = Color(0xFFC3D4E6),
            isDark = false,
        )
        "09", "10" -> WeatherGradient(
            top = Color(0xFF38506B),
            middle = Color(0xFF5B7591),
            bottom = Color(0xFF93A9C0),
            isDark = false,
        )
        "11" -> WeatherGradient(
            top = Color(0xFF1F2533),
            middle = Color(0xFF3A4255),
            bottom = Color(0xFF5A6378),
            isDark = true,
        )
        "13" -> WeatherGradient(
            top = Color(0xFF7C93B3),
            middle = Color(0xFFB6C8DD),
            bottom = Color(0xFFE8F1FB),
            isDark = false,
        )
        "50" -> WeatherGradient(
            top = Color(0xFF7A8896),
            middle = Color(0xFFA3AFBA),
            bottom = Color(0xFFD2D9DF),
            isDark = false,
        )
        else -> WeatherGradient(
            top = Color(0xFF4A90E2),
            middle = Color(0xFF7FB2F0),
            bottom = Color(0xFFCDE5FF),
            isDark = false,
        )
    }
}

val DefaultWeatherGradient = WeatherGradient(
    top = Color(0xFF5B86E5),
    middle = Color(0xFF7FA8F0),
    bottom = Color(0xFFCFE3FF),
    isDark = false,
)