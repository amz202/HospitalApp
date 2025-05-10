package com.example.hospitalapp.ui.patient

import androidx.compose.foundation.lazy.LazyColumn
import com.example.hospitalapp.network.model.PatientResponse
import kotlinx.coroutines.launch

package com.example.hospitalapp.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hospitalapp.ui.viewModels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientDashboardStateScreen(
    patientId: Long,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    // Initialize all required data
    LaunchedEffect(patientId) {
        patientViewModel.getPatientDetails(patientId)
        vitalsViewModel.getVitalsByPatient(patientId)
        medicationViewModel.getPatientMedications(patientId)
        appointmentViewModel.getPatientAppointments(patientId)
    }

    // Observe UI States
    val patientState = patientViewModel.patientDetailsUiState

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
            PatientDashboard(
                patientId = patientId,
                patient = patientState.data,
                navController = navController,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
            )
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Error loading patient data",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = {
                        patientViewModel.getPatientDetails(patientId)
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDashboard(
    patientId: Long,
    patient: PatientResponse,
    navController: NavHostController,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEmergencyDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Patient Dashboard") },
                actions = {
                    // Emergency Alert Button
                    Button(
                        onClick = { showEmergencyDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Emergency")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Patient Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Patient: ${patient.firstName}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "ID: $patientId",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Last Updated: ${
                            LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Main Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    VitalsSection(
                        patientId = patientId,
                        vitalsViewModel = vitalsViewModel,
                        onNavigateToVitalsDetail = {
                            navController.navigate("vitals_detail/$patientId")
                        }
                    )
                }

                item {
                    MedicationsSection(
                        patientId = patientId,
                        medicationViewModel = medicationViewModel,
                        onNavigateToMedicationDetail = {
                            navController.navigate("medication_detail/$patientId")
                        }
                    )
                }

                item {
                    AppointmentsSection(
                        patientId = patientId,
                        appointmentViewModel = appointmentViewModel,
                        onNavigateToAppointmentDetail = {
                            navController.navigate("appointment_detail/$patientId")
                        }
                    )
                }

                item {
                    Button(
                        onClick = {
                            navController.navigate("health_report/$patientId")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Health Report")
                    }
                }
            }
        }

        // Emergency Alert Dialog
        if (showEmergencyDialog) {
            AlertDialog(
                onDismissRequest = { showEmergencyDialog = false },
                title = {
                    Text(
                        text = "Emergency Alert",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text("This will notify emergency services immediately. Continue?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEmergencyDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Emergency Alert Triggered",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEmergencyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}