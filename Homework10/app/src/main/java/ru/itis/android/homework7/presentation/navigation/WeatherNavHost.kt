package ru.itis.android.homework7.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.itis.android.homework7.analytics.AnalyticsLogger
import ru.itis.android.homework7.presentation.details.DetailsScreen
import ru.itis.android.homework7.presentation.piechart.PieChartScreen
import ru.itis.android.homework7.presentation.weather.WeatherScreen

@Composable
fun WeatherNavHost(
    analytics: AnalyticsLogger,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(backStackEntry) {
        val route = backStackEntry?.destination?.route ?: return@LaunchedEffect
        analytics.logScreenView(screenName = route)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route,
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onCityClick = { city ->
                    navController.navigate(Screen.Details.createRoute(city))
                },
                onOpenPieChart = {
                    navController.navigate(Screen.PieChart.route)
                },
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(Screen.Details.ARG_CITY) { type = NavType.StringType },
            ),
        ) {
            DetailsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.PieChart.route) {
            PieChartScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}