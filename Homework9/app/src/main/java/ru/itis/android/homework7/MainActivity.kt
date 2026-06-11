package ru.itis.android.homework7

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.itis.android.homework7.analytics.AnalyticsLogger
import ru.itis.android.homework7.presentation.navigation.WeatherNavHost
import ru.itis.android.homework7.presentation.onboarding.OnboardingScreen
import ru.itis.android.homework7.presentation.onboarding.OnboardingViewModel
import ru.itis.android.homework7.ui.theme.Homework7Theme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: AnalyticsLogger

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        setContent {
            Homework7Theme {
                AppRoot(analytics = analytics)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun AppRoot(analytics: AnalyticsLogger) {
    val onboardingVm: OnboardingViewModel = hiltViewModel()
    val showOnboarding by onboardingVm.showOnboarding.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        WeatherNavHost(analytics = analytics)
        if (showOnboarding) {
            OnboardingScreen(viewModel = onboardingVm)
        }
    }
}