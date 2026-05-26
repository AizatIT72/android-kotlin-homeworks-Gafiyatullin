package ru.itis.android.homework7.presentation.navigation

sealed class Screen(val route: String) {

    data object Weather : Screen("weather")

    data object Details : Screen("details/{city}") {
        const val ARG_CITY = "city"
        fun createRoute(city: String) = "details/$city"
    }
}