package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class VitalsResponse(
    val id: Long,
    val patient: PatientResponse,
    val heartRate: Int?,
    val systolicPressure: Int?,
    val diastolicPressure: Int?,
    val temperature: Double?,
    val oxygenSaturation: Double?,
    val respiratoryRate: Int?,
    val bloodSugar: Double?,
    val recordedAt: String,
    val critical: Boolean,
    val criticalNotes: String?,
    val alertSent: Boolean
)
@Serializable
data class VitalsRequest(
    val patientId: Long,
    val heartRate: Int?,
    val systolicPressure: Int?,
    val diastolicPressure: Int?,
    val temperature: Double?,
    val oxygenSaturation: Double?,
    val respiratoryRate: Int?,
    val bloodSugar: Double?
)