package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String, // Note: In production, use proper password hashing
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val gender: String,
    val dob: String,
    val address: String,
    val role: String,
    val accountCreationDate: String = System.currentTimeMillis().toString()
)