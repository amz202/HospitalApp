package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DoctorResponse(
    val id: Long,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val email: String,
    val phoneNumber: String,
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean,
    val active: Boolean,
    val createdAt: String,
    val appointments: List<AppointmentResponse> = emptyList(),
    val feedbacks: List<FeedbackResponse> = emptyList(),
    val prescribedMedications: List<MedicationResponse> = emptyList()
)

@Serializable
data class DoctorRequest(
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val email: String,
    val phoneNumber: String,
    val password: String,
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean = true
)
@Serializable
data class DoctorUpdateRequest(
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean,
    val phoneNumber: String,
    val address: String
)