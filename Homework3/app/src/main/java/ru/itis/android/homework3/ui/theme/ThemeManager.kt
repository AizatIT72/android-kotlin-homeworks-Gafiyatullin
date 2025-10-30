package ru.itis.android.homework3.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ru.itis.android.homework3.R

data class ThemeOption(
    val nameResId: Int,
    val colorScheme: ColorScheme
)

object ThemeManager {
    @Composable
    fun getThemeOptions(): List<ThemeOption> {
        return listOf(
            ThemeOption(R.string.theme_red, RedColorScheme),
            ThemeOption(R.string.theme_green, GreenColorScheme),
            ThemeOption(R.string.theme_blue, BlueColorScheme)
        )
    }

    @Composable
    fun rememberThemeState(): MutableState<ColorScheme> {
        return remember { mutableStateOf(BlueColorScheme) }
    }
}