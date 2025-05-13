package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.DoctorDetailEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.extensions.*
import com.example.hospitalapp.network.model.*
import java.time.LocalDate
import javax.inject.Inject

interface DoctorRepository {
    suspend fun getDoctors(): List<DoctorResponse>
    suspend fun getDoctorById(id: Long): DoctorResponse
    suspend fun createDoctorDetails(userId: Long, request: DoctorRequest): DoctorResponse
    suspend fun updateDoctorDetails(userId: Long, request: DoctorRequest): DoctorResponse
    suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse>
    suspend fun deleteDoctor(id: Long)
    suspend fun getDoctorPatients(id: Long): List<PatientResponse>
}

class DoctorRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val doctorDetailDao: DoctorDetailDao,
    private val patientDetailDao: PatientDetailDao,
    private val appointmentDao: AppointmentDao
) : DoctorRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now().toString()


    override suspend fun getDoctors(): List<DoctorResponse> {
        return userDao.getUsersByRole("DOCTOR").mapNotNull { user ->
            val details = doctorDetailDao.getDoctorDetailByUserId(user.id)
            user.toDoctorResponse(details)
        }
    }

    override suspend fun getDoctorById(id: Long): DoctorResponse {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Doctor not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        val details = doctorDetailDao.getDoctorDetailByUserId(id)
        return user.toDoctorResponse(details)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createDoctorDetails(userId: Long, request: DoctorRequest): DoctorResponse {
        // Verify the user exists and has DOCTOR role
        val user = userDao.getUserById(userId)
            ?: throw IllegalStateException("User not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        val doctorDetail = DoctorDetailEntity(
            userId = userId,
            specialization = request.specialization,
            licenseNumber = request.licenseNumber,
            qualification = request.qualification,
            experienceYears = request.experienceYears,
            consultationFee = request.consultationFee,
            availableForEmergency = request.availableForEmergency,
            createdAt = currentDate,
            updatedAt = currentDate,
            email = request.email,
            fName = request.fName,
            lName = request.lName,
            phoneNumber = request.phoneNumber,
        )

        doctorDetailDao.insertDoctorDetail(doctorDetail)
        return getDoctorById(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateDoctorDetails(userId: Long, request: DoctorRequest): DoctorResponse {
        val user = userDao.getUserById(userId)
            ?: throw IllegalStateException("Doctor not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        val existingDetails = doctorDetailDao.getDoctorDetailByUserId(userId)
            ?: throw IllegalStateException("Doctor details not found")

        val updatedDetails = existingDetails.copy(
            specialization = request.specialization,
            licenseNumber = request.licenseNumber,
            qualification = request.qualification,
            experienceYears = request.experienceYears,
            consultationFee = request.consultationFee,
            availableForEmergency = request.availableForEmergency,
            updatedAt = currentDate
        )

        doctorDetailDao.updateDoctorDetail(updatedDetails)
        return getDoctorById(userId)
    }

    override suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse> {
        val doctorDetails = doctorDetailDao.getDoctorsBySpecialization(specialization)
        return doctorDetails.mapNotNull { details ->
            userDao.getUserById(details.userId)?.let { user ->
                user.toDoctorResponse(details)
            }
        }
    }

    override suspend fun deleteDoctor(id: Long) {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Doctor not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        userDao.deleteUser(user)
        // DoctorDetail will be deleted automatically due to CASCADE
    }

    override suspend fun getDoctorPatients(id: Long): List<PatientResponse> {
        // First verify the doctor exists
        getDoctorById(id)

        return appointmentDao.getDoctorPatients(id).mapNotNull { user ->
            val details = patientDetailDao.getPatientDetailByUserId(user.id)
            user.toPatientResponse(details)
        }.distinct()
    }
}