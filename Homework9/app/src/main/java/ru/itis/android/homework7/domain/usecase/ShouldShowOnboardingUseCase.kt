package ru.itis.android.homework7.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.itis.android.homework7.domain.repository.OnboardingRepository
import javax.inject.Inject

class ShouldShowOnboardingUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        repository.onboardingCompleted.map { completed -> !completed }
}