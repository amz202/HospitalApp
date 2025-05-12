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
}

class AppointmentRepositoryImpl (
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao,
    private val doctorDetailDao: DoctorDetailDao
) : AppointmentRepository {


    override suspend fun getAppointments(): List<AppointmentResponse> {
        return appointmentDao.getAllAppointments().map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getAppointmentById(id: Long): AppointmentResponse {
        val appointment = appointmentDao.getAppointmentById(id)
            ?: throw IllegalStateException("Appointment not found with id: $id")
        return appointment.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
    }

    override suspend fun createAppointment(request: AppointmentRequest): AppointmentResponse {
        // Validate patient exists and has patient details
        val patient = userDao.getUserById(request.patientId)
            ?: throw IllegalStateException("Patient not found with id: ${request.patientId}")
        patientDetailDao.getPatientDetailByUserId(request.patientId)
            ?: throw IllegalStateException("Patient details not found for id: ${request.patientId}")

        // Validate doctor exists and has doctor details
        val doctor = userDao.getUserById(request.doctorId)
            ?: throw IllegalStateException("Doctor not found with id: ${request.doctorId}")
        doctorDetailDao.getDoctorDetailByUserId(request.doctorId)
            ?: throw IllegalStateException("Doctor details not found for id: ${request.doctorId}")

        val appointmentEntity = AppointmentEntity(
            patientId = request.patientId,
            doctorId = request.doctorId,
            scheduledTime = request.scheduledTime,
            appointmentStatus = request.type,
            type = request.type,
            reason = request.reason.toString(),
            notes = request.notes,
            virtualMeetingUrl = request.virtualMeetingUrl,
            status = request.type,
        )

        val id = appointmentDao.insertAppointment(appointmentEntity)
        return getAppointmentById(id)
    }

    override suspend fun updateAppointment(id: Long, request: AppointmentRequest): AppointmentResponse {
        val existing = appointmentDao.getAppointmentById(id)
            ?: throw IllegalStateException("Appointment not found with id: $id")

        // Validate patient exists and has patient details
        val patient = userDao.getUserById(request.patientId)
            ?: throw IllegalStateException("Patient not found with id: ${request.patientId}")
        patientDetailDao.getPatientDetailByUserId(request.patientId)
            ?: throw IllegalStateException("Patient details not found for id: ${request.patientId}")

        // Validate doctor exists and has doctor details
        val doctor = userDao.getUserById(request.doctorId)
            ?: throw IllegalStateException("Doctor not found with id: ${request.doctorId}")
        doctorDetailDao.getDoctorDetailByUserId(request.doctorId)
            ?: throw IllegalStateException("Doctor details not found for id: ${request.doctorId}")

        val updated = existing.copy(
            patientId = request.patientId,
            doctorId = request.doctorId,
            scheduledTime = request.scheduledTime,
            appointmentStatus = request.type,
            type = request.type,
            reason = request.reason.toString(),
            notes = request.notes,
            virtualMeetingUrl = request.virtualMeetingUrl,
            status = request.type,
        )

        appointmentDao.updateAppointment(updated)
        return getAppointmentById(id)
    }

    override suspend fun getPatientAppointments(patientId: Long): List<AppointmentResponse> {
        // Verify patient exists and has patient details
        val patient = userDao.getUserById(patientId)
            ?: throw IllegalStateException("Patient not found with id: $patientId")
        patientDetailDao.getPatientDetailByUserId(patientId)
            ?: throw IllegalStateException("Patient details not found for id: $patientId")

        return appointmentDao.getPatientAppointments(patientId).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentResponse> {
        // Verify doctor exists and has doctor details
        val doctor = userDao.getUserById(doctorId)
            ?: throw IllegalStateException("Doctor not found with id: $doctorId")
        doctorDetailDao.getDoctorDetailByUserId(doctorId)
            ?: throw IllegalStateException("Doctor details not found for id: $doctorId")

        return appointmentDao.getDoctorAppointments(doctorId).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }

    override suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<AppointmentResponse> {
        return appointmentDao.getAppointmentsByStatus(status.name).map {
            it.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
        }
    }
}