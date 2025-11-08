package ru.itis.android.homework3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ru.itis.android.homework3.util.isValidEmail
import androidx.navigation.NavController
import ru.itis.android.homework3.R
import ru.itis.android.homework3.constants.AppConstants
import ru.itis.android.homework3.util.dimensionResource
import ru.itis.android.homework3.util.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailValidationError by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large))
            .navigationBarsPadding()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
    ) {
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
                emailValidationError = false
            },
            label = { Text(stringResource(R.string.email_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester),
            isError = emailError || emailValidationError,
            supportingText = {
                when {
                    emailError -> Text(stringResource(R.string.email_empty_error))
                    emailValidationError -> Text(stringResource(R.string.email_invalid_error))
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text(stringResource(R.string.password_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = passwordError,
            supportingText = {
                if (passwordError) {
                    Text(stringResource(R.string.password_length_error, AppConstants.MIN_PASSWORD_LENGTH))
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                keyboardController?.hide()

                var hasError = false

                if (email.isBlank()) {
                    emailError = true
                    hasError = true
                } else if (!email.isValidEmail()) {
                    emailValidationError = true
                    hasError = true
                }

                if (password.isBlank() || password.length < AppConstants.MIN_PASSWORD_LENGTH) {
                    passwordError = true
                    hasError = true
                }

                if (!hasError) {
                    val route = AppConstants.Navigation.NOTES_SCREEN.replace(
                        "{${AppConstants.Navigation.EMAIL_ARGUMENT}}",
                        email
                    )
                    navController.navigate(route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_button_bottom))
        ) {
            Text(stringResource(R.string.login_button))
        }
    }
}