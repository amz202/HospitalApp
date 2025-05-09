package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.ReportRequest
import com.example.hospitalapp.network.model.ReportResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface ReportRepository {
    suspend fun getReport(id: Long): ReportResponse
    suspend fun createReport(report: ReportRequest): ResponseBody
    suspend fun getPatientReports(patientId: Long): List<ReportResponse>
    suspend fun uploadReportFile(id: Long, file: MultipartBody.Part): ResponseBody
}

class ReportRepositoryImpl(private val apiService: ApiService) : ReportRepository {
    override suspend fun getReport(id: Long): ReportResponse =
        apiService.getReport(id)

    override suspend fun createReport(report: ReportRequest): ResponseBody =
        apiService.createReport(report)

    override suspend fun getPatientReports(patientId: Long): List<ReportResponse> =
        apiService.getPatientReports(patientId)

    override suspend fun uploadReportFile(id: Long, file: MultipartBody.Part): ResponseBody =
        apiService.uploadReportFile(id, file)
}