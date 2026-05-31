package ru.itis.android.homework7.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(
    private val analytics: FirebaseAnalytics
) {

    fun logScreenView(screenName: String, screenClass: String = screenName) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    fun logOnboardingShown() {
        analytics.logEvent(EVENT_ONBOARDING_SHOWN, null)
    }

    fun logOnboardingDismissed() {
        analytics.logEvent(EVENT_ONBOARDING_DISMISSED, null)
    }

    companion object {
        const val EVENT_ONBOARDING_SHOWN = "onboarding_shown"
        const val EVENT_ONBOARDING_DISMISSED = "onboarding_dismissed"
    }
}