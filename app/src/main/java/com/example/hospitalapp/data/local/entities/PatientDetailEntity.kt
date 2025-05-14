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
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?,
    val gender: String,
    val dob: String,
    val address: String,
    val bloodGroup: String?,
    val allergies: List<String>,
    val medicalHistory: List<String>,
    val createdAt: String,
    val updatedAt: String
)