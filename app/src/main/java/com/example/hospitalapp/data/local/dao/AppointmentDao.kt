package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY scheduledTime DESC")
    suspend fun getPatientAppointments(patientId: Long): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY scheduledTime DESC")
    suspend fun getAppointmentsByStatus(status: String): List<AppointmentEntity>

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    @Query("""
        SELECT DISTINCT u.* FROM users u 
        INNER JOIN appointments a ON u.id = a.patientId 
        WHERE a.doctorId = :doctorId AND u.role = 'PATIENT'
    """)
    suspend fun getDoctorPatients(doctorId: Long): List<UserEntity>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId")
    suspend fun getDoctorAppointments(doctorId: Long): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long
}