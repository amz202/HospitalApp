package com.example.hospitalapp.ui.patient.detail

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.*
import com.example.hospitalapp.network.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReportScreen(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showDateRangeDialog by remember { mutableStateOf(false) }
    val currentDateTime = remember {
        LocalDateTime.parse("2025-05-10 09:59:19", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    LaunchedEffect(patientId) {
        vitalsViewModel.getVitalsById(patientId)
        medicationViewModel.getPatientMedications(patientId)
        appointmentViewModel.getPatientAppointments(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Report") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share report */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ReportHeaderCard(currentDateTime)
            }

            // Vitals Summary
            item {
                ReportSection(
                    title = "Vitals Overview",
                    icon = Icons.Default.Favorite
                ) {
                    when (val state = vitalsViewModel.patientVitalsUiState) {
                        is BaseUiState.Success -> {
                            val latestVitals = state.data.maxByOrNull { it.recordedAt }
                            if (latestVitals != null) {
                                VitalsReportCard(latestVitals)
                            } else {
                                Text("No vitals data available")
                            }
                        }
                        is BaseUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is BaseUiState.Error -> {
                            Text(
                                "Error loading vitals data",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> Unit
                    }
                }
            }

            // Medications Summary
            item {
                ReportSection(
                    title = "Active Medications",
                    icon = Icons.Default.Medication
                ) {
                    when (val state = medicationViewModel.patientMedicationsUiState) {
                        is BaseUiState.Success -> {
                            val activeMedications = state.data.filter { it.active }
                            if (activeMedications.isNotEmpty()) {
                                MedicationsReportCard(activeMedications)
                            } else {
                                Text("No active medications")
                            }
                        }
                        is BaseUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is BaseUiState.Error -> {
                            Text(
                                "Error loading medications data",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> Unit
                    }
                }
            }

            // Appointments Summary
            item {
                ReportSection(
                    title = "Upcoming Appointments",
                    icon = Icons.Default.Group
                ) {
                    when (val state = appointmentViewModel.appointmentsUiState) {
                        is BaseUiState.Success -> {
                            val upcomingAppointments = state.data
                                .filter {
                                    LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime)
                                }
                                .sortedBy { it.scheduledTime }
                                .take(3)

                            if (upcomingAppointments.isNotEmpty()) {
                                AppointmentsReportCard(upcomingAppointments)
                            } else {
                                Text("No upcoming appointments")
                            }
                        }
                        is BaseUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is BaseUiState.Error -> {
                            Text(
                                "Error loading appointments data",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> Unit
                    }
                }
            }

            // Health Summary
            item {
                ReportSection(
                    title = "Health Summary",
                    icon = Icons.Default.Assessment
                ) {
                    HealthSummaryCard(
                        vitalsViewModel.patientVitalsUiState,
                        medicationViewModel.patientMedicationsUiState,
                        appointmentViewModel.appointmentsUiState,
                        currentDateTime
                    )
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReportHeaderCard(
    currentDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                text = "Health Report Summary",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generated on: ${currentDateTime.format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                )}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ReportSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            content()
        }
    }
}

@Composable
private fun VitalsReportCard(vitals: VitalsResponse) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItem("Heart Rate", "${vitals.heartRate ?: "--"} bpm")
            VitalItem("Blood Pressure", "${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItem("Temperature", "${vitals.temperature?.let { "%.1f".format(it) } ?: "--"}°C")
            VitalItem("SpO2", "${vitals.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%")
        }
    }
}

@Composable
private fun VitalItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MedicationsReportCard(medications: List<MedicationResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        medications.forEach { medication ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${medication.dosage} - ${medication.frequency}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Until ${
                        LocalDateTime.parse(medication.endDate)
                            .format(DateTimeFormatter.ofPattern("MMM dd"))
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentsReportCard(appointments: List<AppointmentResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        appointments.forEach { appointment ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = LocalDateTime.parse(appointment.scheduledTime)
                            .format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = when (appointment.type) {
                        AppointmentType.IN_PERSON -> "In-Person"
                        AppointmentType.VIDEO_CONSULTATION -> "Video"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HealthSummaryCard(
    vitalsState: BaseUiState<List<VitalsResponse>>,
    medicationsState: BaseUiState<List<MedicationResponse>>,
    appointmentsState: BaseUiState<List<AppointmentResponse>>,
    currentDateTime: LocalDateTime
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Critical Vitals Summary
        if (vitalsState is BaseUiState.Success) {
            val criticalVitals = vitalsState.data.filter { it.critical }
            if (criticalVitals.isNotEmpty()) {
                Text(
                    text = "⚠️ ${criticalVitals.size} vital measurements need attention",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Active Medications Count
        if (medicationsState is BaseUiState.Success) {
            val activeMedications = medicationsState.data.filter { it.active }
            Text(
                text = "💊 Currently taking ${activeMedications.size} medications",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Upcoming Appointments
        if (appointmentsState is BaseUiState.Success) {
            val upcomingAppointments = appointmentsState.data.filter {
                LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime)
            }
            Text(
                text = "📅 ${upcomingAppointments.size} upcoming appointments",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}