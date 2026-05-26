package ru.itis.android.homework7.data.repository

import ru.itis.android.homework7.data.db.entity.WeatherCacheEntity
import ru.itis.android.homework7.data.model.WeatherResponseDto
import ru.itis.android.homework7.domain.model.Weather

internal fun WeatherResponseDto.toEntity(cachedAt: Long): WeatherCacheEntity {
    val condition = weather.firstOrNull()
    return WeatherCacheEntity(
        city = name.lowercase(),
        countryCode = sys.country,
        temperatureCelsius = main.temp,
        feelsLikeCelsius = main.feelsLike,
        tempMinCelsius = main.tempMin,
        tempMaxCelsius = main.tempMax,
        description = condition?.description.orEmpty(),
        iconCode = condition?.icon.orEmpty(),
        humidity = main.humidity,
        windSpeedMs = wind.speed,
        pressureHpa = main.pressure,
        visibilityMeters = visibility,
        sunriseUnix = sys.sunrise,
        sunsetUnix = sys.sunset,
        cachedAt = cachedAt,
    )
}

internal fun WeatherResponseDto.toDomain(): Weather {
    val condition = weather.firstOrNull()
    return Weather(
        cityName = name,
        countryCode = sys.country,
        temperatureCelsius = main.temp,
        feelsLikeCelsius = main.feelsLike,
        tempMinCelsius = main.tempMin,
        tempMaxCelsius = main.tempMax,
        description = condition?.description.orEmpty(),
        iconCode = condition?.icon.orEmpty(),
        humidity = main.humidity,
        windSpeedMs = wind.speed,
        pressureHpa = main.pressure,
        visibilityMeters = visibility,
        sunriseUnix = sys.sunrise,
        sunsetUnix = sys.sunset,
    )
}

internal fun WeatherCacheEntity.toDomain(): Weather = Weather(
    cityName = city.replaceFirstChar { it.uppercase() },
    countryCode = countryCode,
    temperatureCelsius = temperatureCelsius,
    feelsLikeCelsius = feelsLikeCelsius,
    tempMinCelsius = tempMinCelsius,
    tempMaxCelsius = tempMaxCelsius,
    description = description,
    iconCode = iconCode,
    humidity = humidity,
    windSpeedMs = windSpeedMs,
    pressureHpa = pressureHpa,
    visibilityMeters = visibilityMeters,
    sunriseUnix = sunriseUnix,
    sunsetUnix = sunsetUnix,
)