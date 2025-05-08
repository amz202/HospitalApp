package com.example.hospitalapp.network.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.FeedbackRequest
import com.example.hospitalapp.network.model.FeedbackResponse
import okhttp3.ResponseBody

interface FeedbackRepository {
    suspend fun getFeedback(id: Long): FeedbackResponse
    suspend fun createFeedback(feedback: FeedbackRequest): ResponseBody
    suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): ResponseBody
    suspend fun getPatientFeedback(patientId: Long): List<FeedbackResponse>
}

class FeedbackRepositoryImpl(private val apiService: ApiService) : FeedbackRepository {
    override suspend fun getFeedback(id: Long): FeedbackResponse =
        apiService.getFeedback(id)

    override suspend fun createFeedback(feedback: FeedbackRequest): ResponseBody =
        apiService.createFeedback(feedback)

    override suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): ResponseBody =
        apiService.updateFeedback(id, feedback)

    override suspend fun getPatientFeedback(patientId: Long): List<FeedbackResponse> =
        apiService.getPatientFeedback(patientId)
}