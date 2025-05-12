package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.PatientMedicalInfoRequest
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientUpdateRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = emergencyContact,
            onValueChange = { emergencyContact = it },
            label = { Text("Emergency Contact") },
            modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                selectedPatient?.let { currentPatient ->
                    viewModel.updatePatient(
                        patientId,
                        PatientRequest(
                            fName = currentPatient.fName,
                            lName = currentPatient.lName,
                            email = currentPatient.email,
                            phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                            password = "",  // Not needed for update
                            bloodGroup = bloodGroup.takeIf { it.isNotBlank() },
                            emergencyContact = emergencyContact.takeIf { it.isNotBlank() },
                            allergies = allergies.takeIf { it.isNotBlank() },
                            primaryDoctorId = null,  // Since it's not in PatientResponse, we'll set it to null
                            gender = currentPatient.gender,
                            dob = currentPatient.dob,
                            address = address
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        if (updateState is BaseUiState.Success) {
            LaunchedEffect(Unit) {
                onProfileUpdated()
            }
        }
    }
}