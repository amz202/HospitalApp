package com.example.hospitalapp.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.network.model.AppointmentType
import com.example.hospitalapp.ui.navigation.AppointmentBookingScreenNav
import com.example.hospitalapp.ui.navigation.AppointmentDetailNav
import com.example.hospitalapp.ui.navigation.DoctorSelectionNav
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentsSection(
    patientId: Long,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val appointmentsState = appointmentViewModel.appointmentsUiState

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Appointments",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = {
                        navController.navigate(DoctorSelectionNav(patientId))
                    }
                ) {
                    Text("Book New")
                }
            }

            when (appointmentsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )
                }
                is BaseUiState.Success -> {
                    if (appointmentsState.data.isEmpty()) {
                        Text(
                            text = "No appointments scheduled",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        appointmentsState.data
                            .sortedBy { it.scheduledTime }
                            .forEach { appointment ->
                                val appointmentDateTime = LocalDateTime.parse(appointment.scheduledTime)
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "${appointment.doctor.id}",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    },
                                    supportingContent = {
                                        Column {
                                            Text(
                                                text = appointmentDateTime.format(
                                                    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")
                                                ),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = when (appointment.type.uppercase()) {
                                                    "IN_PERSON" -> "In-Person"
                                                    "VIDEO_CONSULTATION" -> "Video Call"
                                                    else -> appointment.type
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (!appointment.reason.isNullOrBlank()) {
                                                Text(
                                                    text = "Reason: ${appointment.reason}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    trailingContent = {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text(appointment.status.name) },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = when (appointment.status) {
                                                    AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                                                    AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.secondaryContainer
                                                    AppointmentStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                                                    AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                                                }
                                            )
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(AppointmentBookingScreenNav(patientId= patientId, doctorId = appointment.doctor.id))
                                    }
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                    }
                }
                is BaseUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Error loading appointments",
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(
                            onClick = {
                                appointmentViewModel.getPatientAppointments(patientId)
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}