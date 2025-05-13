package com.example.hospitalapp.ui.screens.patient.detail

import android.content.Context
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
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.*
import com.example.hospitalapp.network.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.compareTo
import kotlin.text.format
import com.example.hospitalapp.ui.viewModels.*
import com.example.hospitalapp.network.model.*
import kotlin.text.compareTo
import kotlin.text.format
import android.widget.Toast

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReportScreen(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    patientViewModel: PatientViewModel,
    modifier: Modifier = Modifier
) {
    var showDateRangeDialog by remember { mutableStateOf(false) }
    val currentDateTime = LocalDateTime.now()
    val context = LocalContext.current

    // Get patient information
    LaunchedEffect(patientId) {
        vitalsViewModel.getVitalsByPatient(patientId)
        medicationViewModel.getPatientMedications(patientId)
        appointmentViewModel.getPatientAppointments(patientId)
        patientViewModel.getPatientById(patientId)
    }

    val vitalsState = vitalsViewModel.patientVitalsUiState
    val medicationsState = medicationViewModel.patientMedicationsUiState
    val appointmentsState = appointmentViewModel.appointmentsUiState
    val patientState = patientViewModel.patientDetailsUiState

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
                    IconButton(onClick = {
                        shareHealthReport(
                            context = context,
                            currentDateTime = currentDateTime,
                            vitalsState = vitalsState,
                            medicationsState = medicationsState,
                            appointmentsState = appointmentsState,
                            patientState = patientState
                        )
                    }) {
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
                text = "Generated on: ${
                    currentDateTime.format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                    )
                }",
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
            VitalItem(
                "Blood Pressure",
                "${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}"
            )
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
                val dateText = if (!medication.endDate.isNullOrEmpty()) {
                    try {
                        val endDate = LocalDateTime.parse(medication.endDate)
                        "Until ${endDate.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                    } catch (e: Exception) {
                        "End date unknown"
                    }
                } else {
                    "Ongoing"
                }

                Text(
                    text = dateText,
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
                }
                Text(
                    text = when (appointment.type.uppercase()) {
                        "IN_PERSON" -> "In-Person"
                        "VIDEO_CONSULTATION" -> "Video"
                        else -> appointment.type // fallback to the raw type string
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

@RequiresApi(Build.VERSION_CODES.O)
private fun shareHealthReport(
    context: Context,
    currentDateTime: LocalDateTime,
    vitalsState: BaseUiState<List<VitalsResponse>>,
    medicationsState: BaseUiState<List<MedicationResponse>>,
    appointmentsState: BaseUiState<List<AppointmentResponse>>,
    patientState: BaseUiState<PatientResponse>
) {
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"

        // Format subject line with patient name if available
        val patientName = if (patientState is BaseUiState.Success) {
            "${patientState.data.fName} ${patientState.data.lName}"
        } else {
            "Patient"
        }

        putExtra(
            Intent.EXTRA_SUBJECT, "Health Report for $patientName - ${
                currentDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            }"
        )

        // Build the email body using a StringBuilder for better performance
        val emailBody = StringBuilder().apply {
            // Header
            append("HEALTH REPORT SUMMARY\n")
            append("Generated on: ${currentDateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm"))}\n\n")

            // Patient Info
            if (patientState is BaseUiState.Success) {
                val patient = patientState.data
                append("PATIENT INFORMATION\n")
                append("Name: ${patient.fName} ${patient.lName}\n")
                append("\n")
            }

            // Vitals
            append("VITALS OVERVIEW\n")
            if (vitalsState is BaseUiState.Success) {
                val latestVitals = vitalsState.data.maxByOrNull { it.recordedAt }
                if (latestVitals != null) {
                    append(
                        "Latest readings recorded on: ${
                            LocalDateTime.parse(latestVitals.recordedAt)
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        }\n"
                    )
                    append("Heart Rate: ${latestVitals.heartRate ?: "N/A"} bpm\n")
                    append("Blood Pressure: ${latestVitals.systolicPressure ?: "N/A"}/${latestVitals.diastolicPressure ?: "N/A"} mmHg\n")
                    append("Temperature: ${latestVitals.temperature?.let { "%.1f".format(it) } ?: "N/A"}°C\n")
                    append(
                        "Oxygen Saturation: ${
                            latestVitals.oxygenSaturation?.let {
                                "%.1f".format(
                                    it
                                )
                            } ?: "N/A"
                        }%\n")
                    if (latestVitals.respiratoryRate != null) {
                        append("Respiratory Rate: ${latestVitals.respiratoryRate} breaths/min\n")
                    }
                    if (latestVitals.bloodSugar != null) {
                        append("Blood Sugar: ${latestVitals.bloodSugar} mg/dL\n")
                    }

                    if (latestVitals.critical) {
                        append("\n⚠️ CRITICAL VALUES DETECTED ⚠️\n")
                        if (!latestVitals.criticalNotes.isNullOrBlank()) {
                            append("Notes: ${latestVitals.criticalNotes}\n")
                        }
                    }
                } else {
                    append("No vitals data available\n")
                }
            } else {
                append("Vitals data unavailable\n")
            }
            append("\n")

            // Medications
            append("ACTIVE MEDICATIONS\n")
            if (medicationsState is BaseUiState.Success) {
                val activeMedications = medicationsState.data.filter { it.active }
                if (activeMedications.isNotEmpty()) {
                    activeMedications.forEachIndexed { index, medication ->
                        val endDateText = if (!medication.endDate.isNullOrEmpty()) {
                            try {
                                val endDate = LocalDateTime.parse(medication.endDate)
                                "Until ${endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
                            } catch (e: Exception) {
                                "End date unknown"
                            }
                        } else {
                            "Ongoing"
                        }

                        append("${index + 1}. ${medication.name} - ${medication.dosage}, ${medication.frequency}\n")
                        append("   ${endDateText}\n")
                        if (!medication.instructions.isNullOrBlank()) {
                            append("   Instructions: ${medication.instructions}\n")
                        }
                    }
                } else {
                    append("No active medications\n")
                }
            } else {
                append("Medication data unavailable\n")
            }
            append("\n")

            // Upcoming Appointments
            append("UPCOMING APPOINTMENTS\n")
            if (appointmentsState is BaseUiState.Success) {
                val upcomingAppointments = appointmentsState.data
                    .filter { LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime) }
                    .sortedBy { it.scheduledTime }

                if (upcomingAppointments.isNotEmpty()) {
                    upcomingAppointments.forEachIndexed { index, appointment ->
                        val appointmentDate = LocalDateTime.parse(appointment.scheduledTime)
                        val appointmentType = when (appointment.type.uppercase()) {
                            "IN_PERSON" -> "In-Person"
                            "VIDEO_CONSULTATION" -> "Video Consultation"
                            else -> appointment.type
                        }

                        append("${index + 1}. ${appointmentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))} - $appointmentType\n")
                        if (!appointment.notes.isNullOrBlank()) {
                            append("   Notes: ${appointment.notes}\n")
                        }
                    }
                } else {
                    append("No upcoming appointments\n")
                }
            } else {
                append("Appointment data unavailable\n")
            }
            append("\n")

            // Health Summary
            append("HEALTH SUMMARY\n")
            if (vitalsState is BaseUiState.Success) {
                val criticalVitals = vitalsState.data.filter { it.critical }
                if (criticalVitals.isNotEmpty()) {
                    append("⚠️ ${criticalVitals.size} vital measurements need attention\n")
                }
            }

            if (medicationsState is BaseUiState.Success) {
                val activeMedications = medicationsState.data.filter { it.active }
                append("💊 Currently taking ${activeMedications.size} medications\n")
            }

            if (appointmentsState is BaseUiState.Success) {
                val upcomingAppointments = appointmentsState.data.filter {
                    LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime)
                }
                append("📅 ${upcomingAppointments.size} upcoming appointments\n")
            }

            // Footer
            append("\n\n")
            append("This report is generated automatically from the Hospital App.")
            append("\n\n")
            append("CONFIDENTIAL MEDICAL INFORMATION")
            append("\nThis email contains confidential medical information and is intended only for the recipient.")
        }

        putExtra(Intent.EXTRA_TEXT, emailBody.toString())
    }

    // Launch email chooser
    try {
        context.startActivity(Intent.createChooser(emailIntent, "Send Health Report via..."))
    } catch (ex: android.content.ActivityNotFoundException) {
        Toast.makeText(context, "No email apps installed.", Toast.LENGTH_SHORT).show()
    }
}