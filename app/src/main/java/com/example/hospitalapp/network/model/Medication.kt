package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicationResponse(
    val id: Long,
    val patient: PatientResponse,
    val appointmentId: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String?,
    val instructions: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
@Serializable
data class MedicationRequest(
    val patientId: Long,
    val appointmentId: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String? = null,
    val instructions: String? = null
)