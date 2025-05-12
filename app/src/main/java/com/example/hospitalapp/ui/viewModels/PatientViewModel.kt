package com.example.hospitalapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hospitalapp.HospitalApplication
import com.example.hospitalapp.data.repositories.PatientRepository
import com.example.hospitalapp.network.model.PatientMedicalInfoRequest
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.network.model.PatientUpdateRequest
import com.example.hospitalapp.network.model.ReportResponse
import com.example.hospitalapp.network.model.VitalsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PatientViewModel(
    private val patientRepository: PatientRepository
) : ViewModel() {

    var patientsUiState: BaseUiState<List<PatientResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientDetailsUiState: BaseUiState<PatientResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientVitalsUiState: BaseUiState<List<VitalsResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _selectedPatient = MutableStateFlow<PatientResponse?>(null)
    val selectedPatient: StateFlow<PatientResponse?> = _selectedPatient

    // Fix for getPatients
    fun getPatients() {
        viewModelScope.launch {
            patientsUiState = BaseUiState.Loading
            try {
                val result = patientRepository.getAllPatients()
                patientsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientsUiState = BaseUiState.Error
            }
        }
    }

    // Fix for getPatientVitals - collect the Flow
    fun getPatientVitals(patientId: Long) {
        viewModelScope.launch {
            patientVitalsUiState = BaseUiState.Loading
            try {
                patientRepository.getPatientVitals(patientId).collect { vitalsList ->
                    patientVitalsUiState = BaseUiState.Success(vitalsList)
                }
            } catch (e: Exception) {
                patientVitalsUiState = BaseUiState.Error
            }
        }
    }

    fun updatePatient(patientId: Long, request: PatientUpdateRequest) {
        viewModelScope.launch {
            patientDetailsUiState = BaseUiState.Loading
            try {
                val currentPatient = _selectedPatient.value
                    ?: throw IllegalStateException("No patient selected")

                val patientRequest = PatientRequest(
                    username = currentPatient.username,
                    email = currentPatient.email,
                    password = "",  // Not needed for update
                    fName = currentPatient.fName,
                    lName = currentPatient.lName,
                    phoneNumber = request.phoneNumber ?: currentPatient.phoneNumber,
                    gender = currentPatient.gender,
                    dob = currentPatient.dob,
                    address = request.address,
                    bloodGroup = request.bloodGroup ?: currentPatient.bloodGroup,
                    allergies = request.allergies?.split(",")?.map { it.trim() } ?: currentPatient.allergies,
                    medicalHistory = currentPatient.medicalHistory
                )

                val result = patientRepository.updatePatient(patientId, patientRequest)
                _selectedPatient.value = result
                patientDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientDetailsUiState = BaseUiState.Error
            }
        }
    }

    // Add other necessary functions
    fun getPatientById(id: Long) {
        viewModelScope.launch {
            patientDetailsUiState = BaseUiState.Loading
            try {
                val result = patientRepository.getPatientById(id)
                _selectedPatient.value = result
                patientDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientDetailsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                PatientViewModel(
                    patientRepository = application.container.patientRepository
                )
            }
        }
    }
}