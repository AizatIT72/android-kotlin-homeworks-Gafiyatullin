package ru.itis.android.homework7.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("name") val name: String,
    @SerializedName("sys") val sys: SysDto,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("visibility") val visibility: Int,
)

data class SysDto(
    @SerializedName("country") val country: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
)

data class MainDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
)

data class WeatherConditionDto(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
)

data class WindDto(
    @SerializedName("speed") val speed: Double,
)