package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AppointmentRequest
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import okhttp3.ResponseBody

interface AppointmentRepository {
    suspend fun getAppointment(id: Long): AppointmentResponse
    suspend fun getAppointments(): List<AppointmentResponse>
    suspend fun createAppointment(appointment: AppointmentRequest): ResponseBody
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): ResponseBody
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse>
}

class AppointmentRepositoryImpl(private val apiService: ApiService) : AppointmentRepository {
    override suspend fun getAppointment(id: Long): AppointmentResponse =
        apiService.getAppointment(id)

    override suspend fun getAppointments(): List<AppointmentResponse> =
        apiService.getAppointments()

    override suspend fun createAppointment(appointment: AppointmentRequest): ResponseBody =
        apiService.createAppointment(appointment)

    override suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): ResponseBody =
        apiService.updateAppointmentStatus(id, status)

    override suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse> =
        apiService.getPatientAppointments(patientId)
}