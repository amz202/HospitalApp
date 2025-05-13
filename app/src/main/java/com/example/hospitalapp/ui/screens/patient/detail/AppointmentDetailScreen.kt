package com.example.hospitalapp.ui.screens.patient.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: Long,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentDateTime = LocalDateTime.now()


    var showCancelDialog by remember { mutableStateOf(false) }
    val appointmentState = appointmentViewModel.appointmentDetailsUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Appointment Status Card
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Status",
                                        style = MaterialTheme.typography.titleMedium
                                    )
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
                                }
                                if (appointment.status != AppointmentStatus.CANCELLED &&
                                    appointment.status != AppointmentStatus.COMPLETED &&
                                    LocalDateTime.parse(appointment.scheduledTime).isAfter(currentDateTime)
                                ) {
                                    Button(
                                        onClick = { showCancelDialog = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    ) {
                                        Text("Cancel Appointment")
                                    }
                                }
                            }
                        }
                    }

                    // Doctor Information Card
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Doctor Information",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Specialization: ${appointment.doctor.specialization}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Appointment Details Card
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
                                text = "Appointment Details",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LocalDateTime.parse(appointment.scheduledTime)
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when (appointment.type.uppercase()) {
                                        "IN_PERSON" -> Icons.Default.Person
                                        "VIDEO_CONSULTATION" -> Icons.Default.VideoCall
                                        else -> Icons.Default.Event
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (appointment.type.uppercase()) {
                                        "IN_PERSON" -> "In-Person"
                                        "VIDEO_CONSULTATION" -> "Video Consultation"
                                        else -> appointment.type
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (!appointment.reason.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reason: ${appointment.reason}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            if (!appointment.notes.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notes,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Notes: ${appointment.notes}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            if (appointment.type.uppercase() == "VIDEO_CONSULTATION" &&
                                !appointment.meetingLink.isNullOrBlank()
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { /* Handle video call link */ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.VideoCall,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Join Video Call")
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
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Error loading appointment details",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                appointmentViewModel.getAppointmentById(appointmentId)
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