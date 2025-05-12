package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.AppointmentDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.network.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface AppointmentRepository {
    suspend fun getAppointments(): List<AppointmentResponse>
    suspend fun getAppointmentById(id: Long): AppointmentResponse
    suspend fun createAppointment(appointment: AppointmentRequest): AppointmentResponse
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse>
    suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse>
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse>
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse
}

class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao
) : AppointmentRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun getAppointments(): List<AppointmentResponse> {
        return appointmentDao.getAllAppointments().map { it.toAppointmentResponse() }
    }

    override suspend fun getAppointmentById(id: Long): AppointmentResponse {
        return appointmentDao.getAppointmentById(id)?.toAppointmentResponse()
            ?: throw IllegalStateException("Appointment not found")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createAppointment(appointment: AppointmentRequest): AppointmentResponse {
        val currentTime = LocalDateTime.now().format(dateFormatter)
        val appointmentEntity = AppointmentEntity(
            patientId = appointment.patientId,
            doctorId = appointment.doctorId,
            scheduledTime = appointment.scheduledTime,
            status = AppointmentStatus.PENDING.name,
            type = appointment.type,
            notes = appointment.notes,
            reason = appointment.reason,
            meetingLink = null, // Will be set when appointment is confirmed
            createdAt = currentTime,
            updatedAt = currentTime
        )

        val id = appointmentDao.insertAppointment(appointmentEntity)
        return getAppointmentById(id)
    }

    override suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse> {
        return appointmentDao.getPatientAppointments(patientId).map { it.toAppointmentResponse() }
    }

    override suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse> {
        return appointmentDao.getDoctorAppointments(doctorId).map { it.toAppointmentResponse() }
    }

    override suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse> {
        return appointmentDao.getAppointmentsByStatus(status.name).map { it.toAppointmentResponse() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse> {
        val currentTime = LocalDateTime.now()
        return appointmentDao.getPatientAppointments(patientId)
            .filter {
                LocalDateTime.parse(it.scheduledTime, dateFormatter).isAfter(currentTime)
            }
            .map { it.toAppointmentResponse() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse> {
        val currentTime = LocalDateTime.now()
        return appointmentDao.getDoctorAppointments(doctorId)
            .filter {
                LocalDateTime.parse(it.scheduledTime, dateFormatter).isAfter(currentTime)
            }
            .map { it.toAppointmentResponse() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse {
        val appointment = appointmentDao.getAppointmentById(id)
            ?: throw IllegalStateException("Appointment not found")

        val updatedAppointment = appointment.copy(
            status = status.name,
            updatedAt = LocalDateTime.now().format(dateFormatter)
        )
        appointmentDao.updateAppointment(updatedAppointment)
        return getAppointmentById(id)
    }

    private suspend fun AppointmentEntity.toAppointmentResponse(): AppointmentResponse {
        val patient = userDao.getUserById(patientId)?.toPatientResponse()
            ?: throw IllegalStateException("Patient not found")
        val doctor = userDao.getUserById(doctorId)?.toDoctorResponse()
            ?: throw IllegalStateException("Doctor not found")

        return AppointmentResponse(
            id = id,
            patient = patient,
            doctor = doctor,
            scheduledTime = scheduledTime,
            status = AppointmentStatus.fromString(status),
            type = type,
            notes = notes,
            reason = reason,
            meetingLink = meetingLink,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun UserEntity.toPatientResponse(): PatientResponse {
        return PatientResponse(
            id = id,
            username = username,
            email = email,
            fName = fName,
            lName = lName,
            phoneNumber = phoneNumber,
            gender = gender,
            dob = dob,
            address = address,
            role = role,
            accountCreationDate = accountCreationDate,
            bloodGroup = null,
            allergies = emptyList(),
            medicalHistory = emptyList()
        )
    }

    private fun UserEntity.toDoctorResponse(): DoctorResponse {
        return DoctorResponse(
            id = id,
            username = username,
            email = email,
            fName = fName,
            lName = lName,
            phoneNumber = phoneNumber,
            gender = gender,
            dob = dob,
            address = address,
            role = role,
            accountCreationDate = accountCreationDate,
            specialization = "", // Add these fields to UserEntity if needed
            qualification = "",
            experience = 0,
            availableForEmergency = false
        )
    }
}