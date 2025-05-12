package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["id"]
)
data class AppointmentEntity(
    val id: Long = 0,
    val patientId: Long,
    val doctorId: Long,
    val scheduledTime: String,
    val status: String,
    val type: String,
    val notes: String?,
    val reason: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String
)