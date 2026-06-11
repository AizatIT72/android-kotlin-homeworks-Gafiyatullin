package ru.itis.android.homework7

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.edit
import android.os.Build
import com.google.firebase.crashlytics.ktx.BuildConfig
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID


@HiltAndroidApp
class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupCrashlyticsUser()
        createDefaultNotificationChannel()
    }

    private fun setupCrashlyticsUser() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = prefs.getString(KEY_USER_ID, null) ?: run {
            val newId = UUID.randomUUID().toString()
            prefs.edit { putString(KEY_USER_ID, newId) }
            newId
        }
        Firebase.crashlytics.apply {
            setUserId(userId)
            setCustomKey(KEY_USER_ID, userId)
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
        }
    }

    private fun createDefaultNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "default_channel",
                getString(R.string.notif_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_USER_ID = "user_id"
    }
}