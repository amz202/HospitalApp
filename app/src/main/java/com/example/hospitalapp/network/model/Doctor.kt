package com.example.hospitalapp.network.model

data class DoctorResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
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

data class DoctorRequest(
    val firstName: String,
    val lastName: String,
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