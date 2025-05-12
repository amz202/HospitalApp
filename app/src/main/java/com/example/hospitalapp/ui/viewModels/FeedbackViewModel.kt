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
import com.example.hospitalapp.data.repositories.FeedbackRepository
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.FeedbackRequest
import com.example.hospitalapp.network.model.FeedbackResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    var feedbackDetailsUiState: BaseUiState<FeedbackResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var doctorFeedbackUiState: BaseUiState<List<FeedbackResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var patientFeedbackUiState: BaseUiState<List<FeedbackResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var pendingFeedbackUiState: BaseUiState<List<AppointmentResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _selectedFeedback = MutableStateFlow<FeedbackResponse?>(null)
    val selectedFeedback: StateFlow<FeedbackResponse?> = _selectedFeedback

    fun getFeedbackById(id: Long) {
        viewModelScope.launch {
            feedbackDetailsUiState = BaseUiState.Loading
            try {
                val result = feedbackRepository.getFeedbackById(id)
                _selectedFeedback.value = result
                feedbackDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                feedbackDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getFeedbackByAppointment(appointmentId: Long) {
        viewModelScope.launch {
            feedbackDetailsUiState = BaseUiState.Loading
            try {
                val result = feedbackRepository.getFeedbackByAppointment(appointmentId)
                _selectedFeedback.value = result
                feedbackDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                feedbackDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun getFeedbackByDoctor(doctorId: Long) {
        viewModelScope.launch {
            doctorFeedbackUiState = BaseUiState.Loading
            try {
                val result = feedbackRepository.getFeedbackByDoctor(doctorId)
                doctorFeedbackUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                doctorFeedbackUiState = BaseUiState.Error
            }
        }
    }

    fun getFeedbackByPatient(patientId: Long) {
        viewModelScope.launch {
            patientFeedbackUiState = BaseUiState.Loading
            try {
                val result = feedbackRepository.getFeedbackByPatient(patientId)
                patientFeedbackUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                patientFeedbackUiState = BaseUiState.Error
            }
        }
    }


    fun createFeedback(doctorId: Long, appointmentId: Long, patientId: Long, feedback: FeedbackRequest) {
        viewModelScope.launch {
            feedbackDetailsUiState = BaseUiState.Loading
            try {
                // Create a new FeedbackRequest with all required fields
                val feedbackRequest = FeedbackRequest(
                    comments = feedback.comments,
                    diagnosis = feedback.diagnosis,
                    recommendations = feedback.recommendations,
                    nextSteps = feedback.nextSteps,
                    doctorId = doctorId,
                    patientId = patientId,
                    appointmentId = appointmentId
                )

                val result = feedbackRepository.createFeedback(feedbackRequest)
                _selectedFeedback.value = result
                feedbackDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                feedbackDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun updateFeedback(id: Long, feedback: FeedbackRequest) {
        viewModelScope.launch {
            feedbackDetailsUiState = BaseUiState.Loading
            try {
                feedbackRepository.updateFeedback(id, feedback)
                getFeedbackById(id) // Refresh the feedback details
            } catch (e: Exception) {
                feedbackDetailsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                FeedbackViewModel(
                    feedbackRepository = application.container.feedbackRepository
                )
            }
        }
    }
}