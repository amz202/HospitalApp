package com.example.hospitalapp.ui.screens.patient.chart

import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.hospitalapp.ui.viewModels.VitalsViewModel
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.network.model.VitalsResponse
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class TimeRange(val displayName: String, val days: Long) {
    LAST_DAY("24 Hours", 1),
    LAST_WEEK("7 Days", 7),
    LAST_MONTH("30 Days", 30),
    LAST_YEAR("1 Year", 365)
}

enum class VitalType(val displayName: String, val valueSelector: (VitalsResponse) -> Double?) {
    HEART_RATE("Heart Rate", { it.heartRate?.toDouble() }),
    TEMPERATURE("Temperature", { it.temperature }),
    SYSTOLIC_PRESSURE("Systolic BP", { it.systolicPressure?.toDouble() }),
    DIASTOLIC_PRESSURE("Diastolic BP", { it.diastolicPressure?.toDouble() }),
    OXYGEN_SATURATION("Oxygen Saturation", { it.oxygenSaturation }),
    RESPIRATORY_RATE("Respiratory Rate", { it.respiratoryRate?.toDouble() }),
    BLOOD_SUGAR("Blood Sugar", { it.bloodSugar })
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsChartScreen(
    patientId: Long,
    vitalsViewModel: VitalsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val vitalsState = vitalsViewModel.patientVitalsUiState
    var selectedTimeRange by remember { mutableStateOf(TimeRange.LAST_WEEK) }
    var selectedVitalType by remember { mutableStateOf(VitalType.HEART_RATE) }
    var showTimeRangeMenu by remember { mutableStateOf(false) }
    var showVitalTypeMenu by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        vitalsViewModel.getVitalsByPatient(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitals Chart") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chart controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vital Type Selector
                Box {
                    OutlinedButton(
                        onClick = { showVitalTypeMenu = true },
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(selectedVitalType.displayName)
                    }
                    DropdownMenu(
                        expanded = showVitalTypeMenu,
                        onDismissRequest = { showVitalTypeMenu = false }
                    ) {
                        VitalType.values().forEach { vitalType ->
                            DropdownMenuItem(
                                text = { Text(vitalType.displayName) },
                                onClick = {
                                    selectedVitalType = vitalType
                                    showVitalTypeMenu = false
                                }
                            )
                        }
                    }
                }

                // Time Range Selector
                Box {
                    OutlinedButton(
                        onClick = { showTimeRangeMenu = true }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedTimeRange.displayName)
                    }
                    DropdownMenu(
                        expanded = showTimeRangeMenu,
                        onDismissRequest = { showTimeRangeMenu = false }
                    ) {
                        TimeRange.values().forEach { timeRange ->
                            DropdownMenuItem(
                                text = { Text(timeRange.displayName) },
                                onClick = {
                                    selectedTimeRange = timeRange
                                    showTimeRangeMenu = false
                                }
                            )
                        }
                    }
                }
            }

