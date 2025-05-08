package com.example.hospitalapp.network.model

data class AdminResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val department: String,
    val position: String,
    val adminLevel: Int,
    val employeeId: String,
    val active: Boolean,
    val createdAt: String
)

data class AdminRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val department: String,
    val position: String,
    val adminLevel: Int,
    val employeeId: String
)