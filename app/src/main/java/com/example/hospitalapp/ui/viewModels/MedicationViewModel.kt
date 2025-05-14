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
import com.example.hospitalapp.data.repositories.MedicationRepository
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.MedicationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    var medicationsUiState: BaseUiState<List<MedicationResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var medicationDetailsUiState: BaseUiState<MedicationResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientMedicationsUiState: BaseUiState<List<MedicationResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _medications = MutableStateFlow<List<MedicationResponse>>(emptyList())
    val medications: StateFlow<List<MedicationResponse>> = _medications

    private val _selectedMedication = MutableStateFlow<MedicationResponse?>(null)
    val selectedMedication: StateFlow<MedicationResponse?> = _selectedMedication

    var createMedicationUiState: BaseUiState<MedicationResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    fun createMedication(medicationRequest: MedicationRequest) {
        viewModelScope.launch {
            createMedicationUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.createMedication(medicationRequest)
                createMedicationUiState = BaseUiState.Success(result)

                getPatientMedications(medicationRequest.patientId)
            } catch (e: Exception) {
                Log.e("MedicationViewModel", "Error creating medication with request: ", e)
                createMedicationUiState = BaseUiState.Error
            }
        }
    }

    fun resetCreateMedicationState() {
        createMedicationUiState = BaseUiState.Success(null)
    }
    fun getMedications() {
        viewModelScope.launch {
            medicationsUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.getMedications()
                _medications.value = result
                medicationsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                medicationsUiState = BaseUiState.Error
            }
        }
    }

    fun getMedicationById(id: Long) {
        viewModelScope.launch {
            medicationDetailsUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.getMedicationById(id)
                _selectedMedication.value = result
                medicationDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                medicationDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getMedicationsByAppointment(appointmentId: Long) {
        viewModelScope.launch {
            medicationsUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.getMedicationsByAppointment(appointmentId)
                _medications.value = result
                medicationsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                medicationsUiState = BaseUiState.Error
            }
        }
    }

    fun getPatientMedications(patientId: Long) {
        viewModelScope.launch {
            patientMedicationsUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.getPatientMedications(patientId)
                patientMedicationsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientMedicationsUiState = BaseUiState.Error
            }
        }
    }

    fun getActiveMedications(patientId: Long) {
        viewModelScope.launch {
            patientMedicationsUiState = BaseUiState.Loading
            try {
                val result = medicationRepository.getActiveMedications(patientId)
                patientMedicationsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientMedicationsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                MedicationViewModel(
                    medicationRepository = application.container.medicationRepository
                )
            }
        }
    }
}