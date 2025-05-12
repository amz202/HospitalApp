package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.network.model.PatientInfoRequest
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    patientId: Long,
    patientViewModel: PatientViewModel,
    onInfoSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bloodGroup by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val updateState = patientViewModel.updatePatientUiState

    LaunchedEffect(updateState) {
        if (updateState is BaseUiState.Success) {
            onInfoSubmitted()
        }
        showError = updateState is BaseUiState.Error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Patient Profile") }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Please provide your medical information",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group") },
                singleLine = true,
                isError = showError,
                enabled = updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = medicalHistory,
                onValueChange = { medicalHistory = it },
                label = { Text("Medical History") },
                minLines = 3,
                maxLines = 5,
                isError = showError,
                enabled = updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact") },
                singleLine = true,
                isError = showError,
                enabled = updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Allergies (if any)") },
                minLines = 2,
                maxLines = 4,
                isError = showError,
                enabled = updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    patientViewModel.updatePatientInfo(
                        patientId,
                        PatientInfoRequest(
                            bloodGroup = bloodGroup,
                            medicalHistory = medicalHistory,
                            emergencyContact = emergencyContact,
                            allergies = allergies
                        )
                    )
                },
                enabled = bloodGroup.isNotBlank() &&
                        medicalHistory.isNotBlank() &&
                        emergencyContact.isNotBlank() &&
                        updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (updateState is BaseUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Information")
                }
            }

            if (showError) {
                Text(
                    text = patientViewModel.errorMessage ?: "Error updating patient information",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}