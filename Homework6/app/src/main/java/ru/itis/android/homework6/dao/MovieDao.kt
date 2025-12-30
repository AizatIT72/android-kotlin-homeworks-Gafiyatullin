package ru.itis.android.homework6.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.itis.android.homework6.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert
    suspend fun insert(movie: MovieEntity): Long

    @Query("SELECT * FROM movies WHERE addedByUserId = :userId ORDER BY createdAt DESC")
    fun getMoviesByUser(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE addedByUserId = :userId ORDER BY title ASC")
    fun getMoviesSortedByTitle(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE addedByUserId = :userId ORDER BY rating DESC")
    fun getMoviesSortedByRating(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE addedByUserId = :userId ORDER BY year DESC")
    fun getMoviesSortedByYear(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT COUNT(*) FROM movies WHERE addedByUserId = :userId")
    suspend fun getMovieCount(userId: String): Int

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies WHERE id = :movieId")
    suspend fun deleteMovie(movieId: Long)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): MovieEntity?
}
