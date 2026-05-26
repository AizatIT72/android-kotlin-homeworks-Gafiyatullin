package ru.itis.android.homework7.domain.usecase

import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.repository.WeatherRepository

class GetWeatherUseCase(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(city: String): Result<Pair<Weather, Boolean>> {
        if (city.isBlank()) {
            return Result.failure(IllegalArgumentException("City name must not be blank"))
        }
        return repository.getWeather(city.trim())
    }
}
