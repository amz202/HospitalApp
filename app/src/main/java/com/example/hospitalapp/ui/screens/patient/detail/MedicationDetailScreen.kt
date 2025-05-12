package com.example.hospitalapp.ui.screens.patient.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.MedicationViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.MedicationResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationDetailScreen(
    patientId: Long,
    medicationViewModel: MedicationViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(MedicationFilter.ACTIVE) }
    val medicationsState = medicationViewModel.patientMedicationsUiState
    val currentDateTime = LocalDateTime.parse("2025-05-12 17:47:49",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                MedicationFilter.entries.forEach { filter ->
                    SegmentedButton(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        shape = when (filter) {
                            MedicationFilter.ACTIVE -> SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 3
                            )
                            MedicationFilter.INACTIVE -> SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 3
                            )
                            MedicationFilter.ALL -> SegmentedButtonDefaults.itemShape(
                                index = 2,
                                count = 3
                            )
                        }
                    ) {
                        Text(
                            text = when (filter) {
                                MedicationFilter.ACTIVE -> "Active"
                                MedicationFilter.INACTIVE -> "Inactive"
                                MedicationFilter.ALL -> "All"
                            }
                        )
                    }
                }
            }

            when (medicationsState) {
                is BaseUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BaseUiState.Success -> {
                    val medications = medicationsState.data
                    val filteredMedications = when (selectedFilter) {
                        MedicationFilter.ACTIVE -> medications.filter { med ->
                            med.active && med.endDate?.let { endDate ->
                                LocalDateTime.parse(endDate).isAfter(currentDateTime)
                            } ?: true
                        }
                        MedicationFilter.INACTIVE -> medications.filter { med ->
                            !med.active || med.endDate?.let { endDate ->
                                LocalDateTime.parse(endDate).isBefore(currentDateTime)
                            } ?: false
                        }
                        MedicationFilter.ALL -> medications
                    }.sortedWith(
                        compareByDescending<MedicationResponse> { it.active }
                            .thenByDescending { it.createdAt }
                    )

                    if (filteredMedications.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Medication,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = when (selectedFilter) {
                                        MedicationFilter.ACTIVE -> "No active medications"
                                        MedicationFilter.INACTIVE -> "No inactive medications"
                                        MedicationFilter.ALL -> "No medications found"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = filteredMedications,
                                key = { it.id }
                            ) { medication ->
                                MedicationCard(
                                    medication = medication,
                                    currentDateTime = currentDateTime,
                                    onClick = {
                                        navController.navigate("medication_details/${medication.id}")
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error loading medications",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = {
                                    medicationViewModel.getPatientMedications(patientId)
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MedicationCard(
    medication: MedicationResponse,
    currentDateTime: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = medication.dosage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            if (medication.active) "Active" else "Inactive"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (medication.active)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Frequency: ${medication.frequency}",
                style = MaterialTheme.typography.bodyMedium
            )
            medication.instructions?.let { instructions ->
                Text(
                    text = "Instructions: $instructions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Started: ${
                        LocalDateTime.parse(medication.startDate)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                medication.endDate?.let { endDate ->
                    Text(
                        text = "Ends: ${
                            LocalDateTime.parse(endDate)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

enum class MedicationFilter {
    ACTIVE, INACTIVE, ALL
}