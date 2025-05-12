package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.AppointmentEntity
import com.example.hospitalapp.data.local.entities.FeedbackEntity

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedbacks WHERE id = :id")
    suspend fun getFeedbackById(id: Long): FeedbackEntity?

    @Query("SELECT * FROM feedbacks WHERE appointmentId = :appointmentId")
    suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackEntity?

    @Query("SELECT * FROM feedbacks WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    suspend fun getFeedbacksByDoctor(doctorId: Long): List<FeedbackEntity>

    @Query("""
        SELECT f.* FROM feedbacks f 
        INNER JOIN appointments a ON f.appointmentId = a.id 
        WHERE a.patientId = :patientId 
        ORDER BY f.createdAt DESC
    """)
    suspend fun getFeedbacksByPatient(patientId: Long): List<FeedbackEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM feedbacks WHERE appointmentId = :appointmentId)")
    suspend fun hasFeedback(appointmentId: Long): Boolean

    @Query("""
        SELECT a.* FROM appointments a 
        LEFT JOIN feedbacks f ON a.id = f.appointmentId 
        WHERE a.doctorId = :doctorId AND f.id IS NULL 
        AND a.status = 'COMPLETED'
    """)
    suspend fun getAppointmentsWithoutFeedback(doctorId: Long): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Update
    suspend fun updateFeedback(feedback: FeedbackEntity)

    @Delete
    suspend fun deleteFeedback(feedback: FeedbackEntity)
}