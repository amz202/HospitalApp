package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.ReportEntity
import com.example.hospitalapp.data.local.entities.VitalsEntity
import com.example.hospitalapp.data.local.extensions.toReportResponse
import com.example.hospitalapp.data.local.extensions.toVitalsResponse
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReportRepository(
    private val reportDao: ReportDao,
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao,
    private val doctorDetailDao: DoctorDetailDao,
    private val vitalsDao: VitalsDao,
    private val medicationDao: MedicationDao,
    private val feedbackDao: FeedbackDao,
    private val appointmentDao: AppointmentDao
) {
    suspend fun createReport(
        title: String,
        patientId: Long,
        doctorId: Long?,
        summary: String,
        reportType: String,
        appointmentId: Long? = null,
        vitalsId: Long? = null,
        feedbackId: Long? = null,
        filePath: String? = null,
        timePeriodStart: String? = null,
        timePeriodEnd: String? = null
    ): Long {
        val reportEntity = ReportEntity(
            title = title,
            generatedAt = "2025-05-12 21:18:48",
            patientId = patientId,
            doctorId = doctorId,
            summary = summary,
            reportType = reportType,
            appointmentId = appointmentId,
            vitalsId = vitalsId,
            feedbackId = feedbackId,
            filePath = filePath,
            timePeriodStart = timePeriodStart,
            timePeriodEnd = timePeriodEnd
        )
        return reportDao.insertReport(reportEntity)
    }



    suspend fun getPatientReports(patientId: Long): List<ReportResponse> {
        return reportDao.getReportsByPatient(patientId).map { report ->
            report.toReportResponse(
                userDao = userDao,
                patientDetailDao = patientDetailDao,
                doctorDetailDao = doctorDetailDao,
                vitalsDao = vitalsDao,
                medicationDao = medicationDao,
                feedbackDao = feedbackDao,
                appointmentDao = appointmentDao
            )
        }
    }

    suspend fun getDoctorReports(doctorId: Long): List<ReportResponse> {
        return reportDao.getReportsByDoctor(doctorId).map { report ->
            report.toReportResponse(
                userDao = userDao,
                patientDetailDao = patientDetailDao,
                doctorDetailDao = doctorDetailDao,
                vitalsDao = vitalsDao,
                medicationDao = medicationDao,
                feedbackDao = feedbackDao,
                appointmentDao = appointmentDao
            )
        }
    }

    suspend fun deleteReport(reportId: Long) {
        reportDao.getReportById(reportId)?.let { report ->
            reportDao.deleteReport(report)
        }
    }

}