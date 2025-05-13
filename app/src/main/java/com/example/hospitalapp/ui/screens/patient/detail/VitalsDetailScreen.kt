package com.example.hospitalapp.ui.screens.patient.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.VitalsViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.VitalsResponse
import com.example.hospitalapp.ui.navigation.VitalsChartNav
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsDetailScreen(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vitalsState = vitalsViewModel.patientVitalsUiState
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ALL) }
    val currentDateTime = LocalDateTime.now()


    LaunchedEffect(patientId) {
        vitalsViewModel.getVitalsById(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitals History") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(VitalsChartNav(patientId = patientId))
                    }) {
                        Icon(Icons.Default.BarChart, contentDescription = "View Charts")
                    }
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
            when (vitalsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BaseUiState.Success -> {
                    val vitals = vitalsState.data
                    if (vitals.isEmpty()) {
                        Text(
                            text = "No vitals data available",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val filteredVitals = when (selectedTimeRange) {
                            TimeRange.TODAY -> vitals.filter {
                                val vitalDate = LocalDateTime.parse(it.recordedAt)
                                vitalDate.toLocalDate() == currentDateTime.toLocalDate()
                            }
                            TimeRange.LAST_WEEK -> vitals.filter {
                                val vitalDate = LocalDateTime.parse(it.recordedAt)
                                vitalDate.isAfter(currentDateTime.minusWeeks(1))
                            }
                            TimeRange.LAST_MONTH -> vitals.filter {
                                val vitalDate = LocalDateTime.parse(it.recordedAt)
                                vitalDate.isAfter(currentDateTime.minusMonths(1))
                            }
                            TimeRange.ALL -> vitals
                        }.sortedByDescending { it.recordedAt }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                VitalsSummaryCard(
                                    vitals = filteredVitals.firstOrNull(),
                                    timeRange = selectedTimeRange
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(filteredVitals) { vital ->
                                VitalsDetailCard(vital)
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
                            text = "Error loading vitals",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                vitalsViewModel.getVitalsById(patientId)
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
                title = { Text("Filter Vitals") },
                text = {
                    Column {
                        TimeRange.values().forEach { range ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimeRange == range,
                                    onClick = { selectedTimeRange = range }
                                )
                                Text(
                                    text = range.displayName,
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
private fun VitalsSummaryCard(
    vitals: VitalsResponse?,
    timeRange: TimeRange,
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
                text = when (timeRange) {
                    TimeRange.TODAY -> "Today's Overview"
                    TimeRange.LAST_WEEK -> "Last Week's Overview"
                    TimeRange.LAST_MONTH -> "Last Month's Overview"
                    TimeRange.ALL -> "Latest Vitals"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (vitals != null) {
                Spacer(modifier = Modifier.height(16.dp))
                VitalsGrid(vitals)
            }
        }
    }
}

@Composable
private fun VitalsGrid(
    vitals: VitalsResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItemLarge(
                label = "Heart Rate",
                value = "${vitals.heartRate ?: "--"} bpm",
                isCritical = vitals.critical && vitals.heartRate != null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            VitalItemLarge(
                label = "Blood Pressure",
                value = "${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}",
                isCritical = vitals.critical && (vitals.systolicPressure != null || vitals.diastolicPressure != null),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItemLarge(
                label = "Temperature",
                value = "${vitals.temperature?.let { "%.1f".format(it) } ?: "--"}°C",
                isCritical = vitals.critical && vitals.temperature != null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            VitalItemLarge(
                label = "SpO2",
                value = "${vitals.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%",
                isCritical = vitals.critical && vitals.oxygenSaturation != null,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItemLarge(
                label = "Respiratory Rate",
                value = "${vitals.respiratoryRate ?: "--"}/min",
                isCritical = vitals.critical && vitals.respiratoryRate != null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            VitalItemLarge(
                label = "Blood Sugar",
                value = "${vitals.bloodSugar?.let { "%.1f".format(it) } ?: "--"}",
                isCritical = vitals.critical && vitals.bloodSugar != null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun VitalsDetailCard(
    vitals: VitalsResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (vitals.critical)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalDateTime.parse(vitals.recordedAt)
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                    style = MaterialTheme.typography.titleMedium
                )
                if (vitals.critical) {
                    Text(
                        text = "Critical",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            VitalsGrid(vitals)

            if (vitals.criticalNotes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = vitals.criticalNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (vitals.alertSent) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alert sent to medical staff",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun VitalItemLarge(
    label: String,
    value: String,
    isCritical: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

private enum class TimeRange(val displayName: String) {
    TODAY("Today"),
    LAST_WEEK("Last 7 Days"),
    LAST_MONTH("Last 30 Days"),
    ALL("All Time")
}