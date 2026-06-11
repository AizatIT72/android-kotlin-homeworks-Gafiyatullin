package ru.itis.android.homework7.presentation.weather

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.itis.android.homework7.R
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.presentation.weather.components.AnimatedWeatherBackground
import ru.itis.android.homework7.presentation.weather.components.DefaultWeatherGradient
import ru.itis.android.homework7.presentation.weather.components.WeatherErrorView
import ru.itis.android.homework7.presentation.weather.components.WeatherInfo
import ru.itis.android.homework7.presentation.weather.components.WeatherSearchBar
import ru.itis.android.homework7.presentation.weather.components.glassColor
import ru.itis.android.homework7.presentation.weather.components.gradientForIcon
import ru.itis.android.homework7.presentation.weather.components.onGradientColor
import ru.itis.android.homework7.presentation.weather.components.onGradientSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onCityClick: (String) -> Unit,
    onOpenPieChart: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val onCityChanged = remember(viewModel) { viewModel::onCityInputChanged }
    val onSearch = remember(viewModel, keyboardController) {
        {
            keyboardController?.hide()
            viewModel.onGetWeatherClicked()
        }
    }

    LaunchedEffect(uiState.cacheToast) {
        uiState.cacheToast?.let { isFromCache ->
            val msg = if (isFromCache) context.getString(R.string.toast_from_cache)
            else context.getString(R.string.toast_from_network)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onCacheToastConsumed()
        }
    }

    val gradient = uiState.weather?.let { gradientForIcon(it.iconCode) } ?: DefaultWeatherGradient
    val contentColor = onGradientColor(gradient.isDark)
    val secondaryColor = onGradientSecondaryColor(gradient.isDark)

    AnimatedWeatherBackground(gradient = gradient) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.screen_title), fontWeight = FontWeight.Bold)
                    },
                    actions = {
                        IconButton(onClick = onOpenPieChart) {
                            Icon(
                                Icons.Filled.PieChart,
                                contentDescription = "Круговая диаграмма",
                                tint = contentColor,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = contentColor,
                    ),
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WeatherSearchBar(
                    city = uiState.cityInput,
                    onCityChanged = onCityChanged,
                    onSearch = onSearch,
                    isLoading = uiState.isLoading,
                )
                Spacer(modifier = Modifier.height(16.dp))

                WeatherContent(
                    phase = uiState.contentPhase,
                    weather = uiState.weather,
                    error = uiState.error,
                    contentColor = contentColor,
                    secondaryColor = secondaryColor,
                    glass = glassColor(gradient.isDark),
                    onOpenDetails = onCityClick,
                )

                SearchHistorySection(
                    history = uiState.searchHistory,
                    contentColor = contentColor,
                    glass = glassColor(gradient.isDark),
                    onItemClick = onCityClick,
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(
    phase: WeatherContentPhase,
    weather: Weather?,
    error: WeatherError?,
    contentColor: Color,
    secondaryColor: Color,
    glass: Color,
    onOpenDetails: (String) -> Unit,
) {
    AnimatedContent(
        targetState = phase,
        transitionSpec = {
            (fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 6 })
                .togetherWith(fadeOut(tween(160)))
        },
        label = "weather_content_anim",
    ) { targetPhase ->
        when (targetPhase) {
            WeatherContentPhase.Loading -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = contentColor)
                }
            }

            WeatherContentPhase.Error -> {
                error?.let { WeatherErrorView(error = it) }
            }

            WeatherContentPhase.Success -> {
                weather?.let { w ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WeatherInfo(
                            weather = w,
                            contentColor = contentColor,
                            secondaryColor = secondaryColor,
                            glass = glass,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onOpenDetails(w.cityName) },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(stringResource(R.string.button_open_details))
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }

            WeatherContentPhase.Initial -> InitialHint(secondaryColor)
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<String>,
    contentColor: Color,
    glass: Color,
    onItemClick: (String) -> Unit,
) {
    if (history.isEmpty()) return

    Spacer(Modifier.height(24.dp))
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recent_searches),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
        )
        Spacer(Modifier.height(8.dp))
        history.forEach { city ->
            androidx.compose.runtime.key(city) {
                HistoryRow(city = city, contentColor = contentColor, glass = glass, onClick = onItemClick)
            }
        }
    }
}

@Composable
private fun HistoryRow(
    city: String,
    contentColor: Color,
    glass: Color,
    onClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(city) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = glass, contentColor = contentColor),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(city, Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun InitialHint(secondaryColor: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.search_city_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = secondaryColor,
        )
    }
}