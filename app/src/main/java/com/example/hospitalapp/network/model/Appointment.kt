package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
@Serializable
data class AppointmentResponse(
    val id: Long,
    val patient: PatientResponse,
    val doctor: DoctorResponse,
    val scheduledTime: String,
    val type: AppointmentType,
    val status: AppointmentStatus,
    val reason: String,
    val notes: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String
)
@Serializable
data class AppointmentRequest(
    val patientId: Long,
    val doctorId: Long,
    val scheduledTime: String,
    val type: AppointmentType,
    val reason: String
)

enum class AppointmentStatus {
    REQUESTED, APPROVED, DECLINED, COMPLETED, CANCELLED
}

enum class AppointmentType {
    IN_PERSON, VIDEO_CONSULTATION
}
