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

    private val _selectedDoctor = MutableStateFlow<DoctorResponse?>(null)
    val selectedDoctor: StateFlow<DoctorResponse?> = _selectedDoctor

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getDoctors() {
        viewModelScope.launch {
            doctorsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctors()
                doctorsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching doctors"
                doctorsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorById(id: Long) {
        viewModelScope.launch {
            doctorDetailsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctorById(id)
                _selectedDoctor.value = result
                doctorDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching doctor details"
                doctorDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun createDoctorDetails(userId: Long, request: DoctorRequest) {
        viewModelScope.launch {
            doctorDetailsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.createDoctorDetails(userId, request)
                _selectedDoctor.value = result
                doctorDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error creating doctor details"
                doctorDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun updateDoctorDetails(userId: Long, request: DoctorRequest) {
        viewModelScope.launch {
            doctorDetailsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.updateDoctorDetails(userId, request)
                _selectedDoctor.value = result
                doctorDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error updating doctor details"
                doctorDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getDoctorsBySpecialization(specialization: String) {
        viewModelScope.launch {
            doctorsUiState = BaseUiState.Loading
            try {
                val result = doctorRepository.getDoctorsBySpecialization(specialization)
                doctorsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching doctors by specialization"
                doctorsUiState = BaseUiState.Error
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
                _errorMessage.value = e.message ?: "Error fetching doctor's patients"
                doctorPatientsUiState = BaseUiState.Error
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