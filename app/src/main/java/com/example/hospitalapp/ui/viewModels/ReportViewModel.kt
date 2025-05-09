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

    private val _reports = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _selectedReport = MutableStateFlow<ReportResponse?>(null)
    val selectedReport: StateFlow<ReportResponse?> = _selectedReport

    fun getAllReports() {
        viewModelScope.launch {
            reportsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.getAllReports()
                _reports.value = result
                reportsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                reportsUiState = BaseUiState.Error
            }
        }
    }

    fun getReportById(id: Long) {
        viewModelScope.launch {
            reportDetailsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.getReportById(id)
                _selectedReport.value = result
                reportDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                reportDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getReportsByPatient(patientId: Long) {
        viewModelScope.launch {
            patientReportsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.getReportsByPatient(patientId)
                patientReportsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientReportsUiState = BaseUiState.Error
            }
        }
    }

    fun generateReport(appointmentId: Long) {
        viewModelScope.launch {
            reportDetailsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.generateReport(appointmentId)
                _selectedReport.value = result
                reportDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                reportDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun updateReport(id: Long, report: ReportRequest) {
        viewModelScope.launch {
            reportDetailsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.updateReport(id, report)
                _selectedReport.value = result
                reportDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                reportDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getReportsByPatientBetweenDates(patientId: Long, startDate: String, endDate: String) {
        viewModelScope.launch {
            patientReportsUiState = BaseUiState.Loading
            try {
                val result = reportRepository.getReportsByPatientBetweenDates(patientId, startDate, endDate)
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