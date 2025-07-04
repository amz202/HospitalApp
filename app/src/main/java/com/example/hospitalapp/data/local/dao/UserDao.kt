package com.example.hospitalapp.data.local.dao

import androidx.room.*
import com.example.hospitalapp.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun getUsersByRole(role: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE role = :role ORDER BY accountCreationDate DESC LIMIT 1")
    suspend fun getLatestUserByRole(role: String): UserEntity?

    @Query("SELECT * FROM users WHERE userName = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?
}