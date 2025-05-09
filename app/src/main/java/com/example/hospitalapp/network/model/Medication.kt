package com.example.hospitalapp.network.model

data class MedicationResponse(
    val id: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String,
    val instructions: String,
    val doctor: DoctorResponse,
    val patient: PatientResponse,
    val appointment: AppointmentResponse?,
    val prescribedAt: String,
    val notes: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class MedicationRequest(
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String,
    val instructions: String,
    val doctorId: Long,
    val patientId: Long,
    val appointmentId: Long?,
    val notes: String?
)