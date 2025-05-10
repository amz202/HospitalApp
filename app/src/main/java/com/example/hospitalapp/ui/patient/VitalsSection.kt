package com.example.hospitalapp.ui.patient

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.ui.viewModels.VitalsViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.VitalsRequest
import com.example.hospitalapp.network.model.VitalsResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VitalsSection(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    onNavigateToVitalsDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCsvError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val vitalsState = vitalsViewModel.patientVitalsUiState
    val currentDateTime = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    // File picker for CSV upload
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val headerLine = reader.readLine()
                    val headers = headerLine.split(",")

                    val expectedHeaders = listOf(
                        "heartRate",
                        "systolicPressure",
                        "diastolicPressure",
                        "temperature",
                        "oxygenSaturation",
                        "respiratoryRate",
                        "bloodSugar"
                    )

                    if (!headers.containsAll(expectedHeaders)) {
                        errorMessage = "Invalid CSV format. Expected headers: ${expectedHeaders.joinToString(", ")}"
                        showCsvError = true
                        return@let
                    }

                    reader.lineSequence()
                        .map { line ->
                            try {
                                val values = line.split(",")
                                val headerToValue = headers.zip(values).toMap()

                                VitalsRequest(
                                    patientId = patientId,
                                    heartRate = headerToValue["heartRate"]?.toIntOrNull(),
                                    systolicPressure = headerToValue["systolicPressure"]?.toIntOrNull(),
                                    diastolicPressure = headerToValue["diastolicPressure"]?.toIntOrNull(),
                                    temperature = headerToValue["temperature"]?.toDoubleOrNull(),
                                    oxygenSaturation = headerToValue["oxygenSaturation"]?.toDoubleOrNull(),
                                    respiratoryRate = headerToValue["respiratoryRate"]?.toIntOrNull(),
                                    bloodSugar = headerToValue["bloodSugar"]?.toDoubleOrNull()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        .filterNotNull()
                        .forEach { vitalsRequest ->
                            vitalsViewModel.createVitals(vitalsRequest)
                        }
                }
            } catch (e: Exception) {
                errorMessage = "Error processing CSV file: ${e.localizedMessage}"
                showCsvError = true
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onNavigateToVitalsDetail
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
                IconButton(
                    onClick = { csvLauncher.launch("text/csv") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload Vitals"
                    )
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
                        VitalsList(vitals = vitals)
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

    if (showCsvError) {
        AlertDialog(
            onDismissRequest = { showCsvError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showCsvError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun VitalsList(
    vitals: List<VitalsResponse>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Latest Vitals Summary
        val latestVitals = vitals.last()
        VitalsSummary(latestVitals)

        // Vitals History
        Text(
            text = "Recent History",
            style = MaterialTheme.typography.titleMedium
        )

        vitals.takeLast(5).reversed().forEach { vital ->
            VitalHistoryItem(vital)
        }
    }
}

@Composable
private fun VitalHistoryItem(
    vital: VitalsResponse,
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HR: ${vital.heartRate ?: "--"} bpm",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "BP: ${vital.systolicPressure ?: "--"}/${vital.diastolicPressure ?: "--"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "O2: ${vital.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Temp: ${vital.temperature?.let { "%.1f".format(it) } ?: "--"}°C",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Resp: ${vital.respiratoryRate ?: "--"}/min",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Sugar: ${vital.bloodSugar?.let { "%.1f".format(it) } ?: "--"} mg/dL",
                    style = MaterialTheme.typography.bodyMedium
                )
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItem(
                label = "Heart Rate",
                value = "${vitals.heartRate ?: "--"} bpm"
            )
            VitalItem(
                label = "Blood Pressure",
                value = "${vitals.systolicPressure ?: "--"}/${vitals.diastolicPressure ?: "--"}"
            )
            VitalItem(
                label = "O2 Sat",
                value = "${vitals.oxygenSaturation?.let { "%.1f".format(it) } ?: "--"}%"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VitalItem(
                label = "Temperature",
                value = "${vitals.temperature?.let { "%.1f".format(it) } ?: "--"}°C"
            )
            VitalItem(
                label = "Resp Rate",
                value = "${vitals.respiratoryRate ?: "--"}/min"
            )
            VitalItem(
                label = "Blood Sugar",
                value = "${vitals.bloodSugar?.let { "%.1f".format(it) } ?: "--"}"
            )
        }
    }
}

@Composable
private fun VitalItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}