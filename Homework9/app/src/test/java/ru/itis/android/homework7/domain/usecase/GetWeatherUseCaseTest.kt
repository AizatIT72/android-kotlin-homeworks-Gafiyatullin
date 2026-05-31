package ru.itis.android.homework7.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.repository.WeatherRepository

class GetWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherUseCase

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
        repository = mockk()
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `invoke calls repository once and returns weather`() = runTest {
        val expected = fakeWeather to false
        coEvery { repository.getWeather("London") } returns Result.success(expected)

        val result = useCase("London")
        coVerify(exactly = 1) { repository.getWeather("London") }

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `invoke returns failure and skips repository when query is blank`() = runTest {
        val result = useCase("   ")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)

        coVerify(exactly = 0) { repository.getWeather(any()) }
    }
}