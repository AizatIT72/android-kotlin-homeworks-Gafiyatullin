package ru.itis.android.homework7.data.repository

import ru.itis.android.homework7.data.api.WeatherApiService
import ru.itis.android.homework7.data.db.dao.WeatherCacheDao
import ru.itis.android.homework7.domain.model.Weather
import ru.itis.android.homework7.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val cacheDao: WeatherCacheDao,
) : WeatherRepository {

    companion object {
        private const val CACHE_TTL_MS = 30_000L
    }

    override suspend fun getWeather(city: String): Result<Pair<Weather, Boolean>> {
        return runCatching {
            val normalizedCity = city.trim().lowercase()
            val cached = cacheDao.getByCity(normalizedCity)
            val now = System.currentTimeMillis()
            if (cached != null && (now - cached.cachedAt) < CACHE_TTL_MS) {
                Pair(cached.toDomain(), true)
            } else {
                val response = apiService.getCurrentWeather(normalizedCity)
                cacheDao.insertOrReplace(response.toEntity(cachedAt = now))
                Pair(response.toDomain(), false)
            }
        }
    }
}