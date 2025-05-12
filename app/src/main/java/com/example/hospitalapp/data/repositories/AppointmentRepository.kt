package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.AppointmentDao
import com.example.hospitalapp.data.local.dao.DoctorDetailDao
import com.example.hospitalapp.data.local.dao.PatientDetailDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.extensions.toAppointmentResponse
import com.example.hospitalapp.network.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface AppointmentRepository {
    suspend fun getAppointments(): List<AppointmentResponse>
    suspend fun getAppointmentById(id: Long): AppointmentResponse
    suspend fun createAppointment(appointment: AppointmentRequest): AppointmentResponse
    suspend fun updateAppointment(id: Long, appointment: AppointmentRequest): AppointmentResponse
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse>
    suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse>
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse>
    suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse>
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse
    suspend fun cancelAppointment(id: Long): AppointmentResponse
}

class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao,
    private val doctorDetailDao: DoctorDetailDao
) : AppointmentRepository {

    private val currentDate = "2025-05-12 20:38:34" // Current UTC time
    private val currentUser = "amz202" // Current user's login

    override suspend fun getAppointments(): List<AppointmentResponse> {
        return appointmentDao.getAllAppointments().map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getAppointmentById(id: Long): AppointmentResponse {
        return appointmentDao.getAppointmentById(id)
            ?.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
            ?: throw IllegalStateException("Appointment not found")
    }

    override suspend fun createAppointment(request: AppointmentRequest): AppointmentResponse {
        // Validate patient exists
        userDao.getUserById(request.patientId)
            ?: throw IllegalStateException("Patient not found")

        // Validate doctor exists
        userDao.getUserById(request.doctorId)
            ?: throw IllegalStateException("Doctor not found")

        val appointmentEntity = AppointmentEntity(
            patientId = request.patientId,
            doctorId = request.doctorId,
            scheduledTime = request.scheduledTime,
            appointmentStatus = request.status.name,
            type = request.type,
            reason = request.reason,
            notes = request.notes,
            virtualMeetingUrl = request.virtualMeetingUrl,
            createdAt = currentDate,
            updatedAt = currentDate,
            createdBy = currentUser,
            updatedBy = currentUser
        )

        val id = appointmentDao.insertAppointment(appointmentEntity)
        return getAppointmentById(id)
    }

    override suspend fun updateAppointment(id: Long, request: AppointmentRequest): AppointmentResponse {
        val existing = appointmentDao.getAppointmentById(id)
            ?: throw IllegalStateException("Appointment not found")

        // Validate patient exists
        userDao.getUserById(request.patientId)
            ?: throw IllegalStateException("Patient not found")

        // Validate doctor exists
        userDao.getUserById(request.doctorId)
            ?: throw IllegalStateException("Doctor not found")

        val updated = existing.copy(
            patientId = request.patientId,
            doctorId = request.doctorId,
            scheduledTime = request.scheduledTime,
            appointmentStatus = request.status.name,
            type = request.type,
            reason = request.reason,
            notes = request.notes,
            virtualMeetingUrl = request.virtualMeetingUrl,
            updatedAt = currentDate,
            updatedBy = currentUser
        )

        appointmentDao.updateAppointment(updated)
        return getAppointmentById(id)
    }

    override suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse> {
        // Verify patient exists
        userDao.getUserById(patientId)
            ?: throw IllegalStateException("Patient not found")

        return appointmentDao.getPatientAppointments(patientId).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse> {
        // Verify doctor exists
        userDao.getUserById(doctorId)
            ?: throw IllegalStateException("Doctor not found")

        return appointmentDao.getDoctorAppointments(doctorId).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse> {
        return appointmentDao.getAppointmentsByStatus(status.name).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getUpcomingAppointmentsByPatient(patientId: Long): List<AppointmentResponse> {
        // Verify patient exists
        userDao.getUserById(patientId)
            ?: throw IllegalStateException("Patient not found")

        return appointmentDao.getFuturePatientAppointments(patientId, currentDate).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getUpcomingAppointmentsByDoctor(doctorId: Long): List<AppointmentResponse> {
        // Verify doctor exists
        userDao.getUserById(doctorId)
            ?: throw IllegalStateException("Doctor not found")

        return appointmentDao.getFutureDoctorAppointments(doctorId, currentDate).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus): AppointmentResponse {
        val existing = appointmentDao.getAppointmentById(id)
            ?: throw IllegalStateException("Appointment not found")

        val updated = existing.copy(
            appointmentStatus = status.name,
            updatedAt = currentDate,
            updatedBy = currentUser
        )

        appointmentDao.updateAppointment(updated)
        return getAppointmentById(id)
    }

    override suspend fun cancelAppointment(id: Long): AppointmentResponse {
        return updateAppointmentStatus(id, AppointmentStatus.CANCELLED)
    }
}