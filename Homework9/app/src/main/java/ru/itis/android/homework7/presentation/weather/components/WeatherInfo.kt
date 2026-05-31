package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.itis.android.homework7.R
import ru.itis.android.homework7.domain.model.Weather

@Composable
fun WeatherInfo(
    weather: Weather,
    contentColor: Color,
    secondaryColor: Color,
    glass: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "${weather.cityName}, ${weather.countryCode}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
        )

        var appeared by remember(weather.iconCode) { mutableStateOf(false) }
        val iconScale by animateFloatAsState(
            targetValue = if (appeared) 1f else 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
            label = "weather_icon_scale",
        )
        LaunchedEffect(weather.iconCode) { appeared = true }

        AsyncImage(
            model = "https://openweathermap.org/img/wn/${weather.iconCode}@4x.png",
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .scale(iconScale),
        )

        Text(
            text = "${weather.temperatureCelsius.toInt()}${stringResource(R.string.unit_celsius)}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor,
        )

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = secondaryColor,
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherDetailsGrid(
            weather = weather,
            contentColor = contentColor,
            secondaryColor = secondaryColor,
            glass = glass,
        )
    }
}