package ru.itis.android.homework3.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ru.itis.android.homework3.constants.AppConstants

@Composable
fun stringResource(resId: Int): String {
    return LocalContext.current.getString(resId)
}

@Composable
fun stringResource(resId: Int, vararg formatArgs: Any): String {
    return LocalContext.current.getString(resId, *formatArgs)
}

@Composable
fun dimensionResource(resId: Int): Dp {
    return LocalContext.current.resources.getDimension(resId).dp
}

fun String.isValidEmail(): Boolean {
    return AppConstants.EMAIL_REGEX.toRegex().matches(this)
}

@Composable
inline fun <reified T : ViewModel> NavController.sharedViewModel(navController: NavHostController): T {
    val backStackEntry = this.currentBackStackEntry ?: navController.currentBackStackEntry!!
    return viewModel(backStackEntry)
}