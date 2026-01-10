package ru.itis.android.homework6.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.itis.android.homework6.R
import ru.itis.android.homework6.entity.UserEntity
import ru.itis.android.homework6.repository.MovieRepository
import ru.itis.android.homework6.utils.SessionManager

class ProfileViewModel(
    application: Application,
    private val repository: MovieRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _moviesCount = MutableStateFlow(0)
    val moviesCount: StateFlow<Int> = _moviesCount

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = sessionManager.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                _profileState.value = ProfileState.Loading

                val user = repository.getUserByEmail(userId)
                val count = repository.getMovieCount(userId)

                _moviesCount.value = count
                if (user != null && !user.isDeleted) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error(getApplication<Application>().getString(R.string.user_not_found))
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(getApplication<Application>().getString(R.string.profile_load_error))
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun deleteAccount() {
        val userId = sessionManager.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                repository.softDeleteUser(userId)
                sessionManager.clearSession()
            } catch (e: Exception) {
            }
        }
    }

    fun restoreAccount(userId: String) {
        viewModelScope.launch {
            try {
                repository.restoreUser(userId)
            } catch (e: Exception) {
            }
        }
    }

    fun loginAfterRestore(userId: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByEmail(userId)
                if (user != null && !user.isDeleted) {
                    sessionManager.saveUser(user)
                    loadProfile()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun permanentlyDeleteAccount(userId: String) {
        viewModelScope.launch {
            try {
                repository.permanentlyDeleteUser(userId)
            } catch (e: Exception) {
            }
        }
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val user: UserEntity) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }
}