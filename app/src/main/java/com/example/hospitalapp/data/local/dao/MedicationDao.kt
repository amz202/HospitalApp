package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Long): MedicationEntity?

    @Query("SELECT * FROM medications WHERE patientId = :patientId ORDER BY startDate DESC")
    fun getPatientMedications(patientId: Long): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE appointmentId = :appointmentId")
    fun getMedicationsByAppointment(appointmentId: Long): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE patientId = :patientId AND active = 1")
    fun getActiveMedications(patientId: Long): Flow<List<MedicationEntity>>

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)
}