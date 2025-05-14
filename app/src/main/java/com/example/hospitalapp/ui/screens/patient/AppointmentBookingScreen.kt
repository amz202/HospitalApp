package com.example.hospitalapp.ui.screens.patient

import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.AppointmentRequest
import com.example.hospitalapp.network.model.AppointmentType
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    patientId: Long,
    doctorId:Long,
    appointmentViewModel: AppointmentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDateTime.now()) }
    var reason by remember { mutableStateOf("") }
    var appointmentType by remember { mutableStateOf(AppointmentType.IN_PERSON) }
    var isBooking by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var meetingLink by remember { mutableStateOf("") }

    val currentDateTime = LocalDateTime.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Selection
            OutlinedCard(
                onClick = { showDatePicker = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Time Selection
            OutlinedCard(
                onClick = { showTimePicker = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected Time",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Appointment Type Selection
            Column {
                Text(
                    text = "Appointment Type",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppointmentType.values().forEach { type ->
                        FilterChip(
                            selected = appointmentType == type,
                            onClick = { appointmentType = type },
                            label = {
                                Text(
                                    text = when (type) {
                                        AppointmentType.IN_PERSON -> "In Person"
                                        AppointmentType.VIDEO_CONSULTATION -> "Video Call"
                                    }
                                )
                            }
                        )
                    }
                }
            }
            if (appointmentType == AppointmentType.VIDEO_CONSULTATION) {
                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter video consultation link") }
                )
            }
            // Reason Input
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for Visit") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Book Button
            Button(
                onClick = {
                    if (reason.isNotBlank()) {
                        isBooking = true
                        val appointment = AppointmentRequest(
                            patientId = patientId,
                            doctorId = doctorId,
                            scheduledTime = selectedDate.format(DateTimeFormatter.ISO_DATE_TIME),
                            type = appointmentType.toString(),
                            reason = reason,
                            virtualMeetingUrl = if (appointmentType == AppointmentType.VIDEO_CONSULTATION) meetingLink else null

                        )
                        appointmentViewModel.createAppointment(appointment)
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBooking && reason.isNotBlank() &&
                        (appointmentType != AppointmentType.VIDEO_CONSULTATION || meetingLink.isNotBlank())            ) {
                if (isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Book Appointment")
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            showTimePicker = true
                        }
                    ) {
                        Text("Next")
                    }
                }
            ) {
                DatePicker(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate.toEpochSecond(ZoneOffset.UTC) * 1000,
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= currentDateTime.toEpochSecond(ZoneOffset.UTC) * 1000
                            }
                        }
                    )
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                onTimeSelected = { hour, minute ->
                    selectedDate = selectedDate.withHour(hour).withMinute(minute)
                    showTimePicker = false
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(state.hour, state.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}