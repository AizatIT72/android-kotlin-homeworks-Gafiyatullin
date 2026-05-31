package ru.itis.android.homework7.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val onboardingCompleted: Flow<Boolean>
    suspend fun markOnboardingCompleted()
}