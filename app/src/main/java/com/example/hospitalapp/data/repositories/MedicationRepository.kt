package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.MedicationResponse
import okhttp3.ResponseBody

interface MedicationRepository {
    suspend fun getMedications(): List<MedicationResponse>
    suspend fun getMedicationById(id: Long): MedicationResponse
    suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationResponse>
    suspend fun getPatientMedications(patientId: Long): List<MedicationResponse>
    suspend fun getActiveMedications(patientId: Long): List<MedicationResponse>
}

class MedicationRepositoryImpl(private val apiService: ApiService) : MedicationRepository {
    override suspend fun getMedications(): List<MedicationResponse> =
        apiService.getMedications()

    override suspend fun getMedicationById(id: Long): MedicationResponse =
        apiService.getMedicationById(id)

    override suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationResponse> =
        apiService.getMedicationsByAppointment(appointmentId)

    override suspend fun getPatientMedications(patientId: Long): List<MedicationResponse> =
        apiService.getPatientMedications(patientId)

    override suspend fun getActiveMedications(patientId: Long): List<MedicationResponse> =
        apiService.getActiveMedications(patientId)
}