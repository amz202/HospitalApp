package com.example.hospitalapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SignInScreenNav

@Serializable
data class PatientDashboardNav(
    val patientId: Long
)


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
    val appointmentId: Long
)

@Serializable
data class HealthReportNav(
    val patientId: Long
)

@Serializable
data class AppointmentBookingScreenNav(
    val patientId: Long,
    val doctorId: Long
)

@Serializable
data class DoctorDashboardNav(
    val doctorId: Long
)

@Serializable
data class DoctorPatientDetailNav(
    val patientId: Long,
    val doctorId: Long
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