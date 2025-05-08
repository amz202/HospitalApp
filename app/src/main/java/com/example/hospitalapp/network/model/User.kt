package com.example.hospitalapp.network.model

data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val roles: List<String>,
    val active: Boolean,
    val createdAt: String
)

data class UserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)