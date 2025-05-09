package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.DoctorRequest
import com.example.hospitalapp.network.model.DoctorResponse
import com.example.hospitalapp.network.model.FeedbackResponse
import com.example.hospitalapp.network.model.MedicationResponse
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.network.model.ReportResponse
import okhttp3.ResponseBody

interface DoctorRepository {
    suspend fun getDoctors(): List<DoctorResponse>
    suspend fun getDoctorById(id: Long): DoctorResponse
    suspend fun createDoctor(doctor: DoctorRequest): DoctorResponse
    suspend fun updateDoctor(id: Long, doctor: DoctorRequest): DoctorResponse
    suspend fun deleteDoctor(id: Long)
    suspend fun getDoctorPatients(id: Long): List<PatientResponse>
    suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse>
    suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse>
}

class DoctorRepositoryImpl(private val apiService: ApiService) : DoctorRepository {
    override suspend fun getDoctors(): List<DoctorResponse> =
        apiService.getDoctors()

    override suspend fun getDoctorById(id: Long): DoctorResponse =
        apiService.getDoctorById(id)

    override suspend fun createDoctor(doctor: DoctorRequest): DoctorResponse =
        apiService.createDoctor(doctor)

    override suspend fun updateDoctor(id: Long, doctor: DoctorRequest): DoctorResponse =
        apiService.updateDoctor(id, doctor)

    override suspend fun deleteDoctor(id: Long) =
        apiService.deleteDoctor(id)

    override suspend fun getDoctorPatients(id: Long): List<PatientResponse> =
        apiService.getDoctorPatients(id)

    override suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse> =
        apiService.getDoctorsBySpecialization(specialization)

    override suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse> =
        apiService.getDoctorAppointments(id)
}