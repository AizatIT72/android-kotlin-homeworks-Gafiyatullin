package ru.itis.android.homework7.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.itis.android.homework7.analytics.AnalyticsLogger
import ru.itis.android.homework7.data.prefs.OnboardingPrefs
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: OnboardingPrefs,
    private val analytics: AnalyticsLogger,
) : ViewModel() {

    private val _dismissedLocally = MutableStateFlow(false)

    val showOnboarding: StateFlow<Boolean> = combine(
        prefs.onboardingCompleted,
        _dismissedLocally.asStateFlow(),
    ) { completedInPrefs, dismissedNow ->
        !completedInPrefs && !dismissedNow
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    fun onShown() {
        analytics.logOnboardingShown()
    }

    fun onDismissClicked() {
        analytics.logOnboardingDismissed()
        _dismissedLocally.value = true
        viewModelScope.launch {
            prefs.markOnboardingCompleted()
        }
    }
}