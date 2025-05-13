package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("patientId"),
        Index("appointmentId")
    ]
)
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val appointmentId: Long?,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String?,
    val instructions: String?,
    val active: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)