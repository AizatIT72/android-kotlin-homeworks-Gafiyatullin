package ru.itis.android.homework6.auth

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.itis.android.homework6.R
import ru.itis.android.homework6.ui.screens.navigation.Screen
import ru.itis.android.homework6.viewmodels.AuthViewModel
import ru.itis.android.homework6.viewmodels.ViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetLoginState()
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthViewModel.LoginState.Success -> {
                navController.navigate(Screen.MovieList.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is AuthViewModel.LoginState.AccountDeleted -> {
                val user = (loginState as AuthViewModel.LoginState.AccountDeleted).user
                navController.navigate(Screen.RestoreAccount.routeWithArgs(user.id)) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is AuthViewModel.LoginState.Error -> {
            }
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = loginState is AuthViewModel.LoginState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = loginState is AuthViewModel.LoginState.Error
            )

            if (loginState is AuthViewModel.LoginState.Error) {
                Text(
                    text = (loginState as AuthViewModel.LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState != AuthViewModel.LoginState.Loading
            ) {
                if (loginState == AuthViewModel.LoginState.Loading) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                }
            ) {
                Text(stringResource(R.string.register_link))
            }
        }
    }
}