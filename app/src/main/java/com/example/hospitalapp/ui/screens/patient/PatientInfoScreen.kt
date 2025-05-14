package com.example.hospitalapp.ui.screens.patient

import android.R.attr.type
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
import androidx.navigation.NavHostController
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientUpdateRequest
import com.example.hospitalapp.ui.navigation.PatientDashboardNav
import com.example.hospitalapp.ui.navigation.PatientInfoNav
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    viewModel: PatientViewModel,
    patientId: Long,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var showSaveDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var bloodGroup by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf<List<String>>(emptyList()) }
    var allergyInput by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var medicalHistoryInput by remember { mutableStateOf("") }

    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val patientState = viewModel.patientDetailsUiState


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Profile") },
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
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = firstName.isBlank(),
                        supportingText = { if (firstName.isBlank()) Text("Required") }
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = lastName.isBlank(),
                        supportingText = { if (lastName.isBlank()) Text("Required") }
                    )

                    // Gender Dropdown
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("gender") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = lastName.isBlank(),
                        supportingText = { if (gender.isBlank()) Text("Required") }
                    )

                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = dateOfBirth.isBlank(),
                        supportingText = { if (dateOfBirth.isBlank()) Text("Required") }
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
                        minLines = 2,
                        isError = address.isBlank(),
                        supportingText = { if (address.isBlank()) Text("Required") }
                    )

                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth()
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

            Button(
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank() ||
                        gender.isBlank() || dateOfBirth.isBlank() || address.isBlank()
                    ) {
                        return@Button
                    }
                    showSaveDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Profile")
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Create Profile") },
                text = { Text("Create your patient profile?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSaveDialog = false
                            viewModel.createPatientDetails(
                                patientId,
                                PatientRequest(
                                    id = patientId,
                                    email = email,
                                    fName = firstName,
                                    lName = lastName,
                                    phoneNumber = phoneNumber.ifBlank { null },
                                    gender = gender,
                                    dob = dateOfBirth,
                                    address = address,
                                    bloodGroup = bloodGroup.ifBlank { null },
                                    allergies = allergies,
                                    medicalHistory = medicalHistory
                                )
                            )
                            navController.navigate(PatientDashboardNav(patientId))
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
}
