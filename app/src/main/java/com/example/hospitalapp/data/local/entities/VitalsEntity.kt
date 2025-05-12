package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vitals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VitalsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val heartRate: Int?,
    val systolicPressure: Int?,
    val diastolicPressure: Int?,
    val temperature: Double?,
    val oxygenSaturation: Double?,
    val respiratoryRate: Int?,
    val bloodSugar: Double?,
    val recordedAt: String,
    val critical: Boolean = false,
    val criticalNotes: String? = null,
    val alertSent: Boolean = false
)