            when (vitalsState) {
                is BaseUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BaseUiState.Success -> {
                    val vitals = vitalsState.data
                    if (vitals.isEmpty()) {
                        NoDataDisplay()
                    } else {
                        VitalsChart(
                            vitals = vitals,
                            vitalType = selectedVitalType,
                            timeRange = selectedTimeRange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        VitalStatsCard(
                            vitals = vitals,
                            vitalType = selectedVitalType,
                            timeRange = selectedTimeRange
                        )
                    }
                }
                is BaseUiState.Error -> {
                    ErrorDisplay()
                }
                else -> Unit
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VitalsChart(
    vitals: List<VitalsResponse>,
    vitalType: VitalType,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()
    val cutoffDate = now.minusDays(timeRange.days)

    // Filter and sort vitals data
    val filteredVitals = vitals.filter {
        val recordDate = LocalDateTime.parse(it.recordedAt)
        recordDate.isAfter(cutoffDate) && vitalType.valueSelector(it) != null
    }.sortedBy { it.recordedAt }

    if (filteredVitals.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No ${vitalType.displayName} data available for the selected time range",
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val xLabels = filteredVitals.map {
        LocalDateTime.parse(it.recordedAt).format(DateTimeFormatter.ofPattern("MM/dd"))
    }

    val chartColor = when (vitalType) {
        VitalType.HEART_RATE -> Color(0xFFE57373) // Red for heart rate
        VitalType.TEMPERATURE -> Color(0xFFFFB74D) // Orange for temperature
        VitalType.SYSTOLIC_PRESSURE, VitalType.DIASTOLIC_PRESSURE -> Color(0xFF64B5F6) // Blue for BP
        VitalType.OXYGEN_SATURATION -> Color(0xFF81C784) // Green for oxygen
        VitalType.RESPIRATORY_RATE -> Color(0xFF9575CD) // Purple for resp rate
        VitalType.BLOOD_SUGAR -> Color(0xFFF06292) // Pink for blood sugar
    }.toArgb()

    val chartEntries = filteredVitals.mapIndexedNotNull { index, vitalsResponse ->
        val value = vitalType.valueSelector(vitalsResponse) ?: return@mapIndexedNotNull null
        Entry(index.toFloat(), value.toFloat())
    }

    val materialThemeTextColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val materialThemeGridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f).toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                isScaleXEnabled = true
                isScaleYEnabled = false
                setPinchZoom(false)
                setDrawGridBackground(false)

                // X-Axis setup
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(true)
                    gridColor = materialThemeGridColor
                    textColor = materialThemeTextColor
                    valueFormatter = IndexAxisValueFormatter(xLabels)
                    setLabelCount(4, true)
                }

                // Left Y-Axis setup
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = materialThemeGridColor
                    textColor = materialThemeTextColor
                }

                // Right Y-Axis setup
                axisRight.apply {
                    isEnabled = false
                }

                // Legend setup
                legend.apply {
                    textColor = materialThemeTextColor
                }
            }
        },
        update = { chart ->
            // Create dataset
            val dataSet = LineDataSet(chartEntries, vitalType.displayName).apply {
                color = chartColor
                lineWidth = 2f
                setDrawCircles(true)
                setCircleColor(chartColor)
                circleRadius = 4f
                setDrawCircleHole(true)
                circleHoleRadius = 2f
                setDrawValues(false)
                mode = LineDataSet.Mode.LINEAR

                // Customize appearance based on vital type
                when (vitalType) {
                    VitalType.HEART_RATE -> {
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.2f
                    }
                    else -> {
                        mode = LineDataSet.Mode.LINEAR
                    }
                }
            }

            // Update chart data
            val lineData = LineData(dataSet)
            chart.data = lineData

            // Refresh the chart
            chart.invalidate()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VitalStatsCard(
    vitals: List<VitalsResponse>,
    vitalType: VitalType,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()
    val cutoffDate = now.minusDays(timeRange.days)

    // Filter and prepare data
    val filteredVitals = vitals.filter {
        val recordDate = LocalDateTime.parse(it.recordedAt)
        recordDate.isAfter(cutoffDate) && vitalType.valueSelector(it) != null
    }

    val values = filteredVitals.mapNotNull { vitalType.valueSelector(it) }

    if (values.isEmpty()) return

    // Calculate stats
    val latestValue = values.last()
    val average = values.average()
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: 0.0

    // Determine unit suffix
    val unitSuffix = when (vitalType) {
        VitalType.HEART_RATE -> " BPM"
        VitalType.TEMPERATURE -> " °C"
        VitalType.SYSTOLIC_PRESSURE, VitalType.DIASTOLIC_PRESSURE -> " mmHg"
        VitalType.OXYGEN_SATURATION -> " %"
        VitalType.RESPIRATORY_RATE -> " breaths/min"
        VitalType.BLOOD_SUGAR -> " mg/dL"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${vitalType.displayName} Statistics",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem("Latest", "${String.format("%.1f", latestValue)}$unitSuffix")
                StatItem("Average", "${String.format("%.1f", average)}$unitSuffix")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem("Min", "${String.format("%.1f", min)}$unitSuffix")
                StatItem("Max", "${String.format("%.1f", max)}$unitSuffix")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Based on ${values.size} measurements over ${timeRange.displayName.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun NoDataDisplay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No vitals data available",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Record some vitals to see them graphed over time",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorDisplay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading vitals data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { /* Retry loading logic */ }) {
                Text("Retry")
            }
        }
    }
}