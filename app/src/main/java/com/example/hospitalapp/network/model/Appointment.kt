package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
@Serializable
data class AppointmentResponse(
    val id: Long,
    val patient: PatientResponse,
    val doctor: DoctorResponse,
    val scheduledTime: String,
    val status: AppointmentStatus,
    val type: String,
    val notes: String?,
    val reason: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String
)
@Serializable
data class AppointmentRequest(
    val patientId: Long,
    val doctorId: Long,
    val scheduledTime: String,
    val type: String,
    val notes: String? = null,
    val reason: String? = null
)

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED;

    companion object {
        fun fromString(value: String): AppointmentStatus {
            return AppointmentStatus.entries.find { it.name == value.uppercase() } ?: PENDING
        }
    }
}

enum class AppointmentType {
    IN_PERSON, VIDEO_CONSULTATION
}
