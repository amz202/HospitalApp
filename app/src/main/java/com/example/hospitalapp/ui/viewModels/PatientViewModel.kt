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

    var patientReportsUiState: BaseUiState<List<ReportResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var updatePatientUiState: BaseUiState<PatientResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    private val _patients = MutableStateFlow<List<PatientResponse>>(emptyList())
    val patients: StateFlow<List<PatientResponse>> = _patients

    private val _selectedPatient = MutableStateFlow<PatientResponse?>(null)
    val selectedPatient: StateFlow<PatientResponse?> = _selectedPatient

    fun getPatients() {
        viewModelScope.launch {
            patientsUiState = BaseUiState.Loading
            try {
                val result = patientRepository.getPatients()
                _patients.value = result
                patientsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientsUiState = BaseUiState.Error
            }
        }
    }

    fun getPatientDetails(id: Long) {
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

    fun getPatientVitals(patientId: Long) {
        viewModelScope.launch {
            patientVitalsUiState = BaseUiState.Loading
            try {
                val result = patientRepository.getPatientVitals(patientId)
                patientVitalsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientVitalsUiState = BaseUiState.Error
            }
        }
    }

    fun getPatientReports(patientId: Long) {
        viewModelScope.launch {
            patientReportsUiState = BaseUiState.Loading
            try {
                val result = patientRepository.getPatientReports(patientId)
                patientReportsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientReportsUiState = BaseUiState.Error
            }
        }
    }

    fun updatePatient(patientId: Long, request: PatientRequest) {
        viewModelScope.launch {
            try {
                updatePatientUiState = BaseUiState.Loading
                errorMessage = null

                // Get current patient data to keep existing values
                val currentPatient = _selectedPatient.value
                if (currentPatient != null) {
                    // Create new PatientRequest keeping the existing data
                    val patientRequest = PatientRequest(
                        fName = currentPatient.fName,
                        lName = currentPatient.lName,
                        email = currentPatient.email,
                        phoneNumber = request.phoneNumber,
                        password = "",  // Not needed for update
                        bloodGroup = request.bloodGroup,
                        emergencyContact = request.emergencyContact,
                        allergies = request.allergies,
                        primaryDoctorId = null,  // Since it's not in PatientResponse, we'll set it to null
                        gender = currentPatient.gender,
                        dob = currentPatient.dob,
                        address = request.address
                    )

                    val result = patientRepository.updatePatient(patientId, patientRequest)
                    _selectedPatient.value = result
                    updatePatientUiState = BaseUiState.Success(result)
                } else {
                    throw IllegalStateException("Cannot update: Current patient data not found")
                }
            } catch (e: Exception) {
                updatePatientUiState = BaseUiState.Error
                errorMessage = when {
                    e.message?.contains("400") == true -> "Invalid input data"
                    e is IOException -> "Network error: Please check your connection"
                    else -> "Error updating information: ${e.message}"
                }
                Log.e("PatientViewModel", "Error updating patient", e)
            }
        }
    }

    fun clearUpdateState() {
        updatePatientUiState = BaseUiState.Loading
        errorMessage = null
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