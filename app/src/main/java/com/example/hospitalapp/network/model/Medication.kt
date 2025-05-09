package com.example.hospitalapp.network.model

data class MedicationResponse(
    val id: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String,
    val instructions: String,
    val patientId: Long,
    val doctorId: Long,
    val appointmentId: Long,
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
    val patientId: Long,
    val doctorId: Long,
    val appointmentId: Long
)