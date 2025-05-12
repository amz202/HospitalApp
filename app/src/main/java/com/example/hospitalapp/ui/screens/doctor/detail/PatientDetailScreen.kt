package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.navigation.DoctorAppointmentBookingNav
import com.example.hospitalapp.ui.viewModels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.hospitalapp.network.model.AppointmentStatus

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    patientId: Long,
    doctorId:Long,
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
        patientViewModel.getPatientById(patientId)
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
                    navController.navigate(DoctorAppointmentBookingNav(patientId, doctorId))
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
                                Text("ID: ${patient.id}")
                                Text("Gender: ${patient.gender}")
                                Text("DOB: ${patient.dob}")
                                Text("Blood Group: ${patient.bloodGroup}")
                                if (!patient.allergies.isNullOrEmpty()) {
                                    Text("Allergies: ${patient.allergies}")
                                }
                                Text("Phone: ${patient.phoneNumber}")
                                Text("Address: ${patient.address}")
                            }
                        }
                    }

                    // Vitals Section
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
                                        if (vitalsState.data.isNotEmpty()) {
                                            val latestVitals = vitalsState.data.first()
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Heart Rate: ${latestVitals.heartRate}")
                                            Text("Temperature: ${latestVitals.temperature}°C")
                                            Text("Respiratory Rate: ${latestVitals.respiratoryRate}")
                                            Text(
                                                "Recorded: ${
                                                    LocalDateTime.parse(latestVitals.recordedAt)
                                                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                                                }"
                                            )
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

                    // Medications Section
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
                                        if (medicationsState.data.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            medicationsState.data.forEach { medication ->
                                                Text("${medication.name} - ${medication.dosage}")
                                                Text(
                                                    "Instructions: ${medication.instructions}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
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

                    // Appointments Section
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
                                        if (appointmentsState.data.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            appointmentsState.data.take(5).forEach { appointment ->
                                                ListItem(
                                                    headlineContent = {
                                                        Text(
                                                            LocalDateTime.parse(appointment.scheduledTime)
                                                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                                                        )
                                                    },
                                                    supportingContent = {
                                                        Text(appointment.type)
                                                    },
                                                    trailingContent = {
                                                        AssistChip(
                                                            onClick = { },
                                                            label = { Text(appointment.status.name) },
                                                            colors = AssistChipDefaults.assistChipColors(
                                                                containerColor = when (appointment.status) {
                                                                    AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                                                                    AppointmentStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                                                                    AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                                                                    AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.tertiaryContainer
                                                                    else -> MaterialTheme.colorScheme.errorContainer
                                                                }
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                        } else {
                                            Text("No appointments found")
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
                    Text("Error loading patient details")
                }
            }
        }
    }
}