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
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Information") }
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

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.updatePatientMedicalInfo(
                        patientId,
                        PatientMedicalInfoRequest(
                            bloodGroup = bloodGroup,
                            emergencyContact = emergencyContact,
                            allergies = allergies,
                            primaryDoctorId = null
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}