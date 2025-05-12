package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.PatientMedicalInfoRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    viewModel: PatientViewModel,
    patientId: Long,
    onProfileUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bloodGroup by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val updateState = viewModel.updatePatientUiState
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(patientId) {
        viewModel.getPatientDetails(patientId)
    }

    LaunchedEffect(selectedPatient) {
        selectedPatient?.let { patient ->
            bloodGroup = patient.bloodGroup ?: ""
            emergencyContact = patient.emergencyContact ?: ""
            allergies = patient.allergies ?: ""
            phoneNumber = patient.phoneNumber ?: ""
            address = patient.address
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
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && bloodGroup.isBlank()
            )

            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact") },
                modifier = Modifier.fillMaxWidth(),
                isError = updateState is BaseUiState.Error && emergencyContact.isBlank()
            )

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Allergies") },
                modifier = Modifier.fillMaxWidth()
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

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    viewModel.updatePatientMedicalInfo(
                        patientId,
                        PatientMedicalInfoRequest(
                            bloodGroup = bloodGroup.takeIf { it.isNotBlank() }.toString(),
                            emergencyContact = emergencyContact.takeIf { it.isNotBlank() }.toString(),
                            allergies = allergies.takeIf { it.isNotBlank() }.toString(),
                            phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                            address = address
                        )
                    )
                },
                enabled = updateState !is BaseUiState.Loading,
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