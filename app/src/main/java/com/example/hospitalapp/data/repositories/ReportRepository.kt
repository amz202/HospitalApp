package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.ReportEntity
import com.example.hospitalapp.data.local.extensions.toReportResponse
import com.example.hospitalapp.network.model.*
import javax.inject.Inject

interface ReportRepository {
    suspend fun getAllReports(): List<ReportResponse>
    suspend fun getReportById(id: Long): ReportResponse
    suspend fun getReportsByPatient(patientId: Long): List<ReportResponse>
    suspend fun getReportsByDoctor(doctorId: Long): List<ReportResponse>
    suspend fun generateReport(report: ReportRequest): ReportResponse
    suspend fun updateReport(id: Long, report: ReportRequest): ReportResponse
    suspend fun deleteReport(id: Long)
    suspend fun getReportsByPatientBetweenDates(
        patientId: Long,
        startDate: String?,
        endDate: String?
    ): List<ReportResponse>
}

class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao,
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao,
    private val vitalsDao: VitalsDao,
    private val medicationDao: MedicationDao,
    private val feedbackDao: FeedbackDao
) : ReportRepository {

    private val currentDate = "2025-05-12 14:34:24" // Current UTC time

    override suspend fun getAllReports(): List<ReportResponse> {
        return reportDao.getAllReports().map {
            it.toReportResponse(userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao)
        }
    }

    override suspend fun getReportById(id: Long): ReportResponse {
        return reportDao.getReportById(id)?.toReportResponse(
            userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao
        ) ?: throw IllegalStateException("Report not found")
    }

    override suspend fun getReportsByPatient(patientId: Long): List<ReportResponse> {
        return reportDao.getReportsByPatient(patientId).map {
            it.toReportResponse(userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao)
        }
    }

    override suspend fun getReportsByDoctor(doctorId: Long): List<ReportResponse> {
        return reportDao.getReportsByDoctor(doctorId).map {
            it.toReportResponse(userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao)
        }
    }

    override suspend fun generateReport(report: ReportRequest): ReportResponse {
        // Validate the patient exists
        userDao.getUserById(report.patientId) ?: throw IllegalStateException("Patient not found")

        // Validate doctor if provided
        report.doctorId?.let {
            userDao.getUserById(it) ?: throw IllegalStateException("Doctor not found")
        }

        // Validate appointment if provided
        report.appointmentId?.let {
            appointmentDao.getAppointmentById(it) ?: throw IllegalStateException("Appointment not found")
        }

        // Validate vitals if provided
        report.vitalsId?.let {
            vitalsDao.getVitalsById(it) ?: throw IllegalStateException("Vitals not found")
        }

        // Validate feedback if provided
        report.feedbackId?.let {
            feedbackDao.getFeedbackById(it) ?: throw IllegalStateException("Feedback not found")
        }

        val reportEntity = ReportEntity(
            title = report.title,
            generatedAt = currentDate,
            patientId = report.patientId,
            doctorId = report.doctorId,
            summary = report.summary,
            reportType = report.reportType,
            appointmentId = report.appointmentId,
            vitalsId = report.vitalsId,
            feedbackId = report.feedbackId,
            filePath = generateFilePath(report),
            timePeriodStart = report.timePeriodStart,
            timePeriodEnd = report.timePeriodEnd
        )

        val id = reportDao.insertReport(reportEntity)
        return getReportById(id)
    }

    override suspend fun updateReport(id: Long, report: ReportRequest): ReportResponse {
        val existing = reportDao.getReportById(id)
            ?: throw IllegalStateException("Report not found")

        // Validate all referenced entities exist
        userDao.getUserById(report.patientId) ?: throw IllegalStateException("Patient not found")
        report.doctorId?.let {
            userDao.getUserById(it) ?: throw IllegalStateException("Doctor not found")
        }
        report.appointmentId?.let {
            appointmentDao.getAppointmentById(it) ?: throw IllegalStateException("Appointment not found")
        }
        report.vitalsId?.let {
            vitalsDao.getVitalsById(it) ?: throw IllegalStateException("Vitals not found")
        }
        report.feedbackId?.let {
            feedbackDao.getFeedbackById(it) ?: throw IllegalStateException("Feedback not found")
        }

        val updatedReport = existing.copy(
            title = report.title,
            patientId = report.patientId,
            doctorId = report.doctorId,
            summary = report.summary,
            reportType = report.reportType,
            appointmentId = report.appointmentId,
            vitalsId = report.vitalsId,
            feedbackId = report.feedbackId,
            timePeriodStart = report.timePeriodStart,
            timePeriodEnd = report.timePeriodEnd
        )

        reportDao.updateReport(updatedReport)
        return getReportById(id)
    }

    override suspend fun deleteReport(id: Long) {
        val report = reportDao.getReportById(id)
            ?: throw IllegalStateException("Report not found")
        reportDao.deleteReport(report)
    }

    override suspend fun getReportsByPatientBetweenDates(
        patientId: Long,
        startDate: String?,
        endDate: String?
    ): List<ReportResponse> {
        return reportDao.getReportsByPatientBetweenDates(patientId, startDate, endDate)
            .map { it.toReportResponse(userDao, vitalsDao, medicationDao, feedbackDao, appointmentDao) }
    }

    private fun generateFilePath(report: ReportRequest): String {
        val timestamp = currentDate.replace(Regex("[^0-9]"), "")
        val sanitizedTitle = report.title.replace(Regex("[^a-zA-Z0-9]"), "_")
        return "reports/${report.patientId}/${timestamp}_${sanitizedTitle}.pdf"
    }
}