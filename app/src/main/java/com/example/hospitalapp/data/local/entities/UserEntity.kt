package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String,
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val accountCreationDate: String
)