package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentSelectionDialog(
    patientId: Long,
    appointmentViewModel: AppointmentViewModel,
    onSelect: (AppointmentResponse) -> Unit,
    onDismiss: () -> Unit
) {
    // Fetch patient's appointments
    LaunchedEffect(patientId) {
        appointmentViewModel.getPatientAppointments(patientId)
    }

    val appointmentsState = appointmentViewModel.appointmentsUiState

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select an Appointment",
                    style = MaterialTheme.typography.titleLarge
                )

                when (appointmentsState) {
                    is BaseUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is BaseUiState.Success -> {
                        val appointments = appointmentsState.data

                        if (appointments.isEmpty()) {
                            Text(
                                text = "No appointments found. Please create an appointment first.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(appointments) { appointment ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { onSelect(appointment) },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = "Appointment #${appointment.id}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "Date: ${appointment.scheduledTime}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            if (appointment.status != null) {
                                                Text(
                                                    text = "Status: ${appointment.status}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is BaseUiState.Error -> {
                        Text(
                            text = "Failed to load appointments",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}