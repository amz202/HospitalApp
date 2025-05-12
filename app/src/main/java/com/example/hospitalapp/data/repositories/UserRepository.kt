package com.example.hospitalapp.data.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.extensions.toUserResponse
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.LoginResponse
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.network.model.UserResponse
import com.example.hospitalapp.network.model.UserRole
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

interface UserRepository {
    suspend fun createInitialUser(username: String, password: String, role: UserRole): Long
    suspend fun getUserById(id: Long): UserResponse
    suspend fun deleteUser(id: Long)
    suspend fun getUsersByRole(role: UserRole): List<UserResponse>
    suspend fun getLatestUserByRole(role: UserRole): UserResponse?
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val context: Context
) : UserRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        return LocalDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createInitialUser(username: String, password: String, role: UserRole): Long {
        val userEntity = UserEntity(
            password = password,
            role = role.toString(),
            accountCreationDate = LocalDate.now().toString(),
            userName = username,
        )
        return userDao.insertUser(userEntity)
    }

    override suspend fun getUserById(id: Long): UserResponse {
        return userDao.getUserById(id)?.toUserResponse()
            ?: throw IllegalStateException("User not found")
    }

    override suspend fun deleteUser(id: Long) {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("User not found")
        userDao.deleteUser(user)
    }

    override suspend fun getUsersByRole(role: UserRole): List<UserResponse> {
        return userDao.getUsersByRole(role.toString()).map { it.toUserResponse() }
    }

    override suspend fun getLatestUserByRole(role: UserRole): UserResponse? {
        return userDao.getLatestUserByRole(role.toString())?.toUserResponse()
    }
}