package com.example.hospitalapp.network.model

data class AdminResponse(
    val id: Long,
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val department: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class AdminRequest(
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val department: String,
    val password: String
)

data class SystemStats(
    val totalPatientsCount: Long,
    val totalDoctorsCount: Long,
    val totalAppointmentsCount: Long,
    val pendingAppointmentsCount: Long
)