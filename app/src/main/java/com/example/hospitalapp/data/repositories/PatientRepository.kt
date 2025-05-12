package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.PatientDetailEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.extensions.*
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PatientRepository {
    suspend fun getAllPatients(): List<PatientResponse>
    suspend fun getPatientById(id: Long): PatientResponse
    suspend fun registerPatient(request: PatientRequest): PatientResponse
    suspend fun updatePatient(id: Long, request: PatientRequest): PatientResponse
    suspend fun deletePatient(id: Long)

    // Related data retrieval
    suspend fun getPatientAppointments(id: Long): List<AppointmentResponse>
    suspend fun getPatientMedications(id: Long): List<MedicationResponse>
    fun getPatientVitals(id: Long): Flow<List<VitalsResponse>>
    suspend fun getPatientReports(id: Long): List<ReportResponse>
    suspend fun getPatientFeedback(id: Long): List<FeedbackResponse>
}

class PatientRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao,
    private val appointmentDao: AppointmentDao,
    private val medicationDao: MedicationDao,
    private val vitalsDao: VitalsDao,
    private val reportDao: ReportDao,
    private val feedbackDao: FeedbackDao
) : PatientRepository {

    private val currentDate = "2025-05-12 14:42:58" // Current UTC time
    private val currentUser = "amz202" // Current user's login

    override suspend fun getAllPatients(): List<PatientResponse> {
        return userDao.getUsersByRole("PATIENT").mapNotNull { user ->
            val details = patientDetailDao.getPatientDetailByUserId(user.id)
            user.toPatientResponse().copy(
                bloodGroup = details?.bloodGroup,
                allergies = details?.allergies ?: emptyList(),
                medicalHistory = details?.medicalHistory ?: emptyList()
            )
        }
    }

    override suspend fun getPatientById(id: Long): PatientResponse {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Patient not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        val details = patientDetailDao.getPatientDetailByUserId(user.id)
        return user.toPatientResponse().copy(
            bloodGroup = details?.bloodGroup,
            allergies = details?.allergies ?: emptyList(),
            medicalHistory = details?.medicalHistory ?: emptyList()
        )
    }

    override suspend fun registerPatient(request: PatientRequest): PatientResponse {
        // Validate unique constraints
        if (userDao.getUserByUsername(request.username) != null) {
            throw IllegalStateException("Username already exists")
        }
        if (userDao.getUserByEmail(request.email) != null) {
            throw IllegalStateException("Email already exists")
        }

        // Create user entity
        val userEntity = UserEntity(
            username = request.username,
            password = request.password, // Note: Should be hashed before storing
            email = request.email,
            fName = request.fName,
            lName = request.lName,
            phoneNumber = request.phoneNumber,
            gender = request.gender,
            dob = request.dob,
            address = request.address,
            role = "PATIENT",
            accountCreationDate = currentDate
        )

        val userId = userDao.insertUser(userEntity)

        // Create patient details
        val patientDetail = PatientDetailEntity(
            userId = userId,
            bloodGroup = request.bloodGroup,
            allergies = request.allergies,
            medicalHistory = request.medicalHistory,
            createdAt = currentDate,
            updatedAt = currentDate
        )

        patientDetailDao.insertPatientDetail(patientDetail)
        return getPatientById(userId)
    }

    override suspend fun updatePatient(id: Long, request: PatientRequest): PatientResponse {
        val existingUser = userDao.getUserById(id)
            ?: throw IllegalStateException("Patient not found")

        if (existingUser.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        // Check username and email uniqueness
        userDao.getUserByUsername(request.username)?.let {
            if (it.id != id) throw IllegalStateException("Username already exists")
        }
        userDao.getUserByEmail(request.email)?.let {
            if (it.id != id) throw IllegalStateException("Email already exists")
        }

        // Update user data
        val updatedUser = existingUser.copy(
            username = request.username,
            email = request.email,
            fName = request.fName,
            lName = request.lName,
            phoneNumber = request.phoneNumber,
            gender = request.gender,
            dob = request.dob,
            address = request.address
        )
        userDao.updateUser(updatedUser)

        // Update or create patient details
        val existingDetails = patientDetailDao.getPatientDetailByUserId(id)
        val patientDetail = PatientDetailEntity(
            id = existingDetails?.id ?: 0,
            userId = id,
            bloodGroup = request.bloodGroup,
            allergies = request.allergies,
            medicalHistory = request.medicalHistory,
            createdAt = existingDetails?.createdAt ?: currentDate,
            updatedAt = currentDate
        )
        patientDetailDao.insertPatientDetail(patientDetail)

        return getPatientById(id)
    }

    override suspend fun deletePatient(id: Long) {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Patient not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        userDao.deleteUser(user)
        // PatientDetail will be deleted automatically due to CASCADE
    }

    override suspend fun getPatientAppointments(id: Long): List<AppointmentResponse> {
        return appointmentDao.getPatientAppointments(id).map {
            it.toAppointmentResponse(userDao)
        }
    }

    override suspend fun getPatientMedications(id: Long): List<MedicationResponse> {
        return medicationDao.getPatientMedications(id).map {
            it.toMedicationResponse(userDao)
        }
    }

    override fun getPatientVitals(id: Long): Flow<List<VitalsResponse>> {
        return vitalsDao.getVitalsByPatient(id).map { vitalsList ->
            vitalsList.map { it.toVitalsResponse(userDao) }
        }
    }

    override suspend fun getPatientReports(id: Long): List<ReportResponse> {
        return reportDao.getReportsByPatient(id).map {
            it.toReportResponse(userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao)
        }
    }

    override suspend fun getPatientFeedback(id: Long): List<FeedbackResponse> {
        return feedbackDao.getFeedbacksByPatient(id).map {
            it.toFeedbackResponse(userDao, appointmentDao)
        }
    }
}