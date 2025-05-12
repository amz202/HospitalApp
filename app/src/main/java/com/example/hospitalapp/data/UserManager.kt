package com.example.hospitalapp.data

import kotlinx.serialization.Serializable

object UserManager {
    private var _currentUser: UserData? = null
    val currentUser: UserData?
        get() = _currentUser

    fun updateUser(userData: UserData) {
        _currentUser = userData
    }

    fun clearUser() {
        _currentUser = null
    }
}

@Serializable
data class UserData(
    val id: Long,
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val dob: String,
    val role: String
)