package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.FeedbackRequest
import com.example.hospitalapp.network.model.FeedbackResponse
import okhttp3.ResponseBody

interface FeedbackRepository {
    suspend fun getFeedbackById(id: Long): FeedbackResponse
    suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackResponse
    suspend fun getFeedbackByDoctor(doctorId: Long): List<FeedbackResponse>
    suspend fun getFeedbackByPatient(patientId: Long): List<FeedbackResponse>
    suspend fun createFeedback(feedback: FeedbackRequest): ResponseBody
    suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): ResponseBody
    suspend fun hasFeedback(appointmentId: Long): Boolean
    suspend fun getAppointmentsWithoutFeedback(doctorId: Long): List<AppointmentResponse>
}

class FeedbackRepositoryImpl(private val apiService: ApiService) : FeedbackRepository {
    override suspend fun getFeedbackById(id: Long): FeedbackResponse =
        apiService.getFeedbackById(id)

    override suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackResponse =
        apiService.getFeedbackByAppointment(appointmentId)

    override suspend fun getFeedbackByDoctor(doctorId: Long): List<FeedbackResponse> =
        apiService.getFeedbackByDoctor(doctorId)

    override suspend fun getFeedbackByPatient(patientId: Long): List<FeedbackResponse> =
        apiService.getFeedbackByPatient(patientId)

    override suspend fun createFeedback(feedback: FeedbackRequest): ResponseBody =
        apiService.createFeedback(feedback)

    override suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): ResponseBody =
        apiService.updateFeedback(id, feedback)

    override suspend fun hasFeedback(appointmentId: Long): Boolean =
        apiService.hasFeedback(appointmentId)

    override suspend fun getAppointmentsWithoutFeedback(doctorId: Long): List<AppointmentResponse> =
        apiService.getAppointmentsWithoutFeedback(doctorId)
}