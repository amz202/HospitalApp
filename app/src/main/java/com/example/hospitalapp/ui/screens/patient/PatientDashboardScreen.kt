package com.example.hospitalapp.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hospitalapp.ui.navigation.HealthReportNav
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.ui.viewModels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientDashboardScreen(
    patientId: Long,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    var showEmergencyDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(patientId) {
        patientViewModel.getPatientById(patientId)
        vitalsViewModel.getVitalsByPatient(patientId)
        medicationViewModel.getPatientMedications(patientId)
        appointmentViewModel.getPatientAppointments(patientId)
    }

    val patientState = patientViewModel.patientDetailsUiState

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Patient Dashboard")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
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
                val patient = patientState.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Patient Info Header
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "ID: ${patient.id}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    if (patient.bloodGroup != null) {
                                        Text(
                                            text = "Blood Group: ${patient.bloodGroup}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Button(
                                    onClick = { showEmergencyDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text("Emergency")
                                }
                            }
                            Text(
                                text = "Member since: ${patient.accountCreationDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        // Medical Info Section
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Medical Information",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (patient.allergies.isNotEmpty()) {
                                        Text(
                                            text = "Allergies:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        patient.allergies.forEach { allergy ->
                                            Text(
                                                text = "• $allergy",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    if (patient.medicalHistory.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Medical History:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        patient.medicalHistory.forEach { history ->
                                            Text(
                                                text = "• $history",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Vitals Section
                        item {
                            VitalsSection(
                                patientId = patientId,
                                vitalsViewModel = vitalsViewModel,
                                navController = navController
                            )
                        }

                        // Medications Section
                        item {
                            MedicationsSection(
                                patientId = patientId,
                                medicationViewModel = medicationViewModel,
                                navController = navController
                            )
                        }

                        // Appointments Section
                        item {
                            AppointmentsSection(
                                patientId = patientId,
                                appointmentViewModel = appointmentViewModel,
                                navController = navController
                            )
                        }

                        // Health Report Button
                        item {
                            Button(
                                onClick = {
                                    navController.navigate(HealthReportNav(patientId = patientId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("View Health Report")
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
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
                            text = "Error loading patient data",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { patientViewModel.getPatientById(patientId) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        // Emergency Alert Dialog
        if (showEmergencyDialog) {
            AlertDialog(
                onDismissRequest = { showEmergencyDialog = false },
                icon = {
                    Icon(Icons.Default.Warning, contentDescription = null)
                },
                title = {
                    Text(
                        text = "Emergency Alert",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text("Do you want to send an emergency alert to available doctors?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEmergencyDialog = false
                            // TODO: Implement emergency alert functionality
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Send Alert")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEmergencyDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}