package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.PatientUpdateRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    viewModel: PatientViewModel,
    patientId: Long,
    onProfileUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var bloodGroup by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf<List<String>>(emptyList()) }
    var allergyInput by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var medicalHistoryInput by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val patientState = viewModel.patientDetailsUiState

    LaunchedEffect(patientId) {
        viewModel.getPatientById(patientId)
    }

    LaunchedEffect(selectedPatient) {
        selectedPatient?.let { patient ->
            bloodGroup = patient.bloodGroup ?: ""
            allergies = patient.allergies
            medicalHistory = patient.medicalHistory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (patientState) {
                is BaseUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BaseUiState.Success -> {
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
                                value = bloodGroup,
                                onValueChange = { bloodGroup = it },
                                label = { Text("Blood Group") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Address") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }

                    // Allergies Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Allergies",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = allergyInput,
                                    onValueChange = { allergyInput = it },
                                    label = { Text("Add Allergy") },
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (allergyInput.isNotBlank()) {
                                            allergies = allergies + allergyInput
                                            allergyInput = ""
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text("Add")
                                }
                            }

                            allergies.forEach { allergy ->
                                ListItem(
                                    headlineContent = { Text(allergy) },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                allergies = allergies - allergy
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Medical History Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Medical History",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = medicalHistoryInput,
                                    onValueChange = { medicalHistoryInput = it },
                                    label = { Text("Add Medical History") },
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (medicalHistoryInput.isNotBlank()) {
                                            medicalHistory = medicalHistory + medicalHistoryInput
                                            medicalHistoryInput = ""
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text("Add")
                                }
                            }

                            medicalHistory.forEach { history ->
                                ListItem(
                                    headlineContent = { Text(history) },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                medicalHistory = medicalHistory - history
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }
                is BaseUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error loading profile",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { viewModel.getPatientById(patientId) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Changes") },
                text = { Text("Are you sure you want to update your profile?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSaveDialog = false
                            selectedPatient?.let { currentPatient ->
                                viewModel.updatePatientDetails(
                                    patientId,
                                    PatientUpdateRequest(
                                        bloodGroup = bloodGroup.ifBlank { null },
                                        emergencyContact = currentPatient.id.toString(),
                                        allergies = allergies.joinToString(","),
                                        phoneNumber = phoneNumber,
                                        address = address,
                                        medicalHistory = medicalHistory
                                    )
                                )
                                onProfileUpdated()
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showSaveDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}