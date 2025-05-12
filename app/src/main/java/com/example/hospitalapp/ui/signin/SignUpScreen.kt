package com.example.hospitalapp.ui.signin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.Gender
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.UserViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSignUpSuccess: (role: String, userId: Long) -> Unit,  // Modified to include role and userId

) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("User") }
    var lastName by remember { mutableStateOf("Name") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("PATIENT") }
    var showError by remember { mutableStateOf(false) }

    val createUserState = userViewModel.createUserUiState
    val errorMessage = userViewModel.errorMessage
    val currentUser by userViewModel.currentUser.collectAsState()

    // Clear error when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            userViewModel.clearError()
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { onSignUpSuccess(selectedRole, it.id) }
    }

    LaunchedEffect(createUserState) {
        showError = createUserState is BaseUiState.Error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = createUserState !is BaseUiState.Loading
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Role Selection
            Text(
                "I am a",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("PATIENT", "DOCTOR").forEach { role ->
                    FilterChip(
                        selected = selectedRole == role,
                        onClick = { selectedRole = role },
                        enabled = createUserState !is BaseUiState.Loading,
                        label = { Text(role.capitalize()) }
                    )
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    showError = false
                },
                label = { Text("Username") },
                singleLine = true,
                isError = showError,
                enabled = createUserState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    showError = false
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = showError,
                enabled = createUserState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    showError = false
                },
                label = { Text("Email") },
                singleLine = true,
                isError = showError,
                enabled = createUserState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        showError = false
                    },
                    label = { Text("First Name") },
                    singleLine = true,
                    isError = showError,
                    enabled = createUserState !is BaseUiState.Loading,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        showError = false
                    },
                    label = { Text("Last Name") },
                    singleLine = true,
                    isError = showError,
                    enabled = createUserState !is BaseUiState.Loading,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    showError = false
                },
                label = { Text("Phone Number") },
                singleLine = true,
                isError = showError,
                enabled = createUserState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            if (showError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }


            Button(
                onClick = {

                    val request = SignupRequest(
                        username = username,
                        password = password,
                        email = email,
                        fName = firstName,
                        lName = lastName,
                        phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                        gender = Gender.OTHER.toString(),
                        dob = "01-02-2025", // Default value
                        address = "Not specified", // Default value
                    )
                    userViewModel.createUser(request)
                },
                enabled = username.isNotBlank() && password.isNotBlank() &&
                        email.isNotBlank() && firstName.isNotBlank() &&
                        lastName.isNotBlank() && createUserState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (createUserState is BaseUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Account")
                }
            }
            if (createUserState is BaseUiState.Error) {
                Text(
                    text = "Error: ${createUserState}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}
private fun areFieldsValid(
    username: String,
    password: String,
    email: String,
    firstName: String,
    lastName: String
): Boolean {
    return username.isNotBlank() &&
            password.isNotBlank() &&
            email.isNotBlank() &&
            firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 6
}