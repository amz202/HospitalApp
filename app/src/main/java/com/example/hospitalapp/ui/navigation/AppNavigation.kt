package com.example.hospitalapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.ui.screens.doctor.DoctorDashboardStateScreen
import com.example.hospitalapp.ui.screens.doctor.detail.DoctorAppointmentDetailScreen
import com.example.hospitalapp.ui.screens.doctor.detail.PatientDetailScreen
import com.example.hospitalapp.ui.screens.patient.AppointmentBookingScreen
import com.example.hospitalapp.ui.screens.patient.PatientDashboardStateScreen
import com.example.hospitalapp.ui.screens.patient.detail.AppointmentDetailScreen
import com.example.hospitalapp.ui.screens.patient.detail.HealthReportScreen
import com.example.hospitalapp.ui.screens.patient.detail.MedicationDetailScreen
import com.example.hospitalapp.ui.screens.patient.detail.VitalsDetailScreen
import com.example.hospitalapp.ui.signin.LoginScreen
import com.example.hospitalapp.ui.signin.SignupScreen
import com.example.hospitalapp.ui.viewModels.AppointmentViewModel
import com.example.hospitalapp.ui.viewModels.DoctorViewModel
import com.example.hospitalapp.ui.viewModels.MedicationViewModel
import com.example.hospitalapp.ui.viewModels.PatientViewModel
import com.example.hospitalapp.ui.viewModels.UserViewModel
import com.example.hospitalapp.ui.viewModels.VitalsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    vitalsViewModel: VitalsViewModel,
    medicationViewModel: MedicationViewModel,
    appointmentViewModel: AppointmentViewModel,
    patientViewModel: PatientViewModel,
    doctorViewModel: DoctorViewModel,
    userViewModel: UserViewModel,
    userPreferences: UserPreferences,
) {
    val navController = rememberNavController()
    var userInfo by remember { mutableStateOf<UserPreferences.UserInfo?>(null) }
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        userInfo = userPreferences.getUser()
    }
    NavHost(
        navController = navController,
        startDestination = if (userInfo != null) PatientDashboardNav else LoginScreenNav
    ) {
        // Auth screens
        composable<LoginScreenNav> {
            LoginScreen(
                userViewModel = userViewModel,
                onSignUpClick = { navController.navigate(SignupScreenNav) },
                onLoginSuccess = {
                    navController.navigate(PatientDashboardNav) {
                        popUpTo(LoginScreenNav) { inclusive = true }
                    }
                }
            )
        }

        composable<SignupScreenNav> {
            SignupScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.navigateUp() },
                onSignUpSuccess = {
                    navController.navigate(PatientDashboardNav) {
                        popUpTo(LoginScreenNav) { inclusive = true }
                    }
                }
            )
        }

        // Patient Dashboard
        composable<PatientDashboardNav> {
            val userId = currentUser?.id ?: return@composable
            PatientDashboardStateScreen(
                patientId = userId,
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
            val userId = currentUser?.id ?: return@composable
            DoctorDashboardStateScreen(
                doctorId = userId,
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
            )
        }
    }
}