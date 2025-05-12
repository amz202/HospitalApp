package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val fName: String?,
    val lName: String?,
    val phoneNumber: String?,
    val gender: String?,
    val dob: String?,
    val address: String?,
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
    val role: String  // Single role string as returned by backend
)

enum class Gender {
    MALE, FEMALE, OTHER
}

@Serializable
data class SignupRequest(
    val username: String,
    val password: String,
    val email: String,
    val roles: Set<String> = setOf("PATIENT")
)