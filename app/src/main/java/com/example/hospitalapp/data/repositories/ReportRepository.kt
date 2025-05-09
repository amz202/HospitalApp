package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.ReportRequest
import com.example.hospitalapp.network.model.ReportResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface ReportRepository {
    suspend fun getAllReports(): List<ReportResponse>
    suspend fun getReportById(id: Long): ReportResponse
    suspend fun getReportsByPatient(patientId: Long): List<ReportResponse>
    suspend fun generateReport(appointmentId: Long): ReportResponse
    suspend fun updateReport(id: Long, report: ReportRequest): ReportResponse
    suspend fun deleteReport(id: Long)
    suspend fun getReportsByPatientBetweenDates(
        patientId: Long,
        startDate: String,
        endDate: String
    ): List<ReportResponse>
}

class ReportRepositoryImpl(private val apiService: ApiService) : ReportRepository {
    override suspend fun getAllReports(): List<ReportResponse> =
        apiService.getAllReports()

    override suspend fun getReportById(id: Long): ReportResponse =
        apiService.getReportById(id)

    override suspend fun getReportsByPatient(patientId: Long): List<ReportResponse> =
        apiService.getReportsByPatient(patientId)

    override suspend fun generateReport(appointmentId: Long): ReportResponse =
        apiService.generateReport(appointmentId)

    override suspend fun updateReport(id: Long, report: ReportRequest): ReportResponse =
        apiService.updateReport(id, report)

    override suspend fun deleteReport(id: Long) =
        apiService.deleteReport(id)

    override suspend fun getReportsByPatientBetweenDates(
        patientId: Long,
        startDate: String,
        endDate: String
    ): List<ReportResponse> =
        apiService.getReportsByPatientBetweenDates(patientId, startDate, endDate)
}