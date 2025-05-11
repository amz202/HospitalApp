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