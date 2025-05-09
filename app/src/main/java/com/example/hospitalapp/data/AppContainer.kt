package com.example.hospitalapp.data

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.data.repositories.AdminRepository
import com.example.hospitalapp.data.repositories.AdminRepositoryImpl
import com.example.hospitalapp.data.repositories.AppointmentRepository
import com.example.hospitalapp.data.repositories.AppointmentRepositoryImpl
import com.example.hospitalapp.data.repositories.DoctorRepository
import com.example.hospitalapp.data.repositories.DoctorRepositoryImpl
import com.example.hospitalapp.data.repositories.FeedbackRepository
import com.example.hospitalapp.data.repositories.FeedbackRepositoryImpl
import com.example.hospitalapp.data.repositories.MedicationRepository
import com.example.hospitalapp.data.repositories.MedicationRepositoryImpl
import com.example.hospitalapp.data.repositories.PatientRepository
import com.example.hospitalapp.data.repositories.PatientRepositoryImpl
import com.example.hospitalapp.data.repositories.ReportRepository
import com.example.hospitalapp.data.repositories.ReportRepositoryImpl
import com.example.hospitalapp.data.repositories.UserRepository
import com.example.hospitalapp.data.repositories.UserRepositoryImpl
import com.example.hospitalapp.data.repositories.VitalsRepository
import com.example.hospitalapp.data.repositories.VitalsRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val patientRepository: PatientRepository
    val adminRepository: AdminRepository
    val feedbackRepository: FeedbackRepository
    val appointmentRepository: AppointmentRepository
    val doctorRepository: DoctorRepository
    val medicationRepository : MedicationRepository
    val vitalsRepository: VitalsRepository
    val reportRepository: ReportRepository
    val userRepository: UserRepository
}

class DefaultAppContainer:AppContainer {
    val BASE_URL = "http://192.168.208.28:5432/"
    val json = Json {
        this.ignoreUnknownKeys = true
        coerceInputValues = true
    }
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    override val reportRepository: ReportRepository by lazy {
        ReportRepositoryImpl(apiService)
    }
    override val patientRepository: PatientRepository by lazy {
        PatientRepositoryImpl(apiService)
    }
    override val adminRepository: AdminRepository by lazy {
        AdminRepositoryImpl(apiService)
    }

    override val feedbackRepository: FeedbackRepository by lazy {
        FeedbackRepositoryImpl(apiService)
    }

    override val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl(apiService)
    }

    override val doctorRepository: DoctorRepository by lazy {
        DoctorRepositoryImpl(apiService)
    }

    override val medicationRepository: MedicationRepository by lazy {
        MedicationRepositoryImpl(apiService)
    }

    override val vitalsRepository: VitalsRepository by lazy {
        VitalsRepositoryImpl(apiService)
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(apiService)
    }
}