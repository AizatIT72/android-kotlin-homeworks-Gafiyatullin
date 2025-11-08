package ru.itis.android.homework3.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.itis.android.homework3.constants.AppConstants
import ru.itis.android.homework3.ui.theme.Homework3Theme
import ru.itis.android.homework3.ui.theme.ThemeManager
import ru.itis.android.homework3.ui.screens.AddNoteScreen
import ru.itis.android.homework3.ui.screens.LoginScreen
import ru.itis.android.homework3.ui.screens.NotesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentThemeState = ThemeManager.rememberThemeState()

    Homework3Theme(colorScheme = currentThemeState.value) {
        NavHost(
            navController = navController,
            startDestination = AppConstants.Navigation.LOGIN_SCREEN
        ) {
            composable(AppConstants.Navigation.LOGIN_SCREEN) {
                LoginScreen(navController = navController)
            }
            composable(AppConstants.Navigation.NOTES_SCREEN) { backStackEntry ->
                val email = backStackEntry.arguments?.getString(AppConstants.Navigation.EMAIL_ARGUMENT) ?: ""
                NotesScreen(
                    navController = navController,
                    email = email,
                    currentColorScheme = currentThemeState.value,
                    onThemeChange = { newColorScheme ->
                        currentThemeState.value = newColorScheme
                    }
                )
            }
            composable(AppConstants.Navigation.ADD_NOTE_SCREEN) {
                AddNoteScreen(navController = navController)
            }
        }
    }
}