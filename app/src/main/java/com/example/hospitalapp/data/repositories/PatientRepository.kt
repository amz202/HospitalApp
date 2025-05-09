package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.network.model.ReportResponse
import com.example.hospitalapp.network.model.VitalsResponse
import okhttp3.ResponseBody

interface PatientRepository {
    suspend fun getPatients(): List<PatientResponse>
    suspend fun getPatientById(id: Long): PatientResponse
    suspend fun createPatient(patient: PatientRequest): PatientResponse
    suspend fun updatePatient(id: Long, patient: PatientRequest): PatientResponse
    suspend fun deletePatient(id: Long)
    suspend fun getPatientVitals(patientId: Long): List<VitalsResponse>
    suspend fun getPatientReports(patientId: Long): List<ReportResponse>
    suspend fun getPatientsByDoctor(doctorId: Long): List<PatientResponse>
}

class PatientRepositoryImpl(private val apiService: ApiService) : PatientRepository {
    override suspend fun getPatients(): List<PatientResponse> =
        apiService.getPatients()

    override suspend fun getPatientById(id: Long): PatientResponse =
        apiService.getPatientById(id)

    override suspend fun createPatient(patient: PatientRequest): PatientResponse =
        apiService.createPatient(patient)

    override suspend fun updatePatient(id: Long, patient: PatientRequest): PatientResponse =
        apiService.updatePatient(id, patient)

    override suspend fun deletePatient(id: Long) =
        apiService.deletePatient(id)

    override suspend fun getPatientVitals(patientId: Long): List<VitalsResponse> =
        apiService.getPatientVitals(patientId)

    override suspend fun getPatientReports(patientId: Long): List<ReportResponse> =
        apiService.getPatientReports(patientId)

    override suspend fun getPatientsByDoctor(doctorId: Long): List<PatientResponse> =
        apiService.getPatientsByDoctor(doctorId)
}