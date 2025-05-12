package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminResponse(
    val id: Long,
    val employeeId: String,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val email: String,
    val phoneNumber: String,
    val department: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class AdminRequest(
    val employeeId: String,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val email: String,
    val phoneNumber: String,
    val department: String,
    val password: String
)
@Serializable
data class SystemStats(
    val totalPatientsCount: Long,
    val totalDoctorsCount: Long,
    val totalAppointmentsCount: Long,
    val pendingAppointmentsCount: Long
)