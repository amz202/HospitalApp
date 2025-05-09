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
import com.example.hospitalapp.data.repositories.DoctorRepository
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    var doctorsUiState: BaseUiState<List<DoctorResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var doctorDetailsUiState: BaseUiState<DoctorResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var doctorPatientsUiState: BaseUiState<List<PatientResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var doctorAppointmentsUiState: BaseUiState<List<AppointmentResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _doctors = MutableStateFlow<List<DoctorResponse>>(emptyList())
    val doctors: StateFlow<List<DoctorResponse>> = _doctors

    private val _selectedDoctor = MutableStateFlow<DoctorResponse?>(null)
    val selectedDoctor: StateFlow<DoctorResponse?> = _selectedDoctor

    fun getDoctors() {
        viewModelScope.launch {
            doctorsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctors()
                _doctors.value = result
                doctorsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorDetails(id: Long) {
        viewModelScope.launch {
            doctorDetailsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctorById(id)
                _selectedDoctor.value = result
                doctorDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun createDoctor(doctor: DoctorRequest) {
        viewModelScope.launch {
            doctorsUiState = BaseUiState.Loading
            try {
                doctorRepository.createDoctor(doctor)
                getDoctors() // Refresh the list
            } catch (e: Exception) {
                doctorsUiState = BaseUiState.Error
            }
        }
    }

    fun updateDoctor(id: Long, doctor: DoctorRequest) {
        viewModelScope.launch {
            doctorDetailsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.updateDoctor(id, doctor)
                _selectedDoctor.value = result
                doctorDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorPatients(doctorId: Long) {
        viewModelScope.launch {
            doctorPatientsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctorPatients(doctorId)
                doctorPatientsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorPatientsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorsBySpecialization(specialization: String) {
        viewModelScope.launch {
            doctorsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctorsBySpecialization(specialization)
                _doctors.value = result
                doctorsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorsUiState = BaseUiState.Error
            }
        }
    }

    fun prescribeMedication(doctorId: Long, appointmentId: Long, medication: MedicationRequest) {
        viewModelScope.launch {
            try {
                doctorRepository.prescribeMedication(doctorId, appointmentId, medication)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun provideFeedback(doctorId: Long, appointmentId: Long, feedback: FeedbackRequest) {
        viewModelScope.launch {
            try {
                doctorRepository.provideFeedback(doctorId, appointmentId, feedback)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                DoctorViewModel(
                    doctorRepository = application.container.doctorRepository
                )
            }
        }
    }
}