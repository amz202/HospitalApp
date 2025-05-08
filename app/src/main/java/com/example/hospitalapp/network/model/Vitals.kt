package com.example.hospitalapp.network.model

data class VitalsResponse(
    val id: Long,
    val heartRate: Int?,
    val oxygenLevel: Double?,
    val temperature: Double?,
    val systolicPressure: Int?,
    val diastolicPressure: Int?,
    val bloodSugar: Double?,
    val recordedAt: String,
    val patient: PatientResponse,
    val isCritical: Boolean,
    val criticalNotes: String?,
    val alertSent: Boolean
)

data class VitalsRequest(
    val patientId: Long,
    val heartRate: Int?,
    val oxygenLevel: Double?,
    val temperature: Double?,
    val systolicPressure: Int?,
    val diastolicPressure: Int?,
    val bloodSugar: Double?
)