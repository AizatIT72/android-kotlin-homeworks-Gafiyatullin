package ru.itis.android.homework7.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        private const val DATABASE_NAME = "weather_cache.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { INSTANCE = it }
            }
    }
}