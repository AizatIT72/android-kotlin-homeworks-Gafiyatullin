package ru.itis.android.homework6.auth

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is AuthViewModel.RegisterState.Success) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.register_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                isError = registerState is AuthViewModel.RegisterState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = registerState is AuthViewModel.RegisterState.Error
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
                isError = registerState is AuthViewModel.RegisterState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = registerState is AuthViewModel.RegisterState.Error
            )

            if (registerState is AuthViewModel.RegisterState.Error) {
                Text(
                    text = (registerState as AuthViewModel.RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.register(name, email, password, confirmPassword)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registerState != AuthViewModel.RegisterState.Loading
            ) {
                if (registerState == AuthViewModel.RegisterState.Loading) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Text(stringResource(R.string.register_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text(stringResource(R.string.login_link))
            }
        }
    }
}