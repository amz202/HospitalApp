package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.VitalsRequest
import com.example.hospitalapp.network.model.VitalsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface VitalsRepository {
    suspend fun getVitalsById(id: Long): VitalsResponse
    suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse>
    suspend fun createVitals(vitals: VitalsRequest): VitalsResponse
}

class VitalsRepositoryImpl(private val apiService: ApiService) : VitalsRepository {
    override suspend fun getVitalsById(id: Long): VitalsResponse =
        apiService.getVitalsById(id)

    override suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse> =
        apiService.getVitalsByPatient(patientId)

    override suspend fun createVitals(vitals: VitalsRequest): VitalsResponse =
        apiService.createVitals(vitals)
}