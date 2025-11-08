package ru.itis.android.homework3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val RedColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),
    secondary = Color(0xFFEF5350),
    tertiary = Color(0xFFE57373),
    background = Color(0xFFFFEBEE),
    surface = Color(0xFFFFCDD2)
)

val GreenColorScheme = lightColorScheme(
    primary = Color(0xFF388E3C),
    secondary = Color(0xFF4CAF50),
    tertiary = Color(0xFF81C784),
    background = Color(0xFFE8F5E8),
    surface = Color(0xFFC8E6C9)
)

val BlueColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF2196F3),
    tertiary = Color(0xFF64B5F6),
    background = Color(0xFFE3F2FD),
    surface = Color(0xFFBBDEFB)
)

@Composable
fun Homework3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme? = null,
    content: @Composable () -> Unit
) {
    val dynamicColorScheme = colorScheme ?: if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = dynamicColorScheme,
        typography = Typography,
        content = content
    )
}