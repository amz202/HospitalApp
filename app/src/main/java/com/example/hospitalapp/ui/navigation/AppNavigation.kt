package com.example.hospitalapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hospitalapp.ui.screens.doctor.DoctorDashboardStateScreen
import com.example.hospitalapp.ui.screens.doctor.detail.DoctorAppointmentDetailScreen
import com.example.hospitalapp.ui.screens.doctor.detail.PatientDetailScreen
import com.example.hospitalapp.ui.screens.patient.AppointmentBookingScreen
import com.example.hospitalapp.ui.screens.patient.PatientDashboardStateScreen
import com.example.hospitalapp.ui.screens.patient.detail.AppointmentDetailScreen
import com.example.hospitalapp.ui.screens.patient.detail.HealthReportScreen
import com.example.hospitalapp.ui.screens.patient.detail.MedicationDetailScreen
import com.example.hospitalapp.ui.screens.patient.detail.VitalsDetailScreen
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.DoctorViewModel
import com.example.hospitalapp.ui.viewModels.MedicationViewModel
import com.example.hospitalapp.ui.viewModels.PatientViewModel
import com.example.hospitalapp.ui.viewModels.VitalsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    patientViewModel: PatientViewModel,
    doctorViewModel: DoctorViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PatientDashboardNav
    ) {

        // Patient Dashboard
        composable<PatientDashboardNav> {
            PatientDashboardStateScreen(
                patientId = 1L,
                navController = navController,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
                patientViewModel = patientViewModel
            )
        }

        // Detail Screens
        composable<VitalsDetailNav> {
            val args = it.toRoute<VitalsDetailNav>()
            VitalsDetailScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                navController = navController
            )
        }

        composable<MedicationDetailNav> {
            val args = it.toRoute<MedicationDetailNav>()
            MedicationDetailScreen(
                patientId = args.patientId,
                medicationViewModel = medicationViewModel,
                navController = navController
            )
        }

        composable<AppointmentDetailNav> {
            val args = it.toRoute<AppointmentDetailNav>()
            AppointmentDetailScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController
            )
        }

        composable<AppointmentBookingScreenNav> { backStackEntry ->
            val args = backStackEntry.toRoute<AppointmentBookingScreenNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController
            )
        }

        composable<HealthReportNav> {
            val args = it.toRoute<HealthReportNav>()
            HealthReportScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
                navController = navController
            )
        }
        // Doctor Dashboard and its routes
        composable<DoctorDashboardNav> {
            DoctorDashboardStateScreen(
                doctorId = 1L, // Replace with actual doctor ID or pass through nav args
                navController = navController,
                doctorViewModel = doctorViewModel,
                appointmentViewModel = appointmentViewModel
            )
        }

        composable<DoctorPatientDetailNav> { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorPatientDetailNav>()
            PatientDetailScreen(
                patientId = args.patientId,
                patientViewModel = patientViewModel,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
                navController = navController
            )
        }

        composable<DoctorAppointmentDetailNav> { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorAppointmentDetailNav>()
            DoctorAppointmentDetailScreen(
                appointmentId = args.appointmentId,
                doctorId = args.doctorId,
                appointmentViewModel = appointmentViewModel,
                navController = navController
            )
        }

        composable<DoctorAppointmentBookingNav> { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorAppointmentBookingNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                isDoctor = true // Add this flag to customize the booking screen for doctors
            )
        }
    }
}