package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate


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
    val username: String,
    val email: String,
    val fName: String?,
    val lName: String?,
    val phoneNumber: String?,
    val gender: String?,
    val dob: String?,
    val address: String?,
    val role: String,
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val primaryDoctor: DoctorResponse?,
    val version: Long?
)

@Serializable
data class PatientUpdateRequest(
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val fName: String?,
    val lName: String?,
    val phoneNumber: String?,
    val gender: String?,
    val dob: String?,
    val address: String?
)