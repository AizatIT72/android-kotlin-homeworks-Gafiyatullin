package ru.itis.android.homework7.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.itis.android.homework7.data.db.dao.WeatherCacheDao
import ru.itis.android.homework7.data.db.entity.WeatherCacheEntity

@Database(
    entities = [WeatherCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherCacheDao(): WeatherCacheDao
}