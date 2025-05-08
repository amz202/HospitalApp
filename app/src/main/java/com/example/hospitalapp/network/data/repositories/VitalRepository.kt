package com.example.hospitalapp.network.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.VitalsRequest
import com.example.hospitalapp.network.model.VitalsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface VitalsRepository {
    suspend fun getVitals(id: Long): VitalsResponse
    suspend fun getPatientVitals(patientId: Long): List<VitalsResponse>
    suspend fun createVitals(vitals: VitalsRequest): ResponseBody
    suspend fun getLatestVitals(patientId: Long): VitalsResponse
    suspend fun getCriticalVitals(patientId: Long): List<VitalsResponse>
    suspend fun uploadVitalsCSV(patientId: Long, file: MultipartBody.Part): ResponseBody
}

class VitalsRepositoryImpl(private val apiService: ApiService) : VitalsRepository {
    override suspend fun getVitals(id: Long): VitalsResponse =
        apiService.getVitals(id)

    override suspend fun getPatientVitals(patientId: Long): List<VitalsResponse> =
        apiService.getPatientVitals(patientId)

    override suspend fun createVitals(vitals: VitalsRequest): ResponseBody =
        apiService.createVitals(vitals)

    override suspend fun getLatestVitals(patientId: Long): VitalsResponse =
        apiService.getLatestVitals(patientId)

    override suspend fun getCriticalVitals(patientId: Long): List<VitalsResponse> =
        apiService.getCriticalVitals(patientId)

    override suspend fun uploadVitalsCSV(patientId: Long, file: MultipartBody.Part): ResponseBody =
        apiService.uploadVitalsCSV(patientId, file)
}