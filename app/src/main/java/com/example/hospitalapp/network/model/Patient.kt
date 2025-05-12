package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PatientResponse(
    val id: Long,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val email: String,
    val phoneNumber: String,
    val dob: String,  // Changed from dateOfBirth
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val active: Boolean,
    val createdAt: String,
    val primaryDoctor: DoctorResponse?,
    val vitalsList: List<VitalsResponse> = emptyList(),
    val appointments: List<AppointmentResponse> = emptyList(),
    val medications: List<MedicationResponse> = emptyList(),
    val reports: List<ReportResponse> = emptyList()
)

@Serializable
data class PatientRequest(
    val fName: String,
    val lName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val dob: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val primaryDoctorId: Long?
)
@Serializable
data class PatientSignupRequest(
    val email: String,
    val password: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String,
    val dob: String,
)

@Serializable
data class PatientMedicalInfoRequest(
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val primaryDoctorId: Long? = null
)

@Serializable
data class PatientResponse(
    val id: Long,
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val dob: String,
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val primaryDoctorId: Long?,
    val createdAt: String,
    val updatedAt: String
)