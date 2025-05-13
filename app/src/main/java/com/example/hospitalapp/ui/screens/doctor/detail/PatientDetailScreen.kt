package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.ui.navigation.DoctorAppointmentBookingNav
import com.example.hospitalapp.ui.navigation.HealthReportNav
import com.example.hospitalapp.ui.screens.patient.AppointmentsSection
import com.example.hospitalapp.ui.screens.patient.MedicationsSection
import com.example.hospitalapp.ui.screens.patient.VitalsSection
import com.example.hospitalapp.ui.viewModels.*

// In your PatientDetailScreen.kt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    patientId: Long,
    patientViewModel: PatientViewModel,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    doctorId: Long
) {
    val patientState = patientViewModel.patientDetailsUiState

    // States for dialogs
    var showAppointmentSelector by remember { mutableStateOf(false) }
    var showAddMedicationDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<AppointmentResponse?>(null) }

    // Current date time from your system
    val currentDateTime = "2025-05-13 11:37:08"

    // Monitor medication creation status
    val createMedicationState = medicationViewModel.createMedicationUiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect to show snackbar messages
    LaunchedEffect(createMedicationState) {
        when (createMedicationState) {
            is BaseUiState.Success -> {
                if (createMedicationState.data != null) {
                    snackbarHostState.showSnackbar(
                        message = "Medication added successfully",
                        duration = SnackbarDuration.Short
                    )
                    medicationViewModel.resetCreateMedicationState()
                }
            }
            is BaseUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Failed to add medication. Please try again.",
                    duration = SnackbarDuration.Short
                )
                medicationViewModel.resetCreateMedicationState()
            }
            else -> {}
        }
    }

    LaunchedEffect(patientId) {
        patientViewModel.getPatientById(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (patientState) {
                        is BaseUiState.Success -> {
                            val patient = patientState.data
                            Text("${patient.fName} ${patient.lName}")
                        }
                        else -> Text("Patient Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Add medication button - shows appointment selector first
                    IconButton(onClick = { showAppointmentSelector = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Medication")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // First, show appointment selector when the Add button is clicked
        if (showAppointmentSelector) {
            AppointmentSelectionDialog(
                patientId = patientId,
                appointmentViewModel = appointmentViewModel,
                onSelect = { appointment ->
                    selectedAppointment = appointment
                    showAppointmentSelector = false
                    showAddMedicationDialog = true  // Show medication dialog after selection
                },
                onDismiss = {
                    showAppointmentSelector = false
                }
            )
        }

        // Then show medication dialog after appointment is selected
        if (showAddMedicationDialog && selectedAppointment != null) {
            AddMedicationDialog(
                patientId = patientId,
                appointmentId = selectedAppointment!!.id,  // Use the selected appointment ID
                onDismiss = {
                    showAddMedicationDialog = false
                    selectedAppointment = null  // Reset selected appointment
                },
                onAddMedication = { medicationRequest ->
                    // Create a new request with the correct date format
                    val updatedRequest = medicationRequest.copy(
                        startDate = currentDateTime,
                        endDate = medicationRequest.endDate?.let { currentDateTime }
                    )

                    medicationViewModel.createMedication(updatedRequest)
                    showAddMedicationDialog = false
                    selectedAppointment = null  // Reset selected appointment
                }
            )
        }

        when (patientState) {
            is BaseUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BaseUiState.Success -> {
                val patient = patientState.data
                PatientDetailContent(
                    patient = patient,
                    patientId = patientId,
                    doctorId = doctorId,
                    vitalsViewModel = vitalsViewModel,
                    medicationViewModel = medicationViewModel,
                    appointmentViewModel = appointmentViewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
            is BaseUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load patient details")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PatientDetailContent(
    patient: PatientResponse,
    patientId: Long,
    doctorId: Long,
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Patient info card
        PatientInfoCard(patient)

        // Doctor actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    navController.navigate(
                        DoctorAppointmentBookingNav(patientId = patientId, doctorId = doctorId)
                    )
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Schedule Appointment")
            }

            Button(
                onClick = {
                    navController.navigate(
                        HealthReportNav(patientId = patientId)
                    )
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("View Health Report")
            }
        }

        MedicationsSection(
            patientId = patientId,
            medicationViewModel = medicationViewModel,
            navController = navController
        )

        AppointmentsSection(
            patientId = patientId,
            appointmentViewModel = appointmentViewModel,
            navController = navController
        )
    }
}

@Composable
private fun PatientInfoCard(patient: PatientResponse) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${patient.fName} ${patient.lName}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))


            patient.gender?.let {
                Text(
                    text = "Gender: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            patient.phoneNumber?.let {
                Text(
                    text = "Phone: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            patient.email?.let {
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}