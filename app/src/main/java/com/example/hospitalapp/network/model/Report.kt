package com.example.hospitalapp.network.model

data class ReportResponse(
    val id: Long,
    val title: String,
    val generatedAt: String,
    val patient: PatientResponse,
    val doctor: DoctorResponse?,
    val summary: String,
    val reportType: String,
    val vitalsList: List<VitalsResponse>,
    val medications: List<MedicationResponse>,
    val feedbacks: List<FeedbackResponse>,
    val appointments: List<AppointmentResponse>,
    val filePath: String?,
    val timePeriodStart: String?,
    val timePeriodEnd: String?
)

data class ReportRequest(
    val title: String,
    val patientId: Long,
    val doctorId: Long?,
    val summary: String,
    val reportType: String,
    val vitalsIds: List<Long>,
    val medicationIds: List<Long>,
    val feedbackIds: List<Long>,
    val appointmentIds: List<Long>,
    val timePeriodStart: String?,
    val timePeriodEnd: String?
)