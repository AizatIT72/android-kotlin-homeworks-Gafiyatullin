package ru.itis.android.homework7.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.itis.android.homework7.data.repository.WeatherRepositoryImpl
import ru.itis.android.homework7.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl,
    ): WeatherRepository
}