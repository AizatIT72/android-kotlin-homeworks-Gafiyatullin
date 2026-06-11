package ru.itis.android.homework7.presentation.weather

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.usecase.GetWeatherUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val fakeWeather = Weather(
        cityName = "London",
        countryCode = "GB",
        temperatureCelsius = 19.0,
        feelsLikeCelsius = 18.0,
        tempMinCelsius = 17.0,
        tempMaxCelsius = 21.0,
        description = "broken clouds",
        iconCode = "04d",
        humidity = 62,
        windSpeedMs = 5.14,
        pressureHpa = 1016,
        visibilityMeters = 10000,
        sunriseUnix = 0L,
        sunsetUnix = 0L,
    )

    @Before
    fun setUp() {
        getWeatherUseCase = mockk()
        savedStateHandle = SavedStateHandle()
    }

    private fun createViewModel() = WeatherViewModel(getWeatherUseCase, savedStateHandle)

    @Test
    fun `successful fetch puts weather into state`() = runTest {
        coEvery { getWeatherUseCase("London") } returns Result.success(fakeWeather to false)

        val viewModel = createViewModel()
        viewModel.onCityInputChanged("London")
        viewModel.onGetWeatherClicked()

        val state = viewModel.uiState.value
        assertEquals(fakeWeather, state.weather)
        assertNull(state.error)
        assertTrue(!state.isLoading)
        // город попал в историю поиска
        assertTrue(state.searchHistory.contains("London"))
        coVerify(exactly = 1) { getWeatherUseCase("London") }
    }

    @Test
    fun `failed fetch shows error in state`() = runTest {
        val errorBody = okhttp3.ResponseBody.create(null, "not found")
        coEvery { getWeatherUseCase("Nowhere") } returns
                Result.failure(
                    retrofit2.HttpException(retrofit2.Response.error<Any>(404, errorBody))
                )

        val viewModel = createViewModel()
        viewModel.onCityInputChanged("Nowhere")
        viewModel.onGetWeatherClicked()

        val state = viewModel.uiState.value
        assertNull(state.weather)
        assertEquals(WeatherError.CityNotFound, state.error)
        assertTrue(!state.isLoading)
    }
}