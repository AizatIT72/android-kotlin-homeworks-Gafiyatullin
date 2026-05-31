package ru.itis.android.homework7.presentation.weather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.usecase.GetWeatherUseCase
import javax.inject.Inject

private const val KEY_CITY = "weather_city"

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        val savedCity = savedStateHandle.get<String>(KEY_CITY).orEmpty()
        if (savedCity.isNotBlank()) {
            _uiState.update { it.copy(cityInput = savedCity) }
            fetchWeather(savedCity)
        }
    }

    fun onCityInputChanged(value: String) {
        savedStateHandle[KEY_CITY] = value
        _uiState.update { it.copy(cityInput = value, error = null) }
    }

    fun onGetWeatherClicked() {
        fetchWeather(_uiState.value.cityInput)
    }

    fun onCacheToastConsumed() {
        _uiState.update { it.copy(cacheToast = null) }
    }

    private fun fetchWeather(city: String) {
        if (city.isBlank()) {
            _uiState.update { it.copy(error = WeatherError.EmptyCity) }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            getWeatherUseCase(city)
                .onSuccess { (weather, isFromCache) ->
                    _uiState.update { state ->
                        val updatedHistory = (listOf(weather.cityName) + state.searchHistory)
                            .distinctBy { it.lowercase() }
                            .take(10)
                        state.copy(
                            weather = weather,
                            isLoading = false,
                            cacheToast = isFromCache,
                            searchHistory = updatedHistory,
                        )
                    }
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isLoading = false, error = mapThrowableToError(t)) }
                }
        }
    }

    private fun mapThrowableToError(throwable: Throwable): WeatherError {
        return when (throwable) {
            is java.net.UnknownHostException,
            is java.net.ConnectException,
            is java.net.SocketTimeoutException -> WeatherError.Network
            is retrofit2.HttpException -> {
                val code = throwable.code()
                if (code == 404) WeatherError.CityNotFound
                else WeatherError.Server(code.toString())
            }
            else -> WeatherError.Unknown
        }
    }
}
@Immutable
data class WeatherUiState(
    val cityInput: String = "",
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: WeatherError? = null,
    val cacheToast: Boolean? = null,
    val searchHistory: List<String> = emptyList(),
) {
    val contentPhase: WeatherContentPhase
        get() = when {
            isLoading -> WeatherContentPhase.Loading
            error != null -> WeatherContentPhase.Error
            weather != null -> WeatherContentPhase.Success
            else -> WeatherContentPhase.Initial
        }
}

enum class WeatherContentPhase { Initial, Loading, Success, Error }

@Immutable
sealed interface WeatherError {
    data object EmptyCity : WeatherError
    data object Network : WeatherError
    data object CityNotFound : WeatherError
    data class Server(val code: String) : WeatherError
    data object Unknown : WeatherError
}