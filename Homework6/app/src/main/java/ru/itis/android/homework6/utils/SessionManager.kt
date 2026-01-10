package ru.itis.android.homework6.utils

import android.content.Context
import android.content.SharedPreferences
import ru.itis.android.homework6.entity.UserEntity

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("movie_collection_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUser(user: UserEntity) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getCurrentUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun getCurrentUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getCurrentUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
