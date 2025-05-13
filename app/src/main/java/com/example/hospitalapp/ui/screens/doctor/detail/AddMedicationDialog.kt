package com.example.hospitalapp.ui.screens.doctor.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hospitalapp.network.model.MedicationRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    patientId: Long,
    appointmentId: Long?,
    onDismiss: () -> Unit,
    onAddMedication: (MedicationRequest) -> Unit
) {
    // Form states
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    // Date states
    val now = LocalDateTime.now()
    var startDate by remember { mutableStateOf(now) }
    var endDateEnabled by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf(now.plusDays(14)) } // Default 2 weeks


    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add New Medication",
                    style = MaterialTheme.typography.titleLarge
                )

                // Medication name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank()
                )

                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g., 10mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = dosage.isBlank()
                )

                // Frequency
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency (e.g., 3 times daily)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = frequency.isBlank()
                )

                // Start date field
                // In a full implementation, use a DatePicker
                Text(
                    text = "Start Date: ${startDate.format(formatter)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // End date toggle and field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = endDateEnabled,
                        onCheckedChange = { endDateEnabled = it }
                    )
                    Text("Set End Date")
                }

                // Only show end date if enabled
                if (endDateEnabled) {
                    // In a full implementation, use a DatePicker
                    Text(
                        text = "End Date: ${endDate.format(formatter)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Special Instructions (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Create medication request and pass to callback
                            val request = MedicationRequest(
                                patientId = patientId,
                                appointmentId = appointmentId ?: 0, // Use 0 as default if no appointment
                                name = name,
                                dosage = dosage,
                                frequency = frequency,
                                startDate = startDate.format(formatter),
                                endDate = if (endDateEnabled) endDate.format(formatter) else null,
                                instructions = if (instructions.isNotBlank()) instructions else null
                            )
                            onAddMedication(request)
                        },
                        enabled = name.isNotBlank() && dosage.isNotBlank() && frequency.isNotBlank()
                    ) {
                        Text("Add Medication")
                    }
                }
            }
        }
    }
}