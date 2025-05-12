package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentDetailScreen(
    appointmentId: Long,
    doctorId: Long,
    viewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    val appointmentState = viewModel.appointmentDetailsUiState

    LaunchedEffect(appointmentId, doctorId) {
        viewModel.getDoctorAppointments(doctorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (appointmentState) {
            is BaseUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BaseUiState.Success -> {
                val appointment = appointmentState.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Appointment Info Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Appointment Information",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Patient Info
                            Text(
                                text = "Patient: ${appointment.patient.fName} ${appointment.patient.lName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Patient ID: ${appointment.patient.id}")

                            Spacer(modifier = Modifier.height(16.dp))

                            // Appointment Details
                            Text(
                                "Date: ${
                                    LocalDateTime.parse(appointment.scheduledTime)
                                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                }"
                            )
                            Text(
                                "Time: ${
                                    LocalDateTime.parse(appointment.scheduledTime)
                                        .format(DateTimeFormatter.ofPattern("hh:mm a"))
                                }"
                            )
                            Text("Type: ${appointment.type}")
                            Text("Reason: ${appointment.reason}")

                            Spacer(modifier = Modifier.height(16.dp))

                            // Status Chip
                            AssistChip(
                                onClick = { showStatusDialog = true },
                                label = { Text(appointment.status.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (appointment.status) {
                                            AppointmentStatus.COMPLETED -> Icons.Default.CheckCircle
                                            AppointmentStatus.CANCELLED -> Icons.Default.Cancel
                                            AppointmentStatus.PENDING -> Icons.Default.CheckCircle
                                            AppointmentStatus.CONFIRMED -> Icons.Default.CheckCircle
                                        },
                                        contentDescription = null
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = when (appointment.status) {
                                        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                                        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                                        AppointmentStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer
                                        AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer

                                        else -> MaterialTheme.colorScheme.errorContainer
                                    }
                                )
                            )
                        }
                    }

                    // Notes Section
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (!appointment.notes.isNullOrBlank()) {
                                Text(
                                    text = appointment.notes,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "No notes available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Status Update Dialog
                if (showStatusDialog) {
                    AlertDialog(
                        onDismissRequest = { showStatusDialog = false },
                        title = { Text("Update Appointment Status") },
                        text = {
                            Column {
                                AppointmentStatus.values().forEach { status ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = status == appointment.status,
                                            onClick = {
                                                viewModel.updateAppointmentStatus(
                                                    appointmentId,
                                                    status
                                                )
                                                showStatusDialog = false
                                            }
                                        )
                                        Text(
                                            text = status.name,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showStatusDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            is BaseUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error loading appointment details")
                }
            }
        }
    }
}