package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.PatientDetailEntity

@Dao
interface PatientDetailDao {
    @Query("SELECT * FROM patient_details WHERE userId = :userId")
    suspend fun getPatientDetailByUserId(userId: Long): PatientDetailEntity?

    @Query("SELECT * FROM patient_details WHERE id = :id")
    suspend fun getPatientDetailById(id: Long): PatientDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientDetail(patientDetail: PatientDetailEntity): Long

    @Update
    suspend fun updatePatientDetail(patientDetail: PatientDetailEntity)

    @Delete
    suspend fun deletePatientDetail(patientDetail: PatientDetailEntity)

    @Query("SELECT * FROM patient_details")
    suspend fun getAllPatientDetails(): List<PatientDetailEntity>
}