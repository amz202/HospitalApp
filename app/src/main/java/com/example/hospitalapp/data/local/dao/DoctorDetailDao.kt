package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.DoctorDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDetailDao {
    @Query("SELECT * FROM doctor_details WHERE userId = :userId")
    suspend fun getDoctorDetailByUserId(userId: Long): DoctorDetailEntity?

    @Query("SELECT * FROM doctor_details WHERE id = :id")
    suspend fun getDoctorDetailById(id: Long): DoctorDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctorDetail(doctorDetail: DoctorDetailEntity): Long

    @Update
    suspend fun updateDoctorDetail(doctorDetail: DoctorDetailEntity)

    @Delete
    suspend fun deleteDoctorDetail(doctorDetail: DoctorDetailEntity)

    @Query("SELECT * FROM doctor_details WHERE specialization = :specialization")
    suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorDetailEntity>
}