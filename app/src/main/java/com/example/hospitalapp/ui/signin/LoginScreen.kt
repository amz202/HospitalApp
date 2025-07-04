package com.example.hospitalapp.ui.signin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: (role: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val errorMessage by userViewModel.errorMessage.collectAsState()

    val loginState = userViewModel.loginState

    val showError = loginState is BaseUiState.Error ||
            (errorMessage != null && errorMessage?.contains("Invalid username or password") == true)

    LaunchedEffect(loginState) {
        if (loginState is BaseUiState.Success && loginState.data != null) {
            onLoginSuccess(loginState.data.role)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hospital App",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            isError = showError,
            enabled = loginState !is BaseUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = showError,
            enabled = loginState !is BaseUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage ?: "Invalid username or password",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                userViewModel.login(username, password)
            },
            enabled = username.isNotBlank() && password.isNotBlank() &&
                    loginState !is BaseUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState is BaseUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onSignUpClick,
            enabled = loginState !is BaseUiState.Loading
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}