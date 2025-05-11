package com.example.hospitalapp.ui.screens.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.network.model.VitalsResponse
import com.example.hospitalapp.ui.navigation.VitalsDetailNav
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.VitalsViewModel

@Composable
fun VitalsSection(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vitalsState = vitalsViewModel.patientVitalsUiState

    LaunchedEffect(patientId) {
        vitalsViewModel.getVitalsById(patientId)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            navController.navigate(VitalsDetailNav(patientId = patientId))
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
                    text = "Vitals",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = {
                        navController.navigate(VitalsDetailNav(patientId = patientId))
                    }
                ) {
                    Text("View All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (vitalsState) {
                is BaseUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is BaseUiState.Success -> {
                    val vitals = vitalsState.data
                    if (vitals.isEmpty()) {
                        Text(
                            text = "No vitals data available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        val latestVitals = vitals.maxByOrNull { it.recordedAt }
                        if (latestVitals != null) {
                            VitalsSummary(latestVitals)

                            if (vitals.size > 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Recent History",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    vitals
                                        .sortedByDescending { it.recordedAt }
                                        .drop(1)  // Skip the latest one as it's shown in summary
                                        .take(2)  // Show only 2 recent records
                                        .forEach { vital ->
                                            VitalsHistoryItem(vital)
                                        }
                                }
                            }
                        }
                    }
                }
                is BaseUiState.Error -> {
                    Text(
                        text = "Error loading vitals",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun VitalsSummary(
    vitals: VitalsResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (vitals.critical) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Critical Values Detected",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    if (!vitals.criticalNotes.isNullOrBlank()) {
                        Text(
                            text = vitals.criticalNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VitalItem(
                label = "Heart Rate",
                value = "${vitals.heartRate ?: "--"} bpm",
                isCritical = vitals.critical && vitals.heartRate != null,
                modifier = Modifier.weight(1f)
            )
            VitalItem(
                label = "Blood Pressure",
                value = "${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}",
                isCritical = vitals.critical && (vitals.systolicPressure != null || vitals.diastolicPressure != null),
                modifier = Modifier.weight(1f)
            )
            VitalItem(
                label = "Temperature",
                value = "${vitals.temperature?.let { "%.1f".format(it) } ?: "--"}°C",
                isCritical = vitals.critical && vitals.temperature != null,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VitalItem(
                label = "SpO2",
                value = "${vitals.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%",
                isCritical = vitals.critical && vitals.oxygenSaturation != null,
                modifier = Modifier.weight(1f)
            )
            VitalItem(
                label = "Respiratory Rate",
                value = "${vitals.respiratoryRate ?: "--"}/min",
                isCritical = vitals.critical && vitals.respiratoryRate != null,
                modifier = Modifier.weight(1f)
            )
            VitalItem(
                label = "Blood Sugar",
                value = "${vitals.bloodSugar?.let { "%.1f".format(it) } ?: "--"}",
                isCritical = vitals.critical && vitals.bloodSugar != null,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Recorded at: ${vitals.recordedAt}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (vitals.alertSent) {
            Text(
                text = "Alert sent to medical staff",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun VitalsHistoryItem(
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
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vitals.recordedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (vitals.critical)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (vitals.critical) {
                    Text(
                        text = "Critical",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "HR: ${vitals.heartRate ?: "--"} bpm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "BP: ${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "SpO2: ${vitals.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column {
                    Text(
                        text = "Temp: ${vitals.temperature?.let { "%.1f".format(it) } ?: "--"}°C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Resp: ${vitals.respiratoryRate ?: "--"}/min",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Sugar: ${vitals.bloodSugar?.let { "%.1f".format(it) } ?: "--"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (vitals.criticalNotes != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vitals.criticalNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun VitalItem(
    label: String,
    value: String,
    isCritical: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}