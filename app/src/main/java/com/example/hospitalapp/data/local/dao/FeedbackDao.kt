package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT * FROM feedback WHERE id = :id")
    suspend fun getFeedbackById(id: Long): FeedbackEntity?

    @Query("SELECT * FROM feedback WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getPatientFeedback(patientId: Long): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedback WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    fun getDoctorFeedback(doctorId: Long): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedback WHERE appointmentId = :appointmentId")
    suspend fun getFeedbackByAppointment(appointmentId: Long): FeedbackEntity?

    @Update
    suspend fun updateFeedback(feedback: FeedbackEntity)

    @Delete
    suspend fun deleteFeedback(feedback: FeedbackEntity)
}