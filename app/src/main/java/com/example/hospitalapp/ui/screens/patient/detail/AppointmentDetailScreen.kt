package com.example.hospitalapp.ui.screens.patient.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.network.model.AppointmentType
import com.example.hospitalapp.ui.navigation.AppointmentBookingScreenNav
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    patientId: Long,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val appointmentsState = appointmentViewModel.appointmentsUiState
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(AppointmentFilter.ALL) }
    val currentDateTime = remember {
        LocalDateTime.parse("2025-05-10 08:46:23", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    LaunchedEffect(patientId) {
        appointmentViewModel.getPatientAppointments(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointments") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        AppointmentBookingScreenNav(patientId = patientId)
                    )
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Book Appointment")
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (appointmentsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BaseUiState.Success -> {
                    val appointments = appointmentsState.data
                    if (appointments.isEmpty()) {
                        Text(
                            text = "No appointments found",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val filteredAppointments = when (selectedFilter) {
                            AppointmentFilter.UPCOMING -> appointments.filter {
                                LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime)
                            }
                            AppointmentFilter.PAST -> appointments.filter {
                                LocalDateTime.parse(it.scheduledTime).isBefore(currentDateTime)
                            }
                            AppointmentFilter.REQUESTED -> appointments.filter {
                                it.status == AppointmentStatus.REQUESTED
                            }
                            AppointmentFilter.APPROVED -> appointments.filter {
                                it.status == AppointmentStatus.APPROVED
                            }
                            AppointmentFilter.ALL -> appointments
                        }.sortedBy { it.scheduledTime }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                AppointmentSummaryCard(
                                    appointments = filteredAppointments,
                                    filter = selectedFilter,
                                    currentDateTime = currentDateTime
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(filteredAppointments) { appointment ->
                                AppointmentDetailCard(
                                    appointment = appointment,
                                    currentDateTime = currentDateTime
                                )
                            }

                            // Add bottom padding for FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error loading appointments",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                appointmentViewModel.getPatientAppointments(patientId)
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
                else -> Unit
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filter Appointments") },
                text = {
                    Column {
                        AppointmentFilter.values().forEach { filter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter }
                                )
                                Text(
                                    text = filter.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentSummaryCard(
    appointments: List<AppointmentResponse>,
    filter: AppointmentFilter,
    currentDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = filter.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            val summaryText = when (filter) {
                AppointmentFilter.UPCOMING -> {
                    val nextAppointment = appointments.firstOrNull()
                    if (nextAppointment != null) {
                        "Next appointment: ${
                            LocalDateTime.parse(nextAppointment.scheduledTime)
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        }"
                    } else {
                        "No upcoming appointments"
                    }
                }
                else -> "${appointments.size} appointment(s)"
            }

            Text(
                text = summaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentDetailCard(
    appointment: AppointmentResponse,
    currentDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val appointmentDateTime = LocalDateTime.parse(appointment.scheduledTime)
    val isUpcoming = appointmentDateTime.isAfter(currentDateTime)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointmentDateTime.format(
                            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Dr. ${appointment.doctor.fName} ${appointment.doctor.lName}",
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
                        style = MaterialTheme.typography.bodyMedium,
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason: ${appointment.reason}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!appointment.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appointment.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (appointment.type == AppointmentType.VIDEO_CONSULTATION &&
                appointment.meetingLink != null &&
                isUpcoming &&
                appointment.status == AppointmentStatus.APPROVED
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { /* Handle video call link */ }
                ) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Join Video Call")
                }
            }
        }
    }
}

private enum class AppointmentFilter(val displayName: String) {
    UPCOMING("Upcoming Appointments"),
    PAST("Past Appointments"),
    REQUESTED("Pending Requests"),
    APPROVED("Confirmed Appointments"),
    ALL("All Appointments")
}