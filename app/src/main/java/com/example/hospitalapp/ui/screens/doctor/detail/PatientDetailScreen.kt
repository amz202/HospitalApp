package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.ui.navigation.DoctorAppointmentBookingNav
import com.example.hospitalapp.ui.viewModels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    patientId: Long,
    patientViewModel: PatientViewModel,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val patientState = patientViewModel.patientDetailsUiState
    val vitalsState = vitalsViewModel.patientVitalsUiState
    val medicationsState = medicationViewModel.patientMedicationsUiState
    val appointmentsState = appointmentViewModel.appointmentsUiState

    LaunchedEffect(patientId) {
        patientViewModel.getPatientDetails(patientId)
        vitalsViewModel.getVitalsByPatient(patientId)
        medicationViewModel.getPatientMedications(patientId)
        appointmentViewModel.getPatientAppointments(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        DoctorAppointmentBookingNav(
                            patientId = patientId,
                            doctorId = 1L // Replace with actual doctorId from viewModel
                        )
                    )
                }
            ) {
                Icon(Icons.Default.Add, "Book Appointment")
            }
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Patient Info Card
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
                                    text = "${patient.fName} ${patient.lName}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("DOB: ${patient.dob}")
                                Text("Blood Group: ${patient.bloodGroup}")
                                Text("Phone: ${patient.phoneNumber}")
                                if (patient.allergies?.isNotBlank() == true) {
                                    Text(
                                        "Allergies: ${patient.allergies}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Recent Vitals
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
                                    text = "Recent Vitals",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                when (vitalsState) {
                                    is BaseUiState.Loading -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                    is BaseUiState.Success -> {
                                        val recentVitals = vitalsState.data.firstOrNull()
                                        if (recentVitals != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Temperature: ${recentVitals.temperature}°C")
                                            Text("Heart Rate: ${recentVitals.heartRate} bpm")
                                            Text("Recorded: ${recentVitals.recordedAt}")
                                        } else {
                                            Text("No vitals recorded")
                                        }
                                    }
                                    is BaseUiState.Error -> {
                                        Text(
                                            "Error loading vitals",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Current Medications
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
                                    text = "Current Medications",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                when (medicationsState) {
                                    is BaseUiState.Loading -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                    is BaseUiState.Success -> {
                                        val currentMeds = medicationsState.data
                                        if (currentMeds.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            currentMeds.forEach { medication ->
                                                Text("${medication.name} - ${medication.dosage}")
                                            }
                                        } else {
                                            Text("No current medications")
                                        }
                                    }
                                    is BaseUiState.Error -> {
                                        Text(
                                            "Error loading medications",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Recent Appointments
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
                                    text = "Recent Appointments",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                when (appointmentsState) {
                                    is BaseUiState.Loading -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                    is BaseUiState.Success -> {
                                        val recentAppointments = appointmentsState.data
                                            .sortedByDescending { it.scheduledTime }
                                            .take(3)
                                        if (recentAppointments.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            recentAppointments.forEach { appointment ->
                                                AppointmentItem(appointment)
                                            }
                                        } else {
                                            Text("No recent appointments")
                                        }
                                    }
                                    is BaseUiState.Error -> {
                                        Text(
                                            "Error loading appointments",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
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
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Error loading patient details",
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentItem(appointment: AppointmentResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = LocalDateTime.parse(appointment.scheduledTime)
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            style = MaterialTheme.typography.bodyMedium
        )
        AssistChip(
            onClick = { },
            label = { Text(appointment.status.name) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = when (appointment.status) {
                    AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                    AppointmentStatus.APPROVED -> MaterialTheme.colorScheme.secondaryContainer
                    AppointmentStatus.REQUESTED -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                }
            )
        )
    }
}