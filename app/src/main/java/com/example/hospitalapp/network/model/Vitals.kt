package com.example.hospitalapp.network.model

data class VitalsResponse(
    val id: Long,
    val patientId: Long,
    val heartRate: Int?,
    val bloodPressureSystolic: Int?,
    val bloodPressureDiastolic: Int?,
    val temperature: Double?,
    val oxygenSaturation: Double?,
    val respiratoryRate: Int?,
    val recordedAt: String,
    val notes: String?,
    val isCritical: Boolean,
    val alertSent: Boolean
)

data class VitalsRequest(
    val patientId: Long,
    val heartRate: Int?,
    val bloodPressureSystolic: Int?,
    val bloodPressureDiastolic: Int?,
    val temperature: Double?,
    val oxygenSaturation: Double?,
    val respiratoryRate: Int?,
    val notes: String?
)