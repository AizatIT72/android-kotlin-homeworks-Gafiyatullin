package ru.itis.android.homework7.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val city: String,
    val countryCode: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val tempMinCelsius: Double,
    val tempMaxCelsius: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeedMs: Double,
    val pressureHpa: Int,
    val visibilityMeters: Int,
    val sunriseUnix: Long,
    val sunsetUnix: Long,
    val cachedAt: Long,
)