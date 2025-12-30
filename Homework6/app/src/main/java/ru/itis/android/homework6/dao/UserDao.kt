package ru.itis.android.homework6.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.itis.android.homework6.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :userId")
    suspend fun softDeleteUser(userId: String, deletedAt: java.util.Date)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun permanentlyDeleteUser(userId: String)

    @Query("UPDATE users SET isDeleted = 0, deletedAt = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: String)

    @Query("DELETE FROM users WHERE isDeleted = 1 AND deletedAt < :cutoffDate")
    suspend fun cleanupDeletedUsers(cutoffDate: java.util.Date)
}