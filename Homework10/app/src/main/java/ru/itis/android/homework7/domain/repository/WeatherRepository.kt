package ru.itis.android.homework7.domain.repository

import ru.itis.android.homework7.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<Pair<Weather, Boolean>>
}