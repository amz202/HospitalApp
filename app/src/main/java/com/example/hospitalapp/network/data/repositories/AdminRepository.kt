package com.example.hospitalapp.network.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AdminRequest
import com.example.hospitalapp.network.model.AdminResponse
import okhttp3.ResponseBody

interface AdminRepository {
    suspend fun getAdmin(id: Long): AdminResponse
    suspend fun getAdmins(): List<AdminResponse>
    suspend fun createAdmin(admin: AdminRequest): ResponseBody
    suspend fun updateAdmin(id: Long, admin: AdminRequest): ResponseBody
    suspend fun deleteAdmin(id: Long): ResponseBody
}

class AdminRepositoryImpl(private val apiService: ApiService) : AdminRepository {
    override suspend fun getAdmin(id: Long): AdminResponse =
        apiService.getAdmin(id)

    override suspend fun getAdmins(): List<AdminResponse> =
        apiService.getAdmins()

    override suspend fun createAdmin(admin: AdminRequest): ResponseBody =
        apiService.createAdmin(admin)

    override suspend fun updateAdmin(id: Long, admin: AdminRequest): ResponseBody =
        apiService.updateAdmin(id, admin)

    override suspend fun deleteAdmin(id: Long): ResponseBody =
        apiService.deleteAdmin(id)
}