package com.example.hospitalapp.data

import android.content.Context
import androidx.room.Room
import com.example.hospitalapp.data.local.HospitalDatabase
import com.example.hospitalapp.data.local.dao.UserDao
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
import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.repositories.*

interface AppContainer {
    val patientRepository: PatientRepository
    val adminRepository: AdminRepository
    val feedbackRepository: FeedbackRepository
    val appointmentRepository: AppointmentRepository
    val doctorRepository: DoctorRepository
    val medicationRepository: MedicationRepository
    val vitalsRepository: VitalsRepository
    val reportRepository: ReportRepository
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val BASE_URL = "http://192.168.0.159:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private val database: HospitalDatabase by lazy {
        Room.databaseBuilder(
            context,
            HospitalDatabase::class.java,
            "hospital_database"
        ).build()
    }

    // DAOs
    private val userDao: UserDao by lazy { database.userDao() }
    private val patientDao: PatientDetailDao by lazy { database.patientDetailDao() }
    private val doctorDao: DoctorDetailDao by lazy { database.doctorDetailDao() }
    private val appointmentDao: AppointmentDao by lazy { database.appointmentDao() }
    private val medicationDao: MedicationDao by lazy { database.medicationDao() }
    private val vitalsDao: VitalsDao by lazy { database.vitalsDao() }
    private val reportDao: ReportDao by lazy { database.reportDao() }
    private val feedbackDao: FeedbackDao by lazy { database.feedbackDao() }

    // Repositories
    override val reportRepository: ReportRepository by lazy {
        ReportRepositoryImpl(
            reportDao = reportDao,
            appointmentDao = appointmentDao,
            userDao = userDao,
            vitalsDao = vitalsDao,
            medicationDao = medicationDao,
            feedbackDao = feedbackDao
        )
    }

    override val patientRepository: PatientRepository by lazy {
        PatientRepositoryImpl(
            userDao = userDao,
            patientDetailDao = patientDao,
            appointmentDao = appointmentDao,
            medicationDao = medicationDao,
            vitalsDao = vitalsDao,
            reportDao = reportDao,
            feedbackDao = feedbackDao,
        )
    }

    override val adminRepository: AdminRepository by lazy {
        AdminRepositoryImpl(apiService = apiService)
    }

    override val feedbackRepository: FeedbackRepository by lazy {
        FeedbackRepositoryImpl(
            feedbackDao = feedbackDao,
            userDao = userDao,
            appointmentDao = appointmentDao,
        )
    }

    override val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl(
            appointmentDao = appointmentDao,
            userDao = userDao,
        )
    }

    override val doctorRepository: DoctorRepository by lazy {
        DoctorRepositoryImpl(
            userDao = userDao,
            doctorDetailDao = doctorDao,
            appointmentDao = appointmentDao,
            medicationDao = medicationDao,
            feedbackDao = feedbackDao,
        )
    }

    override val medicationRepository: MedicationRepository by lazy {
        MedicationRepositoryImpl(
            medicationDao = medicationDao,
            userDao = userDao,
        )
    }

    override val vitalsRepository: VitalsRepository by lazy {
        VitalsRepositoryImpl(
            vitalsDao = vitalsDao,
            userDao = userDao,
        )
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userDao = userDao,
            context = context,
        )
    }
}