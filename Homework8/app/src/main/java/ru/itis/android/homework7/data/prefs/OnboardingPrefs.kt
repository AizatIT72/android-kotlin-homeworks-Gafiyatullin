package ru.itis.android.homework7.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding")

@Singleton
class OnboardingPrefs @Inject constructor(
    private val context: Context,
) {
    private val key = booleanPreferencesKey("onboarding_completed")

    val onboardingCompleted: Flow<Boolean> = context.onboardingDataStore.data
        .map { prefs -> prefs[key] ?: false }

    suspend fun markOnboardingCompleted() {
        context.onboardingDataStore.edit { it[key] = true }
    }
}