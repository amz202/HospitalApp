package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PatientRequest(
    val fName: String,
    val lName: String,
    val email: String,
    val phoneNumber: String?,
    val password: String,
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val primaryDoctorId: Long?,
    val gender: Gender,
    val dob: String,
    val address: String
)
@Serializable
data class PatientResponse(
    val id: Long,
    val fName: String,
    val lName: String,
    val email: String,
    val phoneNumber: String?,
    val bloodGroup: String?,
    val emergencyContact: String?,
    val allergies: String?,
    val gender: Gender,
    val dob: String,
    val address: String,
    val active: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
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