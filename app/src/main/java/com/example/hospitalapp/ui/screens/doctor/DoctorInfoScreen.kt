package com.example.hospitalapp.ui.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.DoctorRequest
import com.example.hospitalapp.network.model.DoctorResponse
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorInfoScreen(
    viewModel: DoctorViewModel,
    doctorId: Long,
    navController: NavController,
    onProfileUpdated: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Form state variables
    var fName by remember { mutableStateOf("") }
    var lName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }
    var experienceYears by remember { mutableStateOf("") }
    var consultationFee by remember { mutableStateOf("") }
    var availableForEmergency by remember { mutableStateOf(true) }

    // Observe doctor details
    val doctorState = viewModel.doctorDetailsUiState

    // Load initial data
    LaunchedEffect(doctorId) {
        viewModel.getDoctorById(doctorId)
    }

    // Update UI state when doctor data changes
    LaunchedEffect(doctorState) {
        if (doctorState is BaseUiState.Success) {
            val doctor = doctorState.data
            specialization = doctor.specialization
            qualification = doctor.qualification
            availableForEmergency = doctor.availableForEmergency
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (doctorState) {
            is BaseUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BaseUiState.Error -> {
                ErrorState(
                    onRetry = { viewModel.getDoctorById(doctorId) }
                )
            }
            is BaseUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Basic Information Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Basic Information",
                                style = MaterialTheme.typography.titleMedium
                            )

                            OutlinedTextField(
                                value = fName,
                                onValueChange = { fName = it },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = lName,
                                onValueChange = { lName = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Professional Information Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Professional Information",
                                style = MaterialTheme.typography.titleMedium
                            )

                            OutlinedTextField(
                                value = specialization,
                                onValueChange = { specialization = it },
                                label = { Text("Specialization") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = licenseNumber,
                                onValueChange = { licenseNumber = it },
                                label = { Text("License Number") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = qualification,
                                onValueChange = { qualification = it },
                                label = { Text("Qualification") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = experienceYears,
                                onValueChange = {
                                    if (it.isEmpty() || it.toIntOrNull() != null) {
                                        experienceYears = it
                                    }
                                },
                                label = { Text("Years of Experience") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = consultationFee,
                                onValueChange = {
                                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                        consultationFee = it
                                    }
                                },
                                label = { Text("Consultation Fee") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = availableForEmergency,
                                    onCheckedChange = { availableForEmergency = it }
                                )
                                Text("Available for Emergency")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Changes") },
            text = { Text("Are you sure you want to update your profile?") },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        viewModel.updateDoctorDetails(
                            doctorId,
                            DoctorRequest(
                                fName = fName,
                                lName = lName,
                                email = email,
                                phoneNumber = phoneNumber,
                                password = "", // Not needed for update
                                specialization = specialization,
                                licenseNumber = licenseNumber,
                                qualification = qualification,
                                experienceYears = experienceYears.toIntOrNull() ?: 0,
                                consultationFee = consultationFee.toDoubleOrNull() ?: 0.0,
                                availableForEmergency = availableForEmergency
                            )
                        )
                        onProfileUpdated()
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Error loading profile data",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}