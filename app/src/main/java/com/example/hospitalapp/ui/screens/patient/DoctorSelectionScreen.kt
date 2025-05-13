package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.DoctorResponse
import com.example.hospitalapp.ui.navigation.AppointmentBookingScreenNav
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorSelectionScreen(
    patientId: Long,
    doctorViewModel: DoctorViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // State to track search query
    var searchQuery by remember { mutableStateOf("") }

    // Get all doctors
    LaunchedEffect(Unit) {
        doctorViewModel.getDoctors()
    }

    val doctorsState = doctorViewModel.doctorsUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Doctor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Simple search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search doctors") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            when (doctorsState) {
                is BaseUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BaseUiState.Success -> {
                    val doctors = doctorsState.data

                    // Simple name-based filtering
                    val filteredDoctors = doctors.filter { doctor ->
                        val fullName = "${doctor.fName} ${doctor.lName}"
                        fullName.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredDoctors.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No doctors found")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredDoctors) { doctor ->
                                SimpleDoctorCard(
                                    doctor = doctor,
                                    onClick = {
                                        // Navigate to appointment booking
                                        navController.navigate(
                                            AppointmentBookingScreenNav(
                                                patientId = patientId,
                                                doctorId = doctor.id
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { doctorViewModel.getDoctors() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleDoctorCard(
    doctor: DoctorResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simple text showing doctor name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dr. ${doctor.fName} ${doctor.lName}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Arrow indicator
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}