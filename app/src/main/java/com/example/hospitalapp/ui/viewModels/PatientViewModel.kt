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
import com.example.hospitalapp.data.repositories.PatientRepository
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.network.model.ReportResponse
import com.example.hospitalapp.network.model.VitalsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PatientViewModel(
    private val patientRepository: PatientRepository,
) : ViewModel() {

    var patientsUiState: BaseUiState<List<PatientResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientDetailsUiState: BaseUiState<PatientResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientVitalsUiState: BaseUiState<List<VitalsResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientReportsUiState: BaseUiState<List<ReportResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var updatePatientUiState: BaseUiState<PatientResponse> by mutableStateOf(BaseUiState.Success(null))
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

    fun createPatient(patient: PatientRequest) {
        viewModelScope.launch {
            patientsUiState = BaseUiState.Loading
            try {
                patientRepository.createPatient(patient)
                getPatients() // Refresh the list
            } catch (e: Exception) {
                patientsUiState = BaseUiState.Error
            }
        }
    }

    // Removed redundant updatePatient method and updatePatientInfo method
    // Combined functionality into updatePatientAfterSignup

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

    fun updatePatientAfterSignup(
        patientId: Long,
        bloodGroup: String,
        emergencyContact: String,
        allergies: String,
        primaryDoctorId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                updatePatientUiState = BaseUiState.Loading
                errorMessage = null

                val currentUser = UserViewModel.currentUser.value ?: throw IllegalStateException("No user found")

                val patientRequest = PatientRequest(
                    fName = currentUser.fName,
                    lName = currentUser.lName,
                    email = currentUser.email,
                    phoneNumber = currentUser.phoneNumber ?: "",
                    password = "", // Password already set during signup
                    dob = currentUser.dob,
                    bloodGroup = bloodGroup,
                    emergencyContact = emergencyContact,
                    allergies = allergies,
                    primaryDoctorId = primaryDoctorId
                )

                val result = patientRepository.updatePatient(patientId, patientRequest)
                _selectedPatient.value = result
                updatePatientUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                updatePatientUiState = BaseUiState.Error
                errorMessage = when {
                    e is IllegalStateException -> "User information not found"
                    e.message?.contains("400") == true -> "Invalid input data"
                    e is IOException -> "Network error: Please check your connection"
                    else -> "Error updating patient information: ${e.message}"
                }
                Log.e("PatientViewModel", "Error updating patient info", e)
            }
        }
    }

    fun clearUpdateState() {
        updatePatientUiState = BaseUiState.Success(null)
        errorMessage = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                PatientViewModel(
                    patientRepository = application.container.patientRepository,
                )
            }
        }
    }
}