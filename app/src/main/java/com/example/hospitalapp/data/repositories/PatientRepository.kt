package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.PatientDetailEntity
import com.example.hospitalapp.data.local.extensions.*
import com.example.hospitalapp.network.model.*
import java.time.LocalDate
import javax.inject.Inject
import kotlin.toString

interface PatientRepository {
    suspend fun getAllPatients(): List<PatientResponse>
    suspend fun getPatientById(id: Long): PatientResponse
    suspend fun createPatientDetails(userId: Long, request: PatientRequest): PatientResponse
    suspend fun updatePatientDetails(userId: Long, request: PatientUpdateRequest): PatientResponse
    suspend fun deletePatient(id: Long)
    suspend fun getPatientAppointments(id: Long): List<AppointmentResponse>
}

class PatientRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao,
    private val doctorDetailDao: DoctorDetailDao,
    private val appointmentDao: AppointmentDao
) : PatientRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now()


    override suspend fun getAllPatients(): List<PatientResponse> {
        return userDao.getUsersByRole("PATIENT").mapNotNull { user ->
            val details = patientDetailDao.getPatientDetailByUserId(user.id)
            user.toPatientResponse(details)
        }
    }

    override suspend fun getPatientById(id: Long): PatientResponse {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Patient not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        val details = patientDetailDao.getPatientDetailByUserId(id)
        return user.toPatientResponse(details)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createPatientDetails(userId: Long, request: PatientRequest): PatientResponse {
        val user = userDao.getUserById(userId)
            ?: throw IllegalStateException("User not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        val patientDetail = PatientDetailEntity(
            userId = userId,
            bloodGroup = request.bloodGroup,
            allergies = request.allergies,
            medicalHistory = request.medicalHistory,
            createdAt = currentDate.toString(),
            updatedAt = currentDate.toString(),
            email = request.email,
            fName = request.fName,
            lName = request.lName,
            phoneNumber = request.phoneNumber,
            gender = request.gender,
            dob = request.dob,
            address = request.address
        )

        patientDetailDao.insertPatientDetail(patientDetail)
        return getPatientById(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updatePatientDetails(userId: Long, request: PatientUpdateRequest): PatientResponse {
        val user = userDao.getUserById(userId)
            ?: throw IllegalStateException("Patient not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        val existingDetails = patientDetailDao.getPatientDetailByUserId(userId)
            ?: throw IllegalStateException("Patient details not found")

        val updatedDetails = existingDetails.copy(
            bloodGroup = request.bloodGroup,
            allergies = request.allergies?.split(",")?.map { it.trim() } ?: emptyList(),
            medicalHistory = request.medicalHistory,
            updatedAt = LocalDate.now().toString()
        )

        patientDetailDao.updatePatientDetail(updatedDetails)
        return getPatientById(userId)
    }

    override suspend fun deletePatient(id: Long) {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Patient not found")

        if (user.role != "PATIENT") {
            throw IllegalStateException("User is not a patient")
        }

        userDao.deleteUser(user)
    }

    override suspend fun getPatientAppointments(id: Long): List<AppointmentResponse> {
        getPatientById(id)

        return appointmentDao.getPatientAppointments(id).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }
}