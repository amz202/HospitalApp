package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AdminRequest
import com.example.hospitalapp.network.model.AdminResponse
import okhttp3.ResponseBody

interface AdminRepository {
    suspend fun getAdmins(): List<AdminResponse>
    suspend fun getAdminById(id: Long): AdminResponse
    suspend fun createAdmin(admin: AdminRequest): AdminResponse
    suspend fun updateAdmin(id: Long, admin: AdminRequest): AdminResponse
    suspend fun deleteAdmin(id: Long)
    suspend fun getTotalPatientsCount(): Long
    suspend fun getTotalDoctorsCount(): Long
    suspend fun getTotalAppointmentsCount(): Long
    suspend fun getPendingAppointmentsCount(): Long
}

class AdminRepositoryImpl(private val apiService: ApiService) : AdminRepository {
    override suspend fun getAdmins(): List<AdminResponse> =
        apiService.getAdmins()

    override suspend fun getAdminById(id: Long): AdminResponse =
        apiService.getAdminById(id)

    override suspend fun createAdmin(admin: AdminRequest): AdminResponse =
        apiService.createAdmin(admin)

    override suspend fun updateAdmin(id: Long, admin: AdminRequest): AdminResponse =
        apiService.updateAdmin(id, admin)

    override suspend fun deleteAdmin(id: Long) =
        apiService.deleteAdmin(id)

    override suspend fun getTotalPatientsCount(): Long =
        apiService.getTotalPatientsCount()

    override suspend fun getTotalDoctorsCount(): Long =
        apiService.getTotalDoctorsCount()

    override suspend fun getTotalAppointmentsCount(): Long =
        apiService.getTotalAppointmentsCount()

    override suspend fun getPendingAppointmentsCount(): Long =
        apiService.getPendingAppointmentsCount()
}