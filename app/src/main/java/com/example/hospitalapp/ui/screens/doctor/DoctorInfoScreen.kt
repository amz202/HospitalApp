package com.example.hospitalapp.ui.screens.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.DoctorRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorInfoScreen(
    viewModel: DoctorViewModel,
    doctorId: Long,
    onProfileUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var specialization by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }
    var experienceYears by remember { mutableStateOf("") }
    var consultationFee by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var availableForEmergency by remember { mutableStateOf(true) }

    val selectedDoctor by viewModel.selectedDoctor.collectAsState()
    val updateState = viewModel.doctorDetailsUiState

    LaunchedEffect(doctorId) {
        viewModel.getDoctorDetails(doctorId)
    }

    LaunchedEffect(selectedDoctor) {
        selectedDoctor?.let { doctor ->
            specialization = doctor.specialization
            licenseNumber = doctor.licenseNumber
            qualification = doctor.qualification
            experienceYears = doctor.experienceYears.toString()
            consultationFee = doctor.consultationFee.toString()
            phoneNumber = doctor.phoneNumber
            availableForEmergency = doctor.availableForEmergency
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Profile") }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = specialization,
                onValueChange = { specialization = it },
                label = { Text("Specialization") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && specialization.isBlank()
            )

            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("License Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && licenseNumber.isBlank()
            )

            OutlinedTextField(
                value = qualification,
                onValueChange = { qualification = it },
                label = { Text("Qualification") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && qualification.isBlank()
            )

            OutlinedTextField(
                value = experienceYears,
                onValueChange = { if (it.all { char -> char.isDigit() }) experienceYears = it },
                label = { Text("Years of Experience") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && experienceYears.isBlank()
            )

            OutlinedTextField(
                value = consultationFee,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) consultationFee = it },
                label = { Text("Consultation Fee") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && consultationFee.isBlank()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && phoneNumber.isBlank()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                isError = updateState is BaseUiState.Error && address.isBlank()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = availableForEmergency,
                    onCheckedChange = { availableForEmergency = it }
                )
                Text("Available for Emergency")
            }

            Button(
                onClick = {
                    viewModel.updateDoctor(
                        doctorId,
                        DoctorRequest(
                            fName = selectedDoctor?.fName ?: "",
                            lName = selectedDoctor?.lName ?: "",
                            email = selectedDoctor?.email ?: "",
                            phoneNumber = phoneNumber,
                            password = "", // Not needed for update
                            specialization = specialization,
                            licenseNumber = licenseNumber,
                            qualification = qualification,
                            experienceYears = experienceYears.toIntOrNull() ?: 0,
                            consultationFee = consultationFee.toDoubleOrNull() ?: 0.0,
                            availableForEmergency = availableForEmergency
                        )
                    )
                },
                enabled = updateState !is BaseUiState.Loading &&
                        specialization.isNotBlank() &&
                        licenseNumber.isNotBlank() &&
                        qualification.isNotBlank() &&
                        experienceYears.isNotBlank() &&
                        consultationFee.isNotBlank() &&
                        address.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }

            if (updateState is BaseUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            LaunchedEffect(updateState) {
                if (updateState is BaseUiState.Success) {
                    onProfileUpdated()
                }
            }
        }
    }
}