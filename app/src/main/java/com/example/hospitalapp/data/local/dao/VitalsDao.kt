package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.VitalsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitals(vitals: VitalsEntity): Long

    @Query("SELECT * FROM vitals WHERE id = :id")
    suspend fun getVitalsById(id: Long): VitalsEntity?

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY recordedAt DESC")
    fun getVitalsByPatient(patientId: Long): Flow<List<VitalsEntity>>

    @Query("SELECT * FROM vitals WHERE patientId = :patientId AND critical = 1 ORDER BY recordedAt DESC")
    fun getCriticalVitals(patientId: Long): Flow<List<VitalsEntity>>

    @Update
    suspend fun updateVitals(vitals: VitalsEntity)

    @Delete
    suspend fun deleteVitals(vitals: VitalsEntity)

}