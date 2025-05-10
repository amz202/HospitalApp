package com.example.hospitalapp.ui.patient


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.MedicationViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.MedicationResponse
import com.example.hospitalapp.ui.navigation.MedicationDetailNav

@Composable
fun MedicationsSection(
    patientId: Long,
    medicationViewModel: MedicationViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val medicationsState = medicationViewModel.patientMedicationsUiState

    LaunchedEffect(patientId) {
        medicationViewModel.getActiveMedications(patientId)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            navController.navigate(MedicationDetailNav(patientId = patientId))
        }
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
                Text(
                    text = "Current Medications",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = {
                        navController.navigate(MedicationDetailNav(patientId = patientId))
                    }
                ) {
                    Text("View All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (medicationsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is BaseUiState.Success -> {
                    val medications = medicationsState.data
                    if (medications.isEmpty()) {
                        Text(
                            text = "No active medications",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            medications
                                .filter { it.active }
                                .take(3)
                                .forEach { medication ->
                                    MedicationItem(medication)
                                }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Text(
                        text = "Error loading medications",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun MedicationItem(
    medication: MedicationResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${medication.dosage} - ${medication.frequency}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = if (medication.active) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (medication.active)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            if (!medication.instructions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medication.instructions,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dr. ${medication.doctor.firstName} ${medication.doctor.lastName}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}