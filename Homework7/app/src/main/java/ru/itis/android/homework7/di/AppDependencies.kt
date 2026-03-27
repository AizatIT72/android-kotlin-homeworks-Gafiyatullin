package ru.itis.android.homework7.di

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.itis.android.homework7.data.api.WeatherApiService
import ru.itis.android.homework7.data.db.AppDatabase
import ru.itis.android.homework7.data.repository.WeatherRepositoryImpl
import ru.itis.android.homework7.domain.repository.WeatherRepository
import ru.itis.android.homework7.domain.usecase.GetWeatherUseCase
import ru.itis.android.homework7.BuildConfig

object AppDependencies {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val apiKeyInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            .build()
        chain.proceed(original.newBuilder().url(url).build())
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApiService: WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    private lateinit var weatherRepository: WeatherRepository

    fun init(context: Context) {
        val db = AppDatabase.getInstance(context)
        weatherRepository = WeatherRepositoryImpl(
            apiService = weatherApiService,
            cacheDao = db.weatherCacheDao(),
        )
    }

    fun provideGetWeatherUseCase(): GetWeatherUseCase =
        GetWeatherUseCase(weatherRepository)
}