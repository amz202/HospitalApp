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
import com.example.hospitalapp.data.repositories.AdminRepository
import com.example.hospitalapp.network.model.AdminRequest
import com.example.hospitalapp.network.model.AdminResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    var adminsUiState: BaseUiState<List<AdminResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var adminDetailsUiState: BaseUiState<AdminResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var dashboardStatsUiState: BaseUiState<DashboardStats> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _admins = MutableStateFlow<List<AdminResponse>>(emptyList())
    val admins: StateFlow<List<AdminResponse>> = _admins

    private val _selectedAdmin = MutableStateFlow<AdminResponse?>(null)
    val selectedAdmin: StateFlow<AdminResponse?> = _selectedAdmin

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats

    data class DashboardStats(
        val totalPatients: Long,
        val totalDoctors: Long,
        val totalAppointments: Long,
        val pendingAppointments: Long
    )

    fun getAdmins() {
        viewModelScope.launch {
            adminsUiState = BaseUiState.Loading
            try {
                val result = adminRepository.getAdmins()
                _admins.value = result
                adminsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                adminsUiState = BaseUiState.Error
            }
        }
    }

    fun getAdminById(id: Long) {
        viewModelScope.launch {
            adminDetailsUiState = BaseUiState.Loading
            try {
                val result = adminRepository.getAdminById(id)
                _selectedAdmin.value = result
                adminDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                adminDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun createAdmin(admin: AdminRequest) {
        viewModelScope.launch {
            adminsUiState = BaseUiState.Loading
            try {
                adminRepository.createAdmin(admin)
                getAdmins()
            } catch (e: Exception) {
                adminsUiState = BaseUiState.Error
            }
        }
    }

    fun updateAdmin(id: Long, admin: AdminRequest) {
        viewModelScope.launch {
            adminDetailsUiState = BaseUiState.Loading
            try {
                val result = adminRepository.updateAdmin(id, admin)
                _selectedAdmin.value = result
                adminDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                adminDetailsUiState = BaseUiState.Error
            }
        }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            dashboardStatsUiState = BaseUiState.Loading
            try {
                val totalPatients = adminRepository.getTotalPatientsCount()
                val totalDoctors = adminRepository.getTotalDoctorsCount()
                val totalAppointments = adminRepository.getTotalAppointmentsCount()
                val pendingAppointments = adminRepository.getPendingAppointmentsCount()

                val stats = DashboardStats(
                    totalPatients = totalPatients,
                    totalDoctors = totalDoctors,
                    totalAppointments = totalAppointments,
                    pendingAppointments = pendingAppointments
                )
                _dashboardStats.value = stats
                dashboardStatsUiState = BaseUiState.Success(stats)
            } catch (e: Exception) {
                dashboardStatsUiState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                AdminViewModel(
                    adminRepository = application.container.adminRepository
                )
            }
        }
    }
}