package ru.itis.android.homework6.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.itis.android.homework6.auth.LoginScreen
import ru.itis.android.homework6.auth.RegisterScreen
import ru.itis.android.homework6.ui.screens.auth.RestoreAccountScreen
import ru.itis.android.homework6.ui.screens.movies.AddMovieScreen
import ru.itis.android.homework6.ui.screens.movies.MovieListScreen
import ru.itis.android.homework6.ui.screens.navigation.Screen
import ru.itis.android.homework6.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.MovieList.route) {
            MovieListScreen(navController)
        }

        composable(Screen.AddMovie.route) {
            AddMovieScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(
            route = Screen.RestoreAccount.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            RestoreAccountScreen(userId, navController)
        }
    }
}