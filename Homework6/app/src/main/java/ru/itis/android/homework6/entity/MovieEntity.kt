package ru.itis.android.homework6.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val year: Int,
    val genre: String,
    val duration: Int,
    val rating: Float,
    val posterUrl: String? = null,
    val addedByUserId: String,
    val createdAt: Date,
    val isFavorite: Boolean = false
)
