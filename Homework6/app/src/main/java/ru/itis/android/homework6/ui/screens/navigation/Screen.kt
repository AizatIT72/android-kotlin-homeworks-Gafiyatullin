package ru.itis.android.homework6.ui.screens.navigation

sealed class Screen(val route: String) {
    object Login : Screen(Routes.LOGIN)
    object Register : Screen(Routes.REGISTER)
    object MovieList : Screen(Routes.MOVIE_LIST)
    object AddMovie : Screen(Routes.ADD_MOVIE)
    object Profile : Screen(Routes.PROFILE)
    object RestoreAccount : Screen(Routes.RESTORE_ACCOUNT) {
        fun routeWithArgs(userId: String) = Routes.RESTORE_ACCOUNT_WITH_ARGS.replace("{userId}", userId)
    }

    companion object {
        fun fromRoute(route: String?): Screen {
            return when {
                route?.startsWith(Routes.RESTORE_ACCOUNT_BASE) == true -> RestoreAccount
                route == Login.route -> Login
                route == Register.route -> Register
                route == MovieList.route -> MovieList
                route == AddMovie.route -> AddMovie
                route == Profile.route -> Profile
                else -> Login
            }
        }
    }
}

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MOVIE_LIST = "movie_list"
    const val ADD_MOVIE = "add_movie"
    const val PROFILE = "profile"
    const val RESTORE_ACCOUNT_BASE = "restore_account"
    const val RESTORE_ACCOUNT = "$RESTORE_ACCOUNT_BASE/{userId}"
    const val RESTORE_ACCOUNT_WITH_ARGS = "restore_account/{userId}"
}