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
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.ui.navigation.DoctorAppointmentBookingNav
import com.example.hospitalapp.ui.navigation.HealthReportNav
import com.example.hospitalapp.ui.screens.patient.AppointmentsSection
import com.example.hospitalapp.ui.screens.patient.MedicationsSection
import com.example.hospitalapp.ui.screens.patient.VitalsSection
import com.example.hospitalapp.ui.viewModels.*

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

    // State for showing the add medication dialog
    var showAddMedicationDialog by remember { mutableStateOf(false) }

    // Monitor medication creation status
    val createMedicationState = medicationViewModel.createMedicationUiState

    // Show a snackbar message when medication is created
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect to show snackbar on successful creation
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
                    // New medication button
                    IconButton(onClick = { showAddMedicationDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Medication")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
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

                // Show add medication dialog if requested
                if (showAddMedicationDialog) {
                    AddMedicationDialog(
                        patientId = patientId,
                        appointmentId = null, // We're not associating with an appointment in this case
                        onDismiss = { showAddMedicationDialog = false },
                        onAddMedication = { medicationRequest ->
                            medicationViewModel.createMedication(medicationRequest)
                            showAddMedicationDialog = false
                        }
                    )
                }

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