package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.itis.android.homework7.R
import ru.itis.android.homework7.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherDetailsGrid(weather: Weather) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val visibilityKm = weather.visibilityMeters / 1000

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailCard(
                icon = { Icon(Icons.Default.WaterDrop, stringResource(R.string.cd_humidity_icon)) },
                label = stringResource(R.string.label_humidity),
                value = "${weather.humidity}${stringResource(R.string.unit_percent)}",
                modifier = Modifier.weight(1f),
            )
            WeatherDetailCard(
                icon = { Icon(Icons.Default.Air, stringResource(R.string.cd_wind_icon)) },
                label = stringResource(R.string.label_wind_speed),
                value = "${weather.windSpeedMs} ${stringResource(R.string.unit_wind)}",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailCard(
                icon = { Icon(Icons.Default.Thermostat, stringResource(R.string.cd_thermometer_icon)) },
                label = stringResource(R.string.label_pressure),
                value = "${weather.pressureHpa} ${stringResource(R.string.unit_pressure)}",
                modifier = Modifier.weight(1f),
            )
            WeatherDetailCard(
                icon = { Icon(Icons.Default.Air, stringResource(R.string.cd_wind_icon)) },
                label = stringResource(R.string.label_visibility),
                value = "$visibilityKm ${stringResource(R.string.unit_km)}",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WeatherDetailCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
