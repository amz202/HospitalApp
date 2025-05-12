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
import com.example.hospitalapp.network.model.UserRole
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.UserViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format

@Composable
fun SignupScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: (role: String, userId: Long) -> Unit,
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showRoleSelection by remember { mutableStateOf(true) }
    var showUserForm by remember { mutableStateOf(false) }

    val createUserState = userViewModel.createUserUiState
    val errorMessage by userViewModel.errorMessage.collectAsState()
    val currentUserId = userViewModel.currentUser.collectAsState().value?.id

    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            selectedRole?.let { role ->
                onSignUpSuccess(role, userId)
            }
        }
    }

    when {
        showRoleSelection -> {
            RoleSelectionContent(
                onRoleSelected = { role ->
                    selectedRole = role
                    showRoleSelection = false
                    showUserForm = true
                },
                onBackClick = onBackClick
            )
        }
        showUserForm -> {
            UserFormContent(
                userViewModel = userViewModel,
                role = selectedRole!!,
                onBackClick = {
                    showUserForm = false
                    showRoleSelection = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleSelectionContent(
    onRoleSelected: (String) -> Unit,
    onBackClick: () -> Unit,
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
            modifier = Modifier
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
private fun UserFormContent(
    userViewModel: UserViewModel,
    role: String,
    onBackClick: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val errorMessage by userViewModel.errorMessage.collectAsState()
    val createUserState = userViewModel.createUserUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
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
            // Basic user information fields
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    userViewModel.createInitialUser(
                        userName = username,
                        password = password,
                        role = UserRole.valueOf(role)
                    )

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = createUserState !is BaseUiState.Loading
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
        }
    }
}