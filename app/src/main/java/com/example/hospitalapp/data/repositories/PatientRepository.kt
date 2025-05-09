package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientResponse
import okhttp3.ResponseBody

interface PatientRepository {
    suspend fun getPatient(id: Long): PatientResponse
    suspend fun getPatients(): List<PatientResponse>
    suspend fun createPatient(patient: PatientRequest): ResponseBody
    suspend fun updatePatient(id: Long, patient: PatientRequest): ResponseBody
    suspend fun deletePatient(id: Long): ResponseBody
}

class PatientRepositoryImpl(private val apiService: ApiService) : PatientRepository {
    override suspend fun getPatient(id: Long): PatientResponse =
        apiService.getPatient(id)

    override suspend fun getPatients(): List<PatientResponse> =
        apiService.getPatients()

    override suspend fun createPatient(patient: PatientRequest): ResponseBody =
        apiService.createPatient(patient)

    override suspend fun updatePatient(id: Long, patient: PatientRequest): ResponseBody =
        apiService.updatePatient(id, patient)

    override suspend fun deletePatient(id: Long): ResponseBody =
        apiService.deletePatient(id)
}