package ru.itis.android.homework7.domain.model

data class Weather(
    val cityName: String,
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
)