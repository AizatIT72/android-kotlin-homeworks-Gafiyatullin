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
import java.util.Date

class AuthViewModel(
    application: Application,
    private val repository: MovieRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error(getApplication<Application>().getString(R.string.fill_all_fields))
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val user = repository.getUserByEmail(email)
                if (user == null) {
                    _loginState.value = LoginState.Error(getApplication<Application>().getString(R.string.login_error))
                    return@launch
                }
                if (user.password != password) {
                    _loginState.value = LoginState.Error(getApplication<Application>().getString(R.string.login_error))
                    return@launch
                }
                if (user.isDeleted) {
                    _loginState.value = LoginState.AccountDeleted(user)
                    return@launch
                }
                sessionManager.saveUser(user)
                repository.updateUser(user.copy(lastLoginAt = Date()))
                _loginState.value = LoginState.Success

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(getApplication<Application>().getString(R.string.network_error))
            }
        }
    }

    fun cleanupOldDeletedAccounts() {
        viewModelScope.launch {
            try {
                val cutoffDate = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
                repository.cleanupDeletedUsers(cutoffDate)
            } catch (e: Exception) {
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _registerState.value = RegisterState.Error(getApplication<Application>().getString(R.string.fill_all_fields))
            return
        }

        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error(getApplication<Application>().getString(R.string.passwords_mismatch))
            return
        }

        if (!isValidEmail(email)) {
            _registerState.value = RegisterState.Error(getApplication<Application>().getString(R.string.email_invalid))
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null && !existingUser.isDeleted) {
                    _registerState.value = RegisterState.Error(getApplication<Application>().getString(R.string.user_exists))
                    return@launch
                }

                val user = UserEntity(
                    id = email,
                    email = email,
                    password = password,
                    name = name,
                    createdAt = Date()
                )

                repository.registerUser(user)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(getApplication<Application>().getString(R.string.registration_error))
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        data class AccountDeleted(val user: UserEntity) : LoginState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}