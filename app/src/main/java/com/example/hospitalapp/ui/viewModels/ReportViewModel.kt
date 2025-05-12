package com.example.hospitalapp.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hospitalapp.HospitalApplication
import com.example.hospitalapp.data.repositories.ReportRepository
import com.example.hospitalapp.network.model.ReportRequest
import com.example.hospitalapp.network.model.ReportResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    var reportsUiState: BaseUiState<List<ReportResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var reportDetailsUiState: BaseUiState<ReportResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientReportsUiState: BaseUiState<List<ReportResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _selectedReport = MutableStateFlow<ReportResponse?>(null)
    val selectedReport: StateFlow<ReportResponse?> = _selectedReport

//    fun generateReport(
//        appointmentId: Long,
//        patientId: Long,
//        doctorId: Long,
//        title: String,
//        summary: String,
//        reportType: String,
//        vitalsId: Long? = null,
//        medicationIds: List<Long> = emptyList(),
//        feedbackId: Long? = null,
//        timePeriodStart: String? = null,
//        timePeriodEnd: String? = null
//    ) {
//        viewModelScope.launch {
//            reportDetailsUiState = BaseUiState.Loading
//            try {
//                val reportRequest = ReportRequest(
//                    title = title,
//                    patientId = patientId,
//                    doctorId = doctorId,
//                    summary = summary,
//                    reportType = reportType,
//                    appointmentId = appointmentId,
//                    vitalsId = vitalsId,
//                    medicationIds = medicationIds,
//                    feedbackId = feedbackId,
//                    timePeriodStart = timePeriodStart,
//                    timePeriodEnd = timePeriodEnd
//                )
//
//                // First create the report and get its ID
//                val reportId = reportRepository.createReport(reportRequest)
//
//                // Then fetch the full report response using the ID
//                val reportResponse = reportRepository.getReportById(reportId)
//
//                // Update the states with the full report response
//                _selectedReport.value = reportResponse
//                reportDetailsUiState = BaseUiState.Success(reportResponse)
//            } catch (e: Exception) {
//                reportDetailsUiState = BaseUiState.Error
//            }
//        }
//    }

//    fun getReportById(id: Long) {
//        viewModelScope.launch {
//            reportDetailsUiState = BaseUiState.Loading
//            try {
//                val result = reportRepository.getReportById(id)
//                _selectedReport.value = result
//                reportDetailsUiState = BaseUiState.Success(result)
//            } catch (e: Exception) {
//                reportDetailsUiState = BaseUiState.Error
//            }
//        }
//    }

    fun getPatientReports(patientId: Long) {
        viewModelScope.launch {
            patientReportsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.getPatientReports(patientId)
                patientReportsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientReportsUiState = BaseUiState.Error
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                ReportViewModel(
                    reportRepository = application.container.reportRepository
                )
            }
        }
    }
}