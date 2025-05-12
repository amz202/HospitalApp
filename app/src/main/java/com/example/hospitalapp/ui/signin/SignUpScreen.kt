package com.example.hospitalapp.ui.signin

import android.os.Build
import android.util.Log
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSignUpSuccess: (role: String, userId: Long) -> Unit,
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showRoleSelection by remember { mutableStateOf(true) }

    if (showRoleSelection) {
        RoleSelectionContent(
            onRoleSelected = { role ->
                selectedRole = role
                showRoleSelection = false
            },
            onBackClick = onBackClick
        )
    } else {
        RegistrationContent(
            role = selectedRole!!,
            userViewModel = userViewModel,
            onBackClick = { showRoleSelection = true },
            onSignUpSuccess = onSignUpSuccess
        )
    }
}

@Composable
private fun RoleSelectionContent(
    onRoleSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Your Role") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = { onRoleSelected("PATIENT") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("I'm a Patient")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = { onRoleSelected("DOCTOR") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("I'm a Doctor")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent(
    role: String,
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: (role: String, userId: Long) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Additional fields based on role
    var specialization by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

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
        currentUser?.let { user ->
            onSignUpSuccess(role, user.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (role == "DOCTOR") "Doctor Registration" else "Patient Registration") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Common fields
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Role specific fields
            if (role == "DOCTOR") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = { Text("Specialization") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("License Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Years of Experience") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    val signupRequest = SignupRequest(
                        username = username,
                        password = password,
                        email = email,
                        fName = firstName,
                        lName = lastName,
                        phoneNumber = phoneNumber,
                        role = role,
                        // Add role-specific fields
                        specialization = if (role == "DOCTOR") specialization else null,
                        licenseNumber = if (role == "DOCTOR") licenseNumber else null,
                        experienceYears = if (role == "DOCTOR") experience.toIntOrNull() ?: 0 else null,
                        bloodGroup = if (role == "PATIENT") bloodGroup else null,
                        allergies = if (role == "PATIENT") allergies.split(",").map { it.trim() } else null
                    )
                    userViewModel.createUser(signupRequest)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Register")
            }
        }
    }
}