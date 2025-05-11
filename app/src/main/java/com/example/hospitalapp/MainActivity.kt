package com.example.hospitalapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.ui.navigation.AppNavigation
import com.example.hospitalapp.ui.theme.HospitalAppTheme
import com.example.hospitalapp.ui.viewModels.*

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)

        // Initialize ViewModels
        val userViewModel: UserViewModel = ViewModelProvider(
            this,
            UserViewModel.Factory
        )[UserViewModel::class.java]

        val vitalsViewModel: VitalsViewModel = ViewModelProvider(
            this,
            VitalsViewModel.Factory
        )[VitalsViewModel::class.java]

        val medicationViewModel: MedicationViewModel = ViewModelProvider(
            this,
            MedicationViewModel.Factory
        )[MedicationViewModel::class.java]

        val appointmentViewModel: AppointmentViewModel = ViewModelProvider(
            this,
            AppointmentViewModel.Factory
        )[AppointmentViewModel::class.java]

        val patientViewModel: PatientViewModel = ViewModelProvider(
            this,
            PatientViewModel.Factory
        )[PatientViewModel::class.java]

        val doctorViewModel: DoctorViewModel = ViewModelProvider(
            this,
            DoctorViewModel.Factory
        )[DoctorViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            HospitalAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    AppNavigation(
                        vitalsViewModel = vitalsViewModel,
                        medicationViewModel = medicationViewModel,
                        appointmentViewModel = appointmentViewModel,
                        patientViewModel = patientViewModel,
                        doctorViewModel = doctorViewModel,
                        userViewModel = userViewModel,
                        userPreferences = userPreferences,
                        padding
                    )
                }
            }
        }
    }
}
