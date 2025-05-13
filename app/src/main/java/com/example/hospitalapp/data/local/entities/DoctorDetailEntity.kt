package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "doctor_details",
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
data class DoctorDetailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val specialization: String,
    val licenseNumber: String,
    val qualification: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val availableForEmergency: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val email: String,
    val fName: String,
    val lName: String,
    val phoneNumber: String?
)