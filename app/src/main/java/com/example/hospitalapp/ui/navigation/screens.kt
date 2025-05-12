package com.example.hospitalapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SignInScreenNav

@Serializable
object PatientDashboardNav

@Serializable
data class VitalsDetailNav(
    val patientId: Long
)

@Serializable
data class MedicationDetailNav(
    val patientId: Long
)

@Serializable
data class AppointmentDetailNav(
    val patientId: Long
)

@Serializable
data class HealthReportNav(
    val patientId: Long
)

@Serializable
data class AppointmentBookingScreenNav(
    val patientId: Long
)

@Serializable
object DoctorDashboardNav

@Serializable
data class DoctorPatientDetailNav(
    val patientId: Long
)

@Serializable
data class DoctorAppointmentDetailNav(
    val appointmentId: Long,
    val doctorId: Long
)

@Serializable
data class DoctorAppointmentBookingNav(
    val patientId: Long,
    val doctorId: Long
)

@Serializable
object LoginScreenNav

@Serializable
object SignupScreenNav

@Serializable
data class DoctorInfoNav(
    val doctorId: Long
)

@Serializable
data class PatientInfoNav(
    val patientId: Long
)