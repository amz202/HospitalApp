package com.example.hospitalapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reports",
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
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = VitalsEntity::class,
            parentColumns = ["id"],
            childColumns = ["vitalsId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FeedbackEntity::class,
            parentColumns = ["id"],
            childColumns = ["feedbackId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("patientId"),
        Index("doctorId"),
        Index("appointmentId"),
        Index("vitalsId"),
        Index("feedbackId")
    ]
)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val generatedAt: String,
    val patientId: Long,
    val doctorId: Long?,
    val summary: String,
    val reportType: String,
    val appointmentId: Long?,
    val vitalsId: Long?,
    val feedbackId: Long?,
    val filePath: String?,
    val timePeriodStart: String?,
    val timePeriodEnd: String?
)