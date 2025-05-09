package com.example.hospitalapp.network.model

data class PatientResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val active: Boolean,
    val createdAt: String,
    val primaryDoctor: DoctorResponse?,
    val vitalsList: List<VitalsResponse> = emptyList(),
    val appointments: List<AppointmentResponse> = emptyList(),
    val medications: List<MedicationResponse> = emptyList(),
    val reports: List<ReportResponse> = emptyList()
)

data class PatientRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val primaryDoctorId: Long?
)