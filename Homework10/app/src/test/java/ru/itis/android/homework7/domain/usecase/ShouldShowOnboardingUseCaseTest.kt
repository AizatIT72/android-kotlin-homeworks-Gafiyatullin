package ru.itis.android.homework7.domain.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.itis.android.homework7.domain.repository.OnboardingRepository

class ShouldShowOnboardingUseCaseTest {

    private lateinit var repository: OnboardingRepository
    private lateinit var useCase: ShouldShowOnboardingUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ShouldShowOnboardingUseCase(repository)
    }

    @Test
    fun `returns true when onboarding not completed`() = runTest {
        every { repository.onboardingCompleted } returns flowOf(false)

        val shouldShow = useCase().first()

        assertEquals(true, shouldShow)
        verify(exactly = 1) { repository.onboardingCompleted }
    }
}