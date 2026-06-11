package ru.itis.android.homework7.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.itis.android.homework7.data.db.entity.WeatherCacheEntity

@Dao
interface WeatherCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE city = :city")
    suspend fun getByCity(city: String): WeatherCacheEntity?
}