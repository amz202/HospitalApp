package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY scheduledTime DESC")
    fun getPatientAppointments(patientId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY scheduledTime DESC")
    fun getDoctorAppointments(doctorId: Long): Flow<List<AppointmentEntity>>

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)
}