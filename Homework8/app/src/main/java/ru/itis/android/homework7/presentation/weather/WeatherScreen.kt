package ru.itis.android.homework7.presentation.weather

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.itis.android.homework7.R
import ru.itis.android.homework7.presentation.weather.components.WeatherErrorView
import ru.itis.android.homework7.presentation.weather.components.WeatherInfo
import ru.itis.android.homework7.presentation.weather.components.WeatherSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onCityClick: (String) -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.cacheToast) {
        uiState.cacheToast?.let { isFromCache ->
            val msg = if (isFromCache) context.getString(R.string.toast_from_cache)
            else context.getString(R.string.toast_from_network)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onCacheToastConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.screen_title), fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
                onCityChanged = viewModel::onCityInputChanged,
                onSearch = {
                    keyboardController?.hide()
                    viewModel.onGetWeatherClicked()
                },
                isLoading = uiState.isLoading,
            )
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "weather_content_anim"
            ) { state ->
                when {
                    state.isLoading -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.error != null -> {
                        WeatherErrorView(error = state.error)
                    }
                    state.weather != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WeatherInfo(weather = state.weather)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onCityClick(state.weather.cityName) },
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
                    else -> InitialHint()
                }
            }

            if (uiState.searchHistory.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                SearchHistorySection(
                    history = uiState.searchHistory,
                    onItemClick = onCityClick,
                )
            }
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<String>,
    onItemClick: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recent_searches),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        history.forEach { city ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onItemClick(city) },
                shape = RoundedCornerShape(12.dp),
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
    }
}

@Composable
private fun InitialHint() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.search_city_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}