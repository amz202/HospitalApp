package com.example.hospitalapp.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
    val currentDateTime = LocalDateTime.now()

    LaunchedEffect(patientId) {
        appointmentViewModel.getPatientAppointments(patientId)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            navController.navigate(AppointmentDetailNav(patientId = patientId))
        }
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
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = {
                        navController.navigate(AppointmentDetailNav(patientId = patientId))
                    }
                ) {
                    Text("View All")
                }
                TextButton(
                    onClick = {
                        navController.navigate(
                            AppointmentBookingScreenNav(patientId = patientId)
                        )
                    }
                ) {
                    Text("Book New")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (appointmentsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is BaseUiState.Success -> {
                    val appointments = appointmentsState.data
                    if (appointments.isEmpty()) {
                        Text(
                            text = "No appointments scheduled",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        val sortedAppointments = appointments.sortedBy { it.scheduledTime }
                        val upcomingAppointments = sortedAppointments.filter {
                            LocalDateTime.parse(it.scheduledTime, DateTimeFormatter.ISO_DATE_TIME) > currentDateTime
                        }
                        val recentAppointments = sortedAppointments.filter {
                            LocalDateTime.parse(it.scheduledTime, DateTimeFormatter.ISO_DATE_TIME) <= currentDateTime
                        }

                        if (upcomingAppointments.isNotEmpty()) {
                            Text(
                                text = "Upcoming",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            upcomingAppointments.take(2).forEach { appointment ->
                                AppointmentItem(appointment)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (recentAppointments.isNotEmpty()) {
                            if (upcomingAppointments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(
                                text = "Recent",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            recentAppointments.take(1).forEach { appointment ->
                                AppointmentItem(appointment)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Text(
                        text = "Error loading appointments",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentItem(
    appointment: AppointmentResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = LocalDateTime.parse(appointment.scheduledTime)
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = appointment.doctor.specialization,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val (statusColor, statusText) = when (appointment.status) {
                        AppointmentStatus.REQUESTED -> Pair(MaterialTheme.colorScheme.tertiary, "Pending")
                        AppointmentStatus.APPROVED -> Pair(MaterialTheme.colorScheme.primary, "Confirmed")
                        AppointmentStatus.COMPLETED -> Pair(MaterialTheme.colorScheme.secondary, "Completed")
                        AppointmentStatus.CANCELLED -> Pair(MaterialTheme.colorScheme.error, "Cancelled")
                        AppointmentStatus.DECLINED -> Pair(MaterialTheme.colorScheme.error, "Declined")
                    }

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )

                    Text(
                        text = when (appointment.type) {
                            AppointmentType.IN_PERSON -> "In-Person"
                            AppointmentType.VIDEO_CONSULTATION -> "Video Call"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!appointment.reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appointment.reason,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!appointment.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appointment.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (appointment.type == AppointmentType.VIDEO_CONSULTATION && appointment.meetingLink != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Video link available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}