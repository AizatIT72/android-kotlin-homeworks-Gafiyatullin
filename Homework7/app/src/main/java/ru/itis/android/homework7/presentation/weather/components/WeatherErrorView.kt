package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.itis.android.homework7.R
import ru.itis.android.homework7.presentation.weather.WeatherError

@Composable
fun WeatherErrorView(error: WeatherError) {
    val message = when (error) {
        WeatherError.EmptyCity -> stringResource(R.string.error_empty_city)
        WeatherError.Network -> stringResource(R.string.error_network)
        WeatherError.CityNotFound -> stringResource(R.string.error_city_not_found)
        is WeatherError.Server -> stringResource(R.string.error_server, error.code)
        WeatherError.Unknown -> stringResource(R.string.error_unknown)
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}