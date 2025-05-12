package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val appointmentState = appointmentViewModel.appointmentDetailsUiState
    var showStatusDialog by remember { mutableStateOf(false) }
    val currentDateTime = LocalDateTime.parse("2025-05-11 10:07:58", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    LaunchedEffect(appointmentId) {
        appointmentViewModel.getAppointmentById(appointmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showStatusDialog = true }) {
                        Icon(Icons.Default.Edit, "Update Status")
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
                            Spacer(modifier = Modifier.height(8.dp))

                            // Patient Info
                            Text(
                                text = "Patient: ${appointment.patient.fName} ${appointment.patient.lName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Patient ID: ${appointment.patient.id}")

                            Spacer(modifier = Modifier.height(8.dp))

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

                            Spacer(modifier = Modifier.height(8.dp))
                            AssistChip(
                                onClick = { showStatusDialog = true },
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
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (!appointment.notes.isNullOrBlank()) {
                                Text(
                                    text = appointment.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
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
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = status == appointment.status,
                                            onClick = {
                                                appointmentViewModel.updateAppointmentStatus(
                                                    appointment.id,
                                                    status
                                                )
                                                showStatusDialog = false
                                            }
                                        )
                                        Text(status.name)
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
                            "Error loading appointment details",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = {
                            appointmentViewModel.getAppointmentById(appointmentId)
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}