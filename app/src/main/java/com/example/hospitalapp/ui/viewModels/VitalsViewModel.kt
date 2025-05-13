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
import com.example.hospitalapp.data.repositories.VitalsRepository
import com.example.hospitalapp.network.model.VitalsRequest
import com.example.hospitalapp.network.model.VitalsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VitalsViewModel(
    private val vitalsRepository: VitalsRepository
) : ViewModel() {

    var vitalsDetailsUiState: BaseUiState<VitalsResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientVitalsUiState: BaseUiState<List<VitalsResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var createVitalsUiState: BaseUiState<VitalsResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    private val _selectedVitals = MutableStateFlow<VitalsResponse?>(null)
    val selectedVitals: StateFlow<VitalsResponse?> = _selectedVitals

    private val _patientVitals = MutableStateFlow<List<VitalsResponse>>(emptyList())
    val patientVitals: StateFlow<List<VitalsResponse>> = _patientVitals

    fun getVitalsById(id: Long) {
        viewModelScope.launch {
            vitalsDetailsUiState = BaseUiState.Loading
            try {
                val result = vitalsRepository.getVitalsById(id)
                _selectedVitals.value = result
                vitalsDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                vitalsDetailsUiState = BaseUiState.Error
            }
        }
    }
    fun resetCreateVitalsState() {
        createVitalsUiState = BaseUiState.Success(null)
    }

    fun getVitalsByPatient(patientId: Long) {
        viewModelScope.launch {
            patientVitalsUiState = BaseUiState.Loading
            try {
                val result = vitalsRepository.getVitalsByPatient(patientId)
                _patientVitals.value = result
                patientVitalsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientVitalsUiState = BaseUiState.Error
            }
        }
    }

    fun createVitals(vitals: VitalsRequest) {
        viewModelScope.launch {
            createVitalsUiState = BaseUiState.Loading
            try {
                val result = vitalsRepository.createVitals(vitals)
                createVitalsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                createVitalsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                VitalsViewModel(
                    vitalsRepository = application.container.vitalsRepository
                )
            }
        }
    }
}