package com.example.hospitalapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val id: Long,
    val title: String,
    val generatedAt: String,
    val patient: PatientResponse,
    val doctor: DoctorResponse?,
    val summary: String,
    val reportType: String,
    val vitals: VitalsResponse?,
    val medications: List<MedicationResponse> = emptyList(),
    val feedback: FeedbackResponse?,
    val appointment: AppointmentResponse?,
    val filePath: String?,
    val timePeriodStart: String?,
    val timePeriodEnd: String?
)
@Serializable
data class ReportRequest(
    val title: String,
    val patientId: Long,
    val doctorId: Long?,
    val summary: String,
    val reportType: String,
    val appointmentId: Long,
    val vitalsId: Long? = null,
    val medicationIds: List<Long> = emptyList(),
    val feedbackId: Long? = null,
    val timePeriodStart: String? = null,
    val timePeriodEnd: String? = null
)