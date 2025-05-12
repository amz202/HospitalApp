package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PatientRequest(
    val fName: String,
    val lName: String,
    val email: String,
    val phoneNumber: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val medicalHistory: String
)