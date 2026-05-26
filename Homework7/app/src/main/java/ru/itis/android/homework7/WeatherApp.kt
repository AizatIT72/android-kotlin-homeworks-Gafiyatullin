package ru.itis.android.homework7

import android.app.Application
import ru.itis.android.homework7.di.AppDependencies

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDependencies.init(this)
    }
}