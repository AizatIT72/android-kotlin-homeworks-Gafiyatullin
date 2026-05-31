package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.itis.android.homework7.R
import ru.itis.android.homework7.domain.model.Weather

@Composable
fun WeatherInfo(weather: Weather) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "${weather.cityName}, ${weather.countryCode}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        AsyncImage(
            model = "https://openweathermap.org/img/wn/${weather.iconCode}@4x.png",
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = "${weather.temperatureCelsius.toInt()}${stringResource(R.string.unit_celsius)}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherDetailsGrid(weather)
    }
}