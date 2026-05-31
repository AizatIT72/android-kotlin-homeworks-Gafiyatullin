package ru.itis.android.homework7.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.itis.android.homework7.BuildConfig
import ru.itis.android.homework7.data.api.WeatherApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            .build()
        chain.proceed(original.newBuilder().url(url).build())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        apiKey: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKey)
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)
}