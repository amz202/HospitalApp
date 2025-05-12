package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY scheduledTime DESC")
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY scheduledTime DESC")
    suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY scheduledTime DESC")
    suspend fun getAppointmentsByStatus(status: String): List<AppointmentEntity>

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)
}