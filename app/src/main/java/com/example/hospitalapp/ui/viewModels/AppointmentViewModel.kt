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
import com.example.hospitalapp.data.repositories.AppointmentRepository
import com.example.hospitalapp.network.model.AppointmentRequest
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    var appointmentsUiState: BaseUiState<List<AppointmentResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var appointmentDetailsUiState: BaseUiState<AppointmentResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointments: StateFlow<List<AppointmentResponse>> = _appointments

    private val _selectedAppointment = MutableStateFlow<AppointmentResponse?>(null)
    val selectedAppointment: StateFlow<AppointmentResponse?> = _selectedAppointment

    fun getAppointments() {
        viewModelScope.launch {
            appointmentsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.getAppointments()
                _appointments.value = result
                appointmentsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentsUiState = BaseUiState.Error
            }
        }
    }

    fun getAppointmentById(id: Long) {
        viewModelScope.launch {
            appointmentDetailsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.getAppointmentById(id)
                _selectedAppointment.value = result
                appointmentDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun createAppointment(appointment: AppointmentRequest) {
        viewModelScope.launch {
            appointmentsUiState = BaseUiState.Loading
            try {
                appointmentRepository.createAppointment(appointment)
                getAppointments() // Refresh the list
            } catch (e: Exception) {
                appointmentsUiState = BaseUiState.Error
            }
        }
    }

    fun getPatientAppointments(patientId: Long) {
        viewModelScope.launch {
            appointmentsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.getPatientAppointments(patientId)
                _appointments.value = result
                appointmentsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorAppointments(doctorId: Long) {
        viewModelScope.launch {
            appointmentsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.getDoctorAppointments(doctorId)
                _appointments.value = result
                appointmentsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentsUiState = BaseUiState.Error
            }
        }
    }

    fun getAppointmentsByStatus(status: AppointmentStatus) {
        viewModelScope.launch {
            appointmentsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.getAppointmentsByStatus(status)
                _appointments.value = result
                appointmentsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentsUiState = BaseUiState.Error
            }
        }
    }

    fun updateAppointmentStatus(id: Long, status: AppointmentStatus) {
        viewModelScope.launch {
            appointmentDetailsUiState = BaseUiState.Loading
            try {
                val result = appointmentRepository.updateAppointmentStatus(id, status)
                _selectedAppointment.value = result
                appointmentDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                appointmentDetailsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                AppointmentViewModel(
                    appointmentRepository = application.container.appointmentRepository
                )
            }
        }
    }
}