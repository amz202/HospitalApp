package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DoctorResponse(
    val id: Long,
    val role: String,
    val fName: String,
    val lName: String,
    val accountCreationDate: String,
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean
)

@Serializable
data class DoctorRequest(
    val fName: String,
    val lName: String,
    val email: String,
    val phoneNumber: String,
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean = true,
    val id: Long? = null,
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