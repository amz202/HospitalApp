package com.example.hospitalapp.network.model

data class PatientResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val medicalHistory: String,
    val allergies: String,
    val active: Boolean,
    val createdAt: String,
    val vitalsList: List<VitalsResponse>,
    val appointments: List<AppointmentResponse>,
    val medications: List<MedicationResponse>
)

data class PatientRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val medicalHistory: String,
    val allergies: String
)