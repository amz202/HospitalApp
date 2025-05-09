package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AppointmentRequest
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import okhttp3.ResponseBody

interface AppointmentRepository {
    suspend fun getAppointments(): List<AppointmentResponse>
    suspend fun getAppointmentById(id: Long): AppointmentResponse
    suspend fun createAppointment(appointment: AppointmentRequest): AppointmentResponse
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse>
    suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse>
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse>
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse
}

class AppointmentRepositoryImpl(private val apiService: ApiService) : AppointmentRepository {
    override suspend fun getAppointments(): List<AppointmentResponse> =
        apiService.getAppointments()

    override suspend fun getAppointmentById(id: Long): AppointmentResponse =
        apiService.getAppointmentById(id)

    override suspend fun createAppointment(appointment: AppointmentRequest): AppointmentResponse =
        apiService.createAppointment(appointment)

    override suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse> =
        apiService.getPatientAppointments(patientId)

    override suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse> =
        apiService.getDoctorAppointments(doctorId)

    override suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse> =
        apiService.getAppointmentsByStatus(status)

    override suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse> =
        apiService.getUpcomingAppointmentsByPatient(patientId)

    override suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse> =
        apiService.getUpcomingAppointmentsByDoctor(doctorId)

    override suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse =
        apiService.updateAppointmentStatus(id, status)
}