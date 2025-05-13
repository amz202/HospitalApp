package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "patient_details",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class PatientDetailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    // Added new basic user information
    val email: String,
    val fName: String,
    val lName: String,
    // Added contact and personal information
    val phoneNumber: String?,
    val gender: String,
    val dob: String,
    val address: String,
    // Existing medical information
    val bloodGroup: String?,
    val allergies: List<String>,
    val medicalHistory: List<String>,
    // Timestamps
    val createdAt: String,
    val updatedAt: String
)