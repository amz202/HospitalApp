package com.example.hospitalapp.ui.screens.doctor.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hospitalapp.network.model.VitalsRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordVitalsDialog(
    patientId: Long,
    onDismiss: () -> Unit,
    onSaveVitals: (VitalsRequest) -> Unit
) {
    var heartRate by remember { mutableStateOf("") }
    var systolicPressure by remember { mutableStateOf("") }
    var diastolicPressure by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var oxygenSaturation by remember { mutableStateOf("") }
    var respiratoryRate by remember { mutableStateOf("") }
    var bloodSugar by remember { mutableStateOf("") }

    // Form validation
    val heartRateError = remember(heartRate) {
        heartRate.isNotEmpty() && (heartRate.toIntOrNull() == null || heartRate.toInt() !in 20..250)
    }

    val systolicError = remember(systolicPressure) {
        systolicPressure.isNotEmpty() && (systolicPressure.toIntOrNull() == null || systolicPressure.toInt() !in 50..250)
    }

    val diastolicError = remember(diastolicPressure) {
        diastolicPressure.isNotEmpty() && (diastolicPressure.toIntOrNull() == null || diastolicPressure.toInt() !in 30..150)
    }

    val tempError = remember(temperature) {
        temperature.isNotEmpty() && (temperature.toDoubleOrNull() == null || temperature.toDouble() !in 30.0..45.0)
    }

    val oxygenError = remember(oxygenSaturation) {
        oxygenSaturation.isNotEmpty() && (oxygenSaturation.toDoubleOrNull() == null || oxygenSaturation.toDouble() !in 50.0..100.0)
    }

    val respRateError = remember(respiratoryRate) {
        respiratoryRate.isNotEmpty() && (respiratoryRate.toIntOrNull() == null || respiratoryRate.toInt() !in 5..60)
    }

    val bloodSugarError = remember(bloodSugar) {
        bloodSugar.isNotEmpty() && (bloodSugar.toDoubleOrNull() == null || bloodSugar.toDouble() !in 30.0..500.0)
    }

    // Check if form is valid
    val isFormValid = remember(
        heartRate, systolicPressure, diastolicPressure, temperature,
        oxygenSaturation, respiratoryRate, bloodSugar,
        heartRateError, systolicError, diastolicError, tempError,
        oxygenError, respRateError, bloodSugarError
    ) {
        // At least one field must be filled and all filled fields must be valid
        (heartRate.isNotEmpty() || systolicPressure.isNotEmpty() || diastolicPressure.isNotEmpty() ||
                temperature.isNotEmpty() || oxygenSaturation.isNotEmpty() || respiratoryRate.isNotEmpty() ||
                bloodSugar.isNotEmpty()) &&
                !heartRateError && !systolicError && !diastolicError && !tempError &&
                !oxygenError && !respRateError && !bloodSugarError
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Record Patient Vitals",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Heart Rate Field
                OutlinedTextField(
                    value = heartRate,
                    onValueChange = { heartRate = it },
                    label = { Text("Heart Rate (BPM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = heartRateError,
                    supportingText = {
                        if (heartRateError) {
                            Text("Enter a valid heart rate between 20-250 BPM")
                        }
                    },
                    singleLine = true
                )

                // Blood Pressure Fields (side by side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = systolicPressure,
                        onValueChange = { systolicPressure = it },
                        label = { Text("Systolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        isError = systolicError,
                        supportingText = {
                            if (systolicError) {
                                Text("Valid: 50-250")
                            }
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = diastolicPressure,
                        onValueChange = { diastolicPressure = it },
                        label = { Text("Diastolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        isError = diastolicError,
                        supportingText = {
                            if (diastolicError) {
                                Text("Valid: 30-150")
                            }
                        },
                        singleLine = true
                    )
                }

                // Temperature Field
                OutlinedTextField(
                    value = temperature,
                    onValueChange = { temperature = it },
                    label = { Text("Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = tempError,
                    supportingText = {
                        if (tempError) {
                            Text("Enter a valid temperature between 30-45°C")
                        }
                    },
                    singleLine = true
                )

                // Oxygen Saturation Field
                OutlinedTextField(
                    value = oxygenSaturation,
                    onValueChange = { oxygenSaturation = it },
                    label = { Text("Oxygen Saturation (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = oxygenError,
                    supportingText = {
                        if (oxygenError) {
                            Text("Enter a valid saturation between 50-100%")
                        }
                    },
                    singleLine = true
                )

                // Respiratory Rate Field
                OutlinedTextField(
                    value = respiratoryRate,
                    onValueChange = { respiratoryRate = it },
                    label = { Text("Respiratory Rate (breaths/min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = respRateError,
                    supportingText = {
                        if (respRateError) {
                            Text("Enter a valid rate between 5-60")
                        }
                    },
                    singleLine = true
                )

                // Blood Sugar Field
                OutlinedTextField(
                    value = bloodSugar,
                    onValueChange = { bloodSugar = it },
                    label = { Text("Blood Sugar (mg/dL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = bloodSugarError,
                    supportingText = {
                        if (bloodSugarError) {
                            Text("Enter a valid blood sugar between 30-500 mg/dL")
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val vitalsRequest = VitalsRequest(
                                patientId = patientId,
                                heartRate = heartRate.toIntOrNull(),
                                systolicPressure = systolicPressure.toIntOrNull(),
                                diastolicPressure = diastolicPressure.toIntOrNull(),
                                temperature = temperature.toDoubleOrNull(),
                                oxygenSaturation = oxygenSaturation.toDoubleOrNull(),
                                respiratoryRate = respiratoryRate.toIntOrNull(),
                                bloodSugar = bloodSugar.toDoubleOrNull()
                            )
                            onSaveVitals(vitalsRequest)
                        },
                        enabled = isFormValid
                    ) {
                        Text("Save Vitals")
                    }
                }
            }
        }
    }
}