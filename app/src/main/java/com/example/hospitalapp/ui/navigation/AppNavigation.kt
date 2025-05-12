package com.example.hospitalapp.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.ui.screens.doctor.*
import com.example.hospitalapp.ui.screens.doctor.detail.*
import com.example.hospitalapp.ui.screens.patient.*
import com.example.hospitalapp.ui.screens.patient.detail.*
import com.example.hospitalapp.ui.signin.*
import com.example.hospitalapp.ui.viewModels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    val currentUserState by userViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        userInfo = userPreferences.getUser()
    }

    NavHost(
        navController = navController,
        startDestination = if (userInfo != null) {
            when (userInfo?.role) {
                "PATIENT" -> PatientDashboardNav.toString()
                "DOCTOR" -> DoctorDashboardNav.toString()
                else -> LoginScreenNav.toString()
            }
        } else LoginScreenNav.toString()
    ) {
        // Auth screens
        composable(toString()) {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { role ->
                    when (role) {
                        "PATIENT" -> navController.navigate(toString()) {
                            popUpTo(toString()) { inclusive = true }
                        }
                        "DOCTOR" -> navController.navigate(toString()) {
                            popUpTo(toString()) { inclusive = true }
                        }
                        else -> Log.e("Navigation", "Unknown role: $role")
                    }
                },
                onSignUpClick = {
                    navController.navigate(toString())
                }
            )
        }

        composable(toString()) {
            SignupScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.navigateUp() },
                onSignUpSuccess = { role, userId ->
                    when (role) {
                        "PATIENT" -> {
                            navController.navigate("patient_info/$userId") {
                                popUpTo(toString()) { inclusive = true }
                            }
                        }
                        "DOCTOR" -> {
                            navController.navigate("doctor_info/$userId") {
                                popUpTo(toString()) { inclusive = true }
                            }
                        }
                        else -> Log.e("Navigation", "Unknown role: $role")
                    }
                }
            )
        }

        // Info Screens
        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorInfoNav>()
            DoctorInfoScreen(
                viewModel = doctorViewModel,
                doctorId = args.doctorId,
                onProfileUpdated = {
                    navController.navigate(toString()) {
                        popUpTo(toString()) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<PatientInfoNav>()
            PatientInfoScreen(
                viewModel = patientViewModel,
                patientId = args.patientId,
                onProfileUpdated = {
                    navController.navigate(toString()) {
                        popUpTo(toString()) { inclusive = true }
                    }
                }
            )
        }

        // Patient Routes
        composable(toString()) {
            val userId = currentUserState?.id ?: return@composable
            PatientDashboardScreen(
                patientId = userId,
                navController = navController,
                patientViewModel = patientViewModel,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<VitalsDetailNav>()
            VitalsDetailScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                navController = navController,
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<MedicationDetailNav>()
            MedicationDetailScreen(
                patientId = args.patientId,
                medicationViewModel = medicationViewModel,
                navController = navController,
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<AppointmentDetailNav>()
            AppointmentDetailScreen(
                appointmentId = args.appointmentId, // This should be appointmentId, need to update the nav class
                appointmentViewModel = appointmentViewModel,
                navController = navController,
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<AppointmentBookingScreenNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                doctorId = args.doctorId
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<HealthReportNav>()
            HealthReportScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
            )
        }

        // Doctor Routes
        composable(toString()) {
            val userId = currentUserState?.id ?: return@composable
            DoctorDashboardScreen(
                doctorId = userId,
                navController = navController,
                doctorViewModel = doctorViewModel,
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorPatientDetailNav>()
            PatientDetailScreen(
                patientId = args.patientId,
                patientViewModel = patientViewModel,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                doctorId = args.doctorId
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorAppointmentDetailNav>()
            DoctorAppointmentDetailScreen(
                appointmentId = args.appointmentId,
                doctorId = args.doctorId,
                viewModel = appointmentViewModel,
                navController = navController,
            )
        }

        composable(toString()) { backStackEntry ->
            val args = backStackEntry.toRoute<DoctorAppointmentBookingNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                doctorId = args.doctorId
            )
        }
    }
}