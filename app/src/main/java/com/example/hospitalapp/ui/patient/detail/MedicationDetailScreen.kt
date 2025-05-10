package com.example.hospitalapp.ui.patient.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.MedicationViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.MedicationResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailScreen(
    patientId: Long,
    medicationViewModel: MedicationViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val medicationsState = medicationViewModel.patientMedicationsUiState
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(MedicationFilter.ALL) }
    val currentDateTime = remember {
        LocalDateTime.parse("2025-05-10 08:43:26", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    LaunchedEffect(patientId) {
        medicationViewModel.getPatientMedications(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (medicationsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BaseUiState.Success -> {
                    val medications = medicationsState.data
                    if (medications.isEmpty()) {
                        Text(
                            text = "No medications found",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val filteredMedications = when (selectedFilter) {
                            MedicationFilter.ACTIVE -> medications.filter { it.active }
                            MedicationFilter.INACTIVE -> medications.filter { !it.active }
                            MedicationFilter.ALL -> medications
                        }.sortedByDescending { it.prescribedAt }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                MedicationSummaryCard(
                                    medications = filteredMedications,
                                    filter = selectedFilter
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(filteredMedications) { medication ->
                                MedicationDetailCard(medication)
                            }

                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                else -> Unit
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filter Medications") },
                text = {
                    Column {
                        MedicationFilter.values().forEach { filter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter }
                                )
                                Text(
                                    text = filter.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
private fun MedicationSummaryCard(
    medications: List<MedicationResponse>,
    filter: MedicationFilter,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = when (filter) {
                    MedicationFilter.ACTIVE -> "Active Medications"
                    MedicationFilter.INACTIVE -> "Past Medications"
                    MedicationFilter.ALL -> "All Medications"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${medications.size} medication(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MedicationDetailCard(
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
                .padding(16.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dr. ${medication.doctor.firstName} ${medication.doctor.lastName}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Prescribed: ${
                    LocalDateTime.parse(medication.prescribedAt)
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!medication.instructions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medication.instructions,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start: ${
                        LocalDateTime.parse(medication.startDate)
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "End: ${
                        LocalDateTime.parse(medication.endDate)
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!medication.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medication.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private enum class MedicationFilter(val displayName: String) {
    ACTIVE("Active Medications"),
    INACTIVE("Past Medications"),
    ALL("All Medications")
}