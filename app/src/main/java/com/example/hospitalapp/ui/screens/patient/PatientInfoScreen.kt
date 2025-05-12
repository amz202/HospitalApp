package com.example.hospitalapp.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.PatientViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    patientId: Long,
    patientViewModel: PatientViewModel,
    onInfoSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bloodGroup by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val currentDate = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    val updateState = patientViewModel.updatePatientUiState
    val currentPatient by patientViewModel.selectedPatient.collectAsState()

    // Get patient details when screen is first shown
    LaunchedEffect(patientId) {
        patientViewModel.getPatientDetails(patientId)
    }

    // Handle state updates
    LaunchedEffect(updateState) {
        when (updateState) {
            is BaseUiState.Success -> {
                onInfoSubmitted()
            }
            is BaseUiState.Error -> {
                showError = true
            }
            else -> {
                showError = false
            }
        }
    }

    // Clear state when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            patientViewModel.clearUpdateState()
        }
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

            if (currentPatient != null) {
                Text(
                    "Patient: ${currentPatient?.fName} ${currentPatient?.lName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Last Updated: $currentDate",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group") },
                singleLine = true,
                isError = showError && bloodGroup.isBlank(),
                enabled = updateState !is BaseUiState.Loading,
                supportingText = if (showError && bloodGroup.isBlank()) {
                    { Text("Blood group is required") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact") },
                singleLine = true,
                isError = showError && emergencyContact.isBlank(),
                enabled = updateState !is BaseUiState.Loading,
                supportingText = if (showError && emergencyContact.isBlank()) {
                    { Text("Emergency contact is required") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Allergies (if any)") },
                minLines = 2,
                maxLines = 4,
                isError = showError && allergies.isBlank(),
                enabled = updateState !is BaseUiState.Loading,
                supportingText = if (showError && allergies.isBlank()) {
                    { Text("Please specify 'None' if no allergies") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (bloodGroup.isBlank() || emergencyContact.isBlank() || allergies.isBlank()) {
                        showError = true
                        return@Button
                    }
                    patientViewModel.updatePatientAfterSignup(
                        patientId = patientId,
                        bloodGroup = bloodGroup,
                        emergencyContact = emergencyContact,
                        allergies = allergies
                    )
                },
                enabled = updateState !is BaseUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (updateState is BaseUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Profile")
                }
            }

            if (showError) {
                Text(
                    text = patientViewModel.errorMessage ?: "Please fill in all required fields",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}