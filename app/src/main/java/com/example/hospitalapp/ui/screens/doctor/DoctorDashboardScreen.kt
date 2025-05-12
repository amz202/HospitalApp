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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorDashboardStateScreen(
    doctorId: Long,
    navController: NavHostController,
    doctorViewModel: DoctorViewModel,
    appointmentViewModel: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    // Initialize required data
    LaunchedEffect(doctorId) {
        doctorViewModel.getDoctorDetails(doctorId)
        doctorViewModel.getDoctorPatients(doctorId)
        appointmentViewModel.getDoctorAppointments(doctorId)
    }

    // Observe UI States
    val doctorState = doctorViewModel.doctorDetailsUiState
    val patientsState = doctorViewModel.doctorPatientsUiState
    val appointmentsState = appointmentViewModel.appointmentsUiState

    when {
        doctorState is BaseUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        doctorState is BaseUiState.Success -> {
            DoctorDashboard(
                doctor = doctorState.data,
                patientsState = patientsState,
                appointmentsState = appointmentsState,
                navController = navController
            )
        }
        doctorState is BaseUiState.Error -> {
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
                        text = "Error loading doctor data",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = {
                        doctorViewModel.getDoctorDetails(doctorId)
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboard(
    doctor: DoctorResponse,
    patientsState: BaseUiState<List<PatientResponse>>,
    appointmentsState: BaseUiState<List<AppointmentResponse>>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Patients", "Appointments", "Overview")

    Scaffold(
        topBar = {
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
                },
                actions = {
                    IconButton(onClick = { /* TODO: Add notifications */ }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> PatientsTab(
                    patientsState = patientsState,
                    onPatientClick = { patientId ->
                        navController.navigate(DoctorPatientDetailNav(patientId = patientId))
                    }
                )
                1 -> AppointmentsTab(
                    appointmentsState = appointmentsState,
                    onAppointmentClick = { appointmentId ->
                        navController.navigate(
                            DoctorAppointmentDetailNav(
                                appointmentId = appointmentId,
                                doctorId = doctor.id
                            )
                        )
                    }
                )
                2 -> OverviewTab(
                    doctor = doctor,
                    patientsState = patientsState,
                    appointmentsState = appointmentsState
                )
            }
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
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BaseUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
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
        is BaseUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading patients")
            }
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
                        text = "${patient.fName} ${patient.lName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "DOB: ${patient.dob}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!patient.active) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Inactive patient",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Blood: ${patient.bloodGroup}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "☎ ${patient.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (patient.allergies?.isNotBlank() ?: false) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Allergies: ${patient.allergies}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentsTab(
    appointmentsState: BaseUiState<List<AppointmentResponse>>,
    onAppointmentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDateTime = LocalDateTime.now()
    var selectedFilter by remember { mutableStateOf("Upcoming") }
    val filters = listOf("Upcoming", "Today", "Past", "All")

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = filters.indexOf(selectedFilter),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            filters.forEach { filter ->
                Tab(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    text = { Text(filter) }
                )
            }
        }

        when (appointmentsState) {
            is BaseUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BaseUiState.Success -> {
                val appointments = appointmentsState.data.filter { appointment ->
                    val appointmentDateTime = LocalDateTime.parse(appointment.scheduledTime)
                    when (selectedFilter) {
                        "Upcoming" -> appointmentDateTime.isAfter(currentDateTime)
                        "Today" -> appointmentDateTime.toLocalDate() == currentDateTime.toLocalDate()
                        "Past" -> appointmentDateTime.isBefore(currentDateTime)
                        else -> true
                    }
                }.sortedBy { it.scheduledTime }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = { onAppointmentClick(appointment.id) }
                        )
                    }

                    if (appointments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No appointments found",
                                    style = MaterialTheme.typography.bodyLarge
                                )
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
                    Text("Error loading appointments")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentCard(
    appointment: AppointmentResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
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
                Column {
                    Text(
                        text = "${appointment.patient.fName} ${appointment.patient.lName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = appointment.scheduledTime,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                AppointmentStatusChip(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Type: ${appointment.type.name.replace('_', ' ')}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (appointment.reason.isNotBlank()) {
                Text(
                    text = "Reason: ${appointment.reason}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!appointment.notes.isNullOrBlank()) {
                Text(
                    text = "Notes: ${appointment.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AppointmentStatusChip(
    status: AppointmentStatus,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status) {
        AppointmentStatus.REQUESTED -> MaterialTheme.colorScheme.tertiary to Icons.Default.Schedule
        AppointmentStatus.APPROVED -> MaterialTheme.colorScheme.primary to Icons.Default.CheckCircle
        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.secondary to Icons.Default.Done
        AppointmentStatus.DECLINED -> MaterialTheme.colorScheme.error to Icons.Default.Cancel
        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.error to Icons.Default.Close
    }

    AssistChip(
        onClick = { },
        label = { Text(status.name) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = color) },
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewTab(
    doctor: DoctorResponse,
    patientsState: BaseUiState<List<PatientResponse>>,
    appointmentsState: BaseUiState<List<AppointmentResponse>>,
    modifier: Modifier = Modifier
) {
    val currentDateTime = LocalDateTime.now()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Doctor Info Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Doctor Information",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Specialization: ${doctor.specialization}")
                    Text("License: ${doctor.licenseNumber}")
                    Text("Experience: ${doctor.experienceYears} years")
                    Text("Available for Emergency: ${if (doctor.availableForEmergency) "Yes" else "No"}")
                }
            }
        }

        // Statistics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (patientsState is BaseUiState.Success) {
                    StatCard(
                        title = "Total Patients",
                        value = patientsState.data.size.toString(),
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (appointmentsState is BaseUiState.Success) {
                    val upcomingCount = appointmentsState.data.count {
                        LocalDateTime.parse(it.scheduledTime).isAfter(currentDateTime)
                    }
                    StatCard(
                        title = "Upcoming Appointments",
                        value = upcomingCount.toString(),
                        icon = Icons.Default.EventAvailable,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Activity
        item {
            if (appointmentsState is BaseUiState.Success) {
                val recentAppointments = appointmentsState.data
                    .filter {
                        val appointmentTime = LocalDateTime.parse(it.scheduledTime)
                        appointmentTime.isAfter(currentDateTime.minusDays(7))
                    }
                    .sortedByDescending { it.scheduledTime }
                    .take(5)

                if (recentAppointments.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Recent Activity",
                            style = MaterialTheme.typography.titleMedium
                        )
                        recentAppointments.forEach { appointment ->
                            RecentActivityItem(appointment = appointment)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
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
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RecentActivityItem(
    appointment: AppointmentResponse,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${appointment.patient.fName} ${appointment.patient.lName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = appointment.scheduledTime,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            AppointmentStatusChip(status = appointment.status)
        }
    }
}