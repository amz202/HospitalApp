package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications")
    suspend fun getAllMedications(): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Long): MedicationEntity?

    @Query("SELECT * FROM medications WHERE appointmentId = :appointmentId")
    suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE patientId = :patientId ORDER BY createdAt DESC")
    suspend fun getPatientMedications(patientId: Long): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE patientId = :patientId AND active = 1 ORDER BY createdAt DESC")
    suspend fun getActiveMedications(patientId: Long): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)
}