package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.PatientDetailEntity

@Dao
interface PatientDetailDao {
    @Query("SELECT * FROM patient_details WHERE userId = :userId")
    suspend fun getPatientDetailByUserId(userId: Long): PatientDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientDetail(patientDetail: PatientDetailEntity): Long

    @Update
    suspend fun updatePatientDetail(patientDetail: PatientDetailEntity)

    @Delete
    suspend fun deletePatientDetail(patientDetail: PatientDetailEntity)

    @Query("""
        SELECT p.* FROM patient_details p 
        INNER JOIN users u ON p.userId = u.id 
        WHERE u.accountCreationDate >= :startDate
    """)
    suspend fun getNewPatients(startDate: String): List<PatientDetailEntity>
}