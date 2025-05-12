package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.ReportEntity

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports")
    suspend fun getAllReports(): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Long): ReportEntity?

    @Query("SELECT * FROM reports WHERE patientId = :patientId ORDER BY generatedAt DESC")
    suspend fun getReportsByPatient(patientId: Long): List<ReportEntity>

    @Query("""
        SELECT * FROM reports 
        WHERE patientId = :patientId 
        AND (:startDate IS NULL OR timePeriodEnd >= :startDate)
        AND (:endDate IS NULL OR timePeriodStart <= :endDate)
        ORDER BY generatedAt DESC
    """)
    suspend fun getReportsByPatientBetweenDates(
        patientId: Long,
        startDate: String?,
        endDate: String?
    ): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE appointmentId = :appointmentId")
    suspend fun getReportByAppointment(appointmentId: Long): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE doctorId = :doctorId ORDER BY generatedAt DESC")
    suspend fun getReportsByDoctor(doctorId: Long): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE appointmentId = :appointmentId")
    suspend fun getReportsByAppointment(appointmentId: Long): List<ReportEntity>
}