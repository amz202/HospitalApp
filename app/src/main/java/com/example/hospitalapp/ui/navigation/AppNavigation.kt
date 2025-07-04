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
import com.example.hospitalapp.ui.screens.admin.AdminDashboardScreen
import com.example.hospitalapp.ui.screens.doctor.*
import com.example.hospitalapp.ui.screens.doctor.detail.*
import com.example.hospitalapp.ui.screens.patient.*
import com.example.hospitalapp.ui.screens.patient.chart.VitalsChartScreen
import com.example.hospitalapp.ui.screens.patient.detail.*
import com.example.hospitalapp.ui.signin.*
import com.example.hospitalapp.ui.viewModels.*
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userInfo = userPreferences.getUser()

        userInfo?.let { user ->
            when (user.role) {
                "PATIENT" -> navController.navigate(PatientDashboardNav(user.id)) {
                    popUpTo(LoginScreenNav) { inclusive = true }
                }
                "DOCTOR" -> navController.navigate(DoctorDashboardNav(user.id)) {
                    popUpTo(LoginScreenNav) { inclusive = true }
                }
                "ADMIN" -> navController.navigate(AdminDashboardNav) {
                    popUpTo(LoginScreenNav) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = LoginScreenNav
    ) {
        composable<LoginScreenNav> {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { role ->
                    coroutineScope.launch {
                        val userId = userPreferences.getUserId()
                        userId?.let {
                            when (role) {
                                "PATIENT" -> navController.navigate(PatientDashboardNav(userId)) {
                                    popUpTo(LoginScreenNav) { inclusive = true }
                                }
                                "DOCTOR" -> navController.navigate(DoctorDashboardNav(userId)) {
                                    popUpTo(LoginScreenNav) { inclusive = true }
                                }
                                "ADMIN" -> navController.navigate(AdminDashboardNav) {
                                    popUpTo(LoginScreenNav) { inclusive = true }
                                }
                                else -> Log.e("Navigation", "Unknown role: $role")
                            }
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(SignupScreenNav)
                }
            )
        }


        composable<SignupScreenNav> {
            SignupScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.navigateUp() },
                onSignUpSuccess = { role, userId ->
                    when (role) {
                        "PATIENT" -> {
                            navController.navigate(PatientInfoNav(userId)) {
                                popUpTo(SignupScreenNav) { inclusive = true }
                            }
                        }
                        "DOCTOR" -> {
                            navController.navigate(DoctorInfoNav(userId)) {
                                popUpTo(SignupScreenNav) { inclusive = true }
                            }
                        }
                        "ADMIN" -> navController.navigate(AdminDashboardNav) {
                            popUpTo(LoginScreenNav) { inclusive = true }
                        }
                        else -> Log.e("Navigation", "Unknown role: $role")
                    }
                }
            )
        }

        // Info Screens
        composable<DoctorInfoNav> {
            val args = it.toRoute<DoctorInfoNav>()
            DoctorInfoScreen(
                viewModel = doctorViewModel,
                doctorId = args.doctorId,
                navController = navController
            )
        }

        composable<PatientInfoNav> {
            val args = it.toRoute<PatientInfoNav>()
            PatientInfoScreen(
                viewModel = patientViewModel,
                patientId = args.patientId,
                navController = navController,
            )
        }

        // Patient Routes
        composable<PatientDashboardNav> {
            val args = it.toRoute<PatientDashboardNav>()
            PatientDashboardScreen(
                patientId = args.patientId,
                navController = navController,
                patientViewModel = patientViewModel,
                vitalsViewModel = vitalsViewModel,
                medicationViewModel = medicationViewModel,
                appointmentViewModel = appointmentViewModel
            )
        }

        composable<VitalsDetailNav> {
            val args = it.toRoute<VitalsDetailNav>()
            VitalsDetailScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                navController = navController,
            )
        }

        composable<MedicationDetailNav> {
            val args = it.toRoute<MedicationDetailNav>()
            MedicationDetailScreen(
                patientId = args.patientId,
                medicationViewModel = medicationViewModel,
                navController = navController,
            )
        }

        composable<AppointmentDetailNav> {
            val args = it.toRoute<AppointmentDetailNav>()
            AppointmentDetailScreen(
                appointmentId = args.appointmentId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
            )
        }

        composable<AppointmentBookingScreenNav> {
            val args = it.toRoute<AppointmentBookingScreenNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                doctorId = args.doctorId
            )
        }

        composable<DoctorSelectionNav> {
            val args = it.toRoute<DoctorSelectionNav>()
            DoctorSelectionScreen(
                patientId = args.patientId,
                doctorViewModel = doctorViewModel,
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
                navController = navController,
                patientViewModel
            )
        }

        // Doctor Routes
        composable<DoctorDashboardNav> {
            val args = it.toRoute<DoctorDashboardNav>()
            DoctorDashboardScreen(
                doctorId = args.doctorId,
                navController = navController,
                doctorViewModel = doctorViewModel,
            )
        }

        composable<DoctorPatientDetailNav> {
            val args = it.toRoute<DoctorPatientDetailNav>()
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

        composable<DoctorAppointmentDetailNav> {
            val args = it.toRoute<DoctorAppointmentDetailNav>()
            DoctorAppointmentDetailScreen(
                appointmentId = args.appointmentId,
                doctorId = args.doctorId,
                viewModel = appointmentViewModel,
                navController = navController,
            )
        }

        composable<DoctorAppointmentBookingNav> {
            val args = it.toRoute<DoctorAppointmentBookingNav>()
            AppointmentBookingScreen(
                patientId = args.patientId,
                appointmentViewModel = appointmentViewModel,
                navController = navController,
                doctorId = args.doctorId
            )
        }

        composable<VitalsChartNav> { route ->
            val args = route.toRoute<VitalsChartNav>()
            VitalsChartScreen(
                patientId = args.patientId,
                vitalsViewModel = vitalsViewModel,
                navController = navController
            )
        }

        composable<AdminDashboardNav> {
            AdminDashboardScreen(
                userViewModel = userViewModel,
                userPreferences = userPreferences,
                navController = navController
            )
        }
    }
}