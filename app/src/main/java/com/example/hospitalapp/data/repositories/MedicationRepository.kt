package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.MedicationResponse
import okhttp3.ResponseBody

interface MedicationRepository {
    suspend fun getMedication(id: Long): MedicationResponse
    suspend fun getMedications(): List<MedicationResponse>
    suspend fun createMedication(medication: MedicationRequest): ResponseBody
    suspend fun updateMedication(id: Long, medication: MedicationRequest): ResponseBody
    suspend fun getPatientMedications(patientId: Long): List<MedicationResponse>
    suspend fun getActiveMedications(patientId: Long): List<MedicationResponse>
}

class MedicationRepositoryImpl(private val apiService: ApiService) : MedicationRepository {
    override suspend fun getMedication(id: Long): MedicationResponse =
        apiService.getMedication(id)

    override suspend fun getMedications(): List<MedicationResponse> =
        apiService.getMedications()

    override suspend fun createMedication(medication: MedicationRequest): ResponseBody =
        apiService.createMedication(medication)

    override suspend fun updateMedication(id: Long, medication: MedicationRequest): ResponseBody =
        apiService.updateMedication(id, medication)

    override suspend fun getPatientMedications(patientId: Long): List<MedicationResponse> =
        apiService.getPatientMedications(patientId)

    override suspend fun getActiveMedications(patientId: Long): List<MedicationResponse> =
        apiService.getActiveMedications(patientId)
}