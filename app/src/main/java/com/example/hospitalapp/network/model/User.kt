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
    val username: String,
    val role: String
)

enum class Gender {
    MALE, FEMALE, OTHER
}

data class SignupRequest(
    val username: String,
    val password: String,
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val role: String,
    // Doctor specific fields
    val specialization: String? = null,
    val licenseNumber: String? = null,
    val experienceYears: Int? = null,
    // Patient specific fields
    val bloodGroup: String? = null,
    val allergies: List<String>? = null
)