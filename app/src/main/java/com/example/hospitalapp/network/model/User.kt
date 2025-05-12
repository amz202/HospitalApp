package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val phoneNumber: String?,
    val gender: Gender,
    val dob: String,  // Changed from dateOfBirth
    val address: String,
    val roles: Set<String>,
    val accountCreationDate: String
)

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val email: String,
    val fName: String, // Changed from firstName
    val lName: String, // Changed from lastName
    val phoneNumber: String?,
    val gender: Gender,
    val dob: String,  // Changed from dateOfBirth
    val address: String,
    val role: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
@Serializable
data class LoginResponse(
    val userId: Long,
    val role: String  // Single role as String, not Set<String>
)

enum class Gender {
    MALE, FEMALE, OTHER
}