package ru.itis.android.homework7.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.usecase.GetWeatherUseCase
import ru.itis.android.homework7.presentation.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val city: String =
        checkNotNull(savedStateHandle[Screen.Details.ARG_CITY]) {
            "city argument is required for DetailsScreen"
        }

    private val _uiState = MutableStateFlow(DetailsUiState(city = city, isLoading = true))
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun retry() = loadDetails()

    private fun loadDetails() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getWeatherUseCase(city)
                .onSuccess { (weather, _) ->
                    _uiState.update { it.copy(isLoading = false, weather = weather) }
                }
                .onFailure { t ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = t.message ?: "Unknown error")
                    }
                }
        }
    }
}

data class DetailsUiState(
    val city: String,
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val errorMessage: String? = null,
)