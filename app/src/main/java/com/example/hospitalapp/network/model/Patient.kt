package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PatientRequest(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val gender: String,
    val dob: String,
    val address: String,
    val bloodGroup: String?,
    val allergies: List<String>,
    val medicalHistory: List<String>
)

@Serializable
data class PatientResponse(
    val id: Long,
    val role: String,
    val accountCreationDate: String,
    val bloodGroup: String?,
    val allergies: List<String>,
    val medicalHistory: List<String>
)


@Serializable
data class PatientUpdateRequest(
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val phoneNumber: String?,
    val address: String
)
@Serializable
data class PatientMedicalInfoRequest(
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val primaryDoctorId: Long? = null
)