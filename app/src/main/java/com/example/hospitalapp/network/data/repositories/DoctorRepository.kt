package com.example.hospitalapp.network.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.DoctorRequest
import com.example.hospitalapp.network.model.DoctorResponse
import com.example.hospitalapp.network.model.FeedbackResponse
import com.example.hospitalapp.network.model.MedicationResponse
import com.example.hospitalapp.network.model.ReportResponse
import okhttp3.ResponseBody

interface DoctorRepository {
    suspend fun getDoctor(id: Long): DoctorResponse
    suspend fun getDoctors(): List<DoctorResponse>
    suspend fun createDoctor(doctor: DoctorRequest): ResponseBody
    suspend fun updateDoctor(id: Long, doctor: DoctorRequest): ResponseBody
    suspend fun deleteDoctor(id: Long): ResponseBody
    suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse>
    suspend fun getDoctorFeedback(id: Long): List<FeedbackResponse>
    suspend fun getDoctorPrescribedMedications(id: Long): List<MedicationResponse>
    suspend fun getDoctorReports(id: Long): List<ReportResponse>
}

class DoctorRepositoryImpl(private val apiService: ApiService) : DoctorRepository {
    override suspend fun getDoctor(id: Long): DoctorResponse =
        apiService.getDoctor(id)

    override suspend fun getDoctors(): List<DoctorResponse> =
        apiService.getDoctors()

    override suspend fun createDoctor(doctor: DoctorRequest): ResponseBody =
        apiService.createDoctor(doctor)

    override suspend fun updateDoctor(id: Long, doctor: DoctorRequest): ResponseBody =
        apiService.updateDoctor(id, doctor)

    override suspend fun deleteDoctor(id: Long): ResponseBody =
        apiService.deleteDoctor(id)

    override suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse> =
        apiService.getDoctorAppointments(id)

    override suspend fun getDoctorFeedback(id: Long): List<FeedbackResponse> =
        apiService.getDoctorFeedback(id)

    override suspend fun getDoctorPrescribedMedications(id: Long): List<MedicationResponse> =
        apiService.getDoctorPrescribedMedications(id)

    override suspend fun getDoctorReports(id: Long): List<ReportResponse> =
        apiService.getDoctorReports(id)
}