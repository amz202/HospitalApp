package com.example.hospitalapp.ui.screens.doctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hospitalapp.ui.viewModels.*
import com.example.hospitalapp.network.model.*
import com.example.hospitalapp.ui.navigation.DoctorAppointmentDetailNav
import com.example.hospitalapp.ui.navigation.DoctorPatientDetailNav
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.ui.viewModels.*
import com.example.hospitalapp.network.model.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorDashboardScreen(
    doctorId: Long,
    navController: NavHostController,
    doctorViewModel: DoctorViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Patients", "Appointment")

    // Initialize required data
    LaunchedEffect(doctorId) {
        doctorViewModel.getDoctorById(doctorId)
        doctorViewModel.getDoctorPatients(doctorId)
    }

    // Observe UI States
    val doctorState = doctorViewModel.doctorDetailsUiState
    val patientsState = doctorViewModel.doctorPatientsUiState

    Scaffold(
        topBar = {
            Column {
                when (doctorState) {
                    is BaseUiState.Success -> {
                        DoctorHeader(doctor = doctorState.data)
                    }
                    is BaseUiState.Error -> {
                        Text(
                            text = "Error loading doctor information",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is BaseUiState.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            doctorState is BaseUiState.Loading -> {
                LoadingState()
            }
            doctorState is BaseUiState.Error -> {
                ErrorState(
                    onRetry = {
                        doctorViewModel.getDoctorById(doctorId)
                        doctorViewModel.getDoctorPatients(doctorId)
                    }
                )
            }
            doctorState is BaseUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedTab) {
                        0 -> OverviewTab(
                            doctor = doctorState.data,
                            patientsState = patientsState
                        )
                        1 -> PatientsTab(
                            patientsState = patientsState,
                            onPatientClick = { patientId ->
                                navController.navigate(DoctorPatientDetailNav(patientId, doctorId ))
                            }
                        )
                        2 -> AppointmentsTab(
                            doctor = doctorState.data
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorHeader(doctor: DoctorResponse) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Dr. ${doctor.fName} ${doctor.lName}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = doctor.specialization,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Error loading data",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun OverviewTab(
    doctor: DoctorResponse,
    patientsState: BaseUiState<List<PatientResponse>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Person,
                    title = "Total Patients",
                    value = when (patientsState) {
                        is BaseUiState.Success -> patientsState.data.size.toString()
                        else -> "-"
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Doctor Info
        item {
            DoctorInfoCard(doctor = doctor)
        }
    }
}

@Composable
private fun PatientsTab(
    patientsState: BaseUiState<List<PatientResponse>>,
    onPatientClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (patientsState) {
        is BaseUiState.Loading -> {
            LoadingState()
        }
        is BaseUiState.Success -> {
            if (patientsState.data.isEmpty()) {
                EmptyPatientsState()
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(patientsState.data) { patient ->
                        PatientCard(
                            patient = patient,
                            onClick = { onPatientClick(patient.id) }
                        )
                    }
                }
            }
        }
        is BaseUiState.Error -> {
            ErrorState(onRetry = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientCard(
    patient: PatientResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${patient.fName} ${patient.lName}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (!patient.bloodGroup.isNullOrBlank()) {
                    Text(
                        text = "Blood Group: ${patient.bloodGroup}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "View Details →",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyPatientsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No patients assigned yet",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DoctorInfoCard(
    doctor: DoctorResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
                text = "Specialization: ${doctor.specialization}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (doctor.availableForEmergency) {
                Text(
                    text = "✓ Available for Emergency",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentsTab(
    doctor: DoctorResponse,
    modifier: Modifier = Modifier
) {
    // This tab will be implemented with appointments integration
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Appointments coming soon")
    }
}