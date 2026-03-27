package ru.itis.android.homework7.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.itis.android.homework7.data.model.WeatherResponseDto

interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
    ): WeatherResponseDto
}