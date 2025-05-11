package com.example.hospitalapp.network.model

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val gender: Gender,
    val dateOfBirth: String,
    val address: String,
    val roles: Set<String>,
    val accountCreationDate: String
)

data class CreateUserRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val gender: Gender,
    val dateOfBirth: String,
    val address: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val userId: Long
)

enum class Gender {
    MALE, FEMALE, OTHER
}