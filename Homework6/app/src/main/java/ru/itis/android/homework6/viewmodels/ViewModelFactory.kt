package ru.itis.android.homework6.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.itis.android.homework6.database.AppDatabase
import ru.itis.android.homework6.repository.MovieRepository
import ru.itis.android.homework6.utils.SessionManager

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = MovieRepository(
                userDao = database.userDao(),
                movieDao = database.movieDao()
            )
            val sessionManager = SessionManager(application)
            return AuthViewModel(application, repository, sessionManager) as T
        }

        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = MovieRepository(
                userDao = database.userDao(),
                movieDao = database.movieDao()
            )
            val sessionManager = SessionManager(application)
            return MovieViewModel(application, repository, sessionManager) as T
        }

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = MovieRepository(
                userDao = database.userDao(),
                movieDao = database.movieDao()
            )
            val sessionManager = SessionManager(application)
            return ProfileViewModel(application, repository, sessionManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}