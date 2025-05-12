package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.AppointmentDao
import com.example.hospitalapp.data.local.dao.FeedbackDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.FeedbackEntity
import com.example.hospitalapp.data.local.extensions.toDoctorResponse
import com.example.hospitalapp.data.local.extensions.toPatientResponse
import com.example.hospitalapp.network.model.*
import java.time.LocalDate
import javax.inject.Inject

interface FeedbackRepository {
    suspend fun getFeedbackById(id: Long): FeedbackResponse
    suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackResponse
    suspend fun getFeedbackByDoctor(doctorId: Long): List<FeedbackResponse>
    suspend fun getFeedbackByPatient(patientId: Long): List<FeedbackResponse>
    suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): FeedbackResponse
    suspend fun hasFeedback(appointmentId: Long): Boolean
    suspend fun getAppointmentsWithoutFeedback(doctorId: Long): List<AppointmentResponse>
    suspend fun createFeedback(feedback: FeedbackRequest): FeedbackResponse
}

class FeedbackRepositoryImpl @Inject constructor(
    private val feedbackDao: FeedbackDao,
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao
) : FeedbackRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now()

    override suspend fun getFeedbackById(id: Long): FeedbackResponse {
        return feedbackDao.getFeedbackById(id)?.toFeedbackResponse()
            ?: throw IllegalStateException("Feedback not found")
    }

    override suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackResponse {
        return feedbackDao.getFeedbackByAppointment(appointmentId)?.toFeedbackResponse()
            ?: throw IllegalStateException("Feedback not found")
    }

    override suspend fun getFeedbackByDoctor(doctorId: Long): List<FeedbackResponse> {
        return feedbackDao.getFeedbacksByDoctor(doctorId).map { it.toFeedbackResponse() }
    }

    override suspend fun getFeedbackByPatient(patientId: Long): List<FeedbackResponse> {
        return feedbackDao.getFeedbacksByPatient(patientId).map { it.toFeedbackResponse() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateFeedback(id: Long, feedback: FeedbackRequest): FeedbackResponse {
        val existing = feedbackDao.getFeedbackById(id)
            ?: throw IllegalStateException("Feedback not found")

        val updatedFeedback = existing.copy(
            comments = feedback.comments,
            diagnosis = feedback.diagnosis,
            recommendations = feedback.recommendations,
            nextSteps = feedback.nextSteps,
            updatedAt = currentDate.toString()
        )

        feedbackDao.updateFeedback(updatedFeedback)
        return getFeedbackById(id)
    }

    override suspend fun hasFeedback(appointmentId: Long): Boolean {
        return feedbackDao.hasFeedback(appointmentId)
    }

    override suspend fun getAppointmentsWithoutFeedback(doctorId: Long): List<AppointmentResponse> {
        return feedbackDao.getAppointmentsWithoutFeedback(doctorId).map { appointmentEntity ->
            val patient = userDao.getUserById(appointmentEntity.patientId)?.toPatientResponse()
                ?: throw IllegalStateException("Patient not found")
            val doctor = userDao.getUserById(appointmentEntity.doctorId)?.toDoctorResponse()
                ?: throw IllegalStateException("Doctor not found")

            AppointmentResponse(
                id = appointmentEntity.id,
                patient = patient,
                doctor = doctor,
                scheduledTime = appointmentEntity.scheduledTime,
                status = AppointmentStatus.fromString(appointmentEntity.status),
                type = appointmentEntity.type,
                notes = appointmentEntity.notes,
                reason = appointmentEntity.reason,
                meetingLink = appointmentEntity.meetingLink,
                createdAt = appointmentEntity.createdAt,
                updatedAt = appointmentEntity.updatedAt
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createFeedback(feedback: FeedbackRequest): FeedbackResponse {
        // Verify appointment exists and is completed
        val appointment = appointmentDao.getAppointmentById(feedback.appointmentId)
            ?: throw IllegalStateException("Appointment not found")

        if (appointment.status != AppointmentStatus.COMPLETED.name) {
            throw IllegalStateException("Cannot add feedback for non-completed appointment")
        }

        // Check if feedback already exists
        if (feedbackDao.hasFeedback(feedback.appointmentId)) {
            throw IllegalStateException("Feedback already exists for this appointment")
        }

        val feedbackEntity = FeedbackEntity(
            comments = feedback.comments,
            diagnosis = feedback.diagnosis,
            recommendations = feedback.recommendations,
            nextSteps = feedback.nextSteps,
            doctorId = feedback.doctorId,
            patientId = feedback.patientId,
            appointmentId = feedback.appointmentId,
            createdAt = currentDate.toString(),
            updatedAt = currentDate.toString()
        )

        val id = feedbackDao.insertFeedback(feedbackEntity)
        return getFeedbackById(id)
    }

    private suspend fun FeedbackEntity.toFeedbackResponse(): FeedbackResponse {
        val doctor = userDao.getUserById(doctorId)?.toDoctorResponse()
            ?: throw IllegalStateException("Doctor not found")

        val patient = userDao.getUserById(patientId)?.toPatientResponse()
            ?: throw IllegalStateException("Patient not found")

        val appointment = appointmentDao.getAppointmentById(appointmentId)
            ?: throw IllegalStateException("Appointment not found")

        return FeedbackResponse(
            id = id,
            comments = comments,
            diagnosis = diagnosis,
            recommendations = recommendations,
            nextSteps = nextSteps,
            createdAt = createdAt,
            updatedAt = updatedAt,
            doctor = doctor,
            patient = patient,
            appointment = AppointmentResponse(
                id = appointment.id,
                patient = patient,
                doctor = doctor,
                scheduledTime = appointment.scheduledTime,
                status = AppointmentStatus.fromString(appointment.status),
                type = appointment.type,
                notes = appointment.notes,
                reason = appointment.reason,
                meetingLink = appointment.meetingLink,
                createdAt = appointment.createdAt,
                updatedAt = appointment.updatedAt
            )
        )
    }
}