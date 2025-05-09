package com.example.hospitalapp.network.model

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val roles: Set<String>,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class UserRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val roles: Set<String>
)