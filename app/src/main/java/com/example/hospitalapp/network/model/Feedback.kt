package com.example.hospitalapp.network.model

data class FeedbackResponse(
    val id: Long,
    val comments: String,
    val diagnosis: String?,
    val recommendations: String?,
    val nextSteps: String?,
    val createdAt: String,
    val updatedAt: String,
    val doctor: DoctorResponse,
    val patient: PatientResponse,
    val appointment: AppointmentResponse
)

data class FeedbackRequest(
    val comments: String,
    val diagnosis: String?,
    val recommendations: String?,
    val nextSteps: String?,
    val doctorId: Long,
    val patientId: Long,
    val appointmentId: Long
)