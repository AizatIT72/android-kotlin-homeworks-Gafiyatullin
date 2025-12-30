package ru.itis.android.homework6.repository

import kotlinx.coroutines.flow.Flow
import ru.itis.android.homework6.dao.MovieDao
import ru.itis.android.homework6.dao.UserDao
import ru.itis.android.homework6.entity.MovieEntity
import ru.itis.android.homework6.entity.UserEntity
import java.util.Date

class MovieRepository(
    private val userDao: UserDao,
    private val movieDao: MovieDao
) {
    suspend fun registerUser(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.password == password && !user.isDeleted) {
            user
        } else {
            null
        }
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun softDeleteUser(userId: String) {
        userDao.softDeleteUser(userId, Date())
    }

    suspend fun restoreUser(userId: String) {
        userDao.restoreUser(userId)
    }

    suspend fun permanentlyDeleteUser(userId: String) {
        userDao.permanentlyDeleteUser(userId)
    }

    suspend fun cleanupDeletedUsers(cutoffDate: Date) {
        userDao.cleanupDeletedUsers(cutoffDate)
    }

    suspend fun addMovie(movie: MovieEntity): Long {
        return movieDao.insert(movie)
    }

    fun getMovies(userId: String, sortType: SortType): Flow<List<MovieEntity>> {
        return when (sortType) {
            SortType.DATE -> movieDao.getMoviesByUser(userId)
            SortType.TITLE -> movieDao.getMoviesSortedByTitle(userId)
            SortType.RATING -> movieDao.getMoviesSortedByRating(userId)
            SortType.YEAR -> movieDao.getMoviesSortedByYear(userId)
        }
    }

    suspend fun getMovieCount(userId: String): Int {
        return movieDao.getMovieCount(userId)
    }

    suspend fun deleteMovie(movieId: Long) {
        movieDao.deleteMovie(movieId)
    }

    suspend fun updateMovie(movie: MovieEntity) {
        movieDao.updateMovie(movie)
    }

    suspend fun getMovieById(movieId: Long): MovieEntity? {
        return movieDao.getMovieById(movieId)
    }
}

enum class SortType {
    DATE, TITLE, RATING, YEAR
}
