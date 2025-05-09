package com.example.hospitalapp.network.model

data class AppointmentResponse(
    val id: Long,
    val patientId: Long,
    val doctorId: Long,
    val scheduledTime: String,
    val type: AppointmentType,
    val status: AppointmentStatus,
    val reason: String,
    val notes: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String
)

data class AppointmentRequest(
    val patientId: Long,
    val doctorId: Long,
    val scheduledTime: String,
    val type: AppointmentType,
    val reason: String
)

enum class AppointmentType {
    IN_PERSON, VIDEO_CONSULTATION, EMERGENCY
}

enum class AppointmentStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}