package ru.itis.android.homework6.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val email: String,
    val password: String,
    val name: String,
    val createdAt: Date,
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null,
    val lastLoginAt: Date? = null
)
