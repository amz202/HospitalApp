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
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.LoginResponse
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.network.model.UserResponse
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

interface UserRepository {
    suspend fun getAllUsers(): List<UserResponse>
    suspend fun getUserById(id: Long): UserResponse
    suspend fun deleteUser(id: Long)
    suspend fun createUser(request: SignupRequest): UserResponse
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun getUserByEmail(email: String): UserResponse
    suspend fun getUserByUsername(username: String): UserResponse
    suspend fun getCurrentUser(): UserResponse?
    suspend fun logout()
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val context: Context
) : UserRepository {
    private val USER_ID_KEY = longPreferencesKey("user_id")
    private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    override suspend fun getAllUsers(): List<UserResponse> {
        return userDao.getAllUsers().map { it.toUserResponse() }
    }

    override suspend fun getUserById(id: Long): UserResponse {
        return userDao.getUserById(id)?.toUserResponse()
            ?: throw IllegalStateException("User not found")
    }

    override suspend fun deleteUser(id: Long) {
        val user = userDao.getUserById(id) ?: throw IllegalStateException("User not found")
        userDao.deleteUser(user)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createUser(request: SignupRequest): UserResponse {
        // Check if username already exists
        if (userDao.getUserByUsername(request.username) != null) {
            throw IllegalStateException("Username already exists")
        }

        val userEntity = UserEntity(
            username = request.username,
            password = request.password, // In production, hash this
            email = request.email,
            fName = request.fName,
            lName = request.lName,
            phoneNumber = request.phoneNumber,
            gender = request.gender.toString(),
            dob = request.dob,
            address = request.address,
            role = request.roles.toString(),
            accountCreationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )

        val id = userDao.insertUser(userEntity)
        return getUserById(id)
    }

    override suspend fun login(username: String, password: String): LoginResponse {
        val user = userDao.login(username, password)
            ?: throw IllegalStateException("Invalid credentials")

        // Store user session
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
            preferences[USER_ROLE_KEY] = user.role
            preferences[USER_NAME_KEY] = user.username
        }

        return LoginResponse(
            userId = user.id,
            username = user.username,
            role = user.role
        )
    }

    override suspend fun getUserByEmail(email: String): UserResponse {
        return userDao.getUserByEmail(email)?.toUserResponse()
            ?: throw IllegalStateException("User not found")
    }

    override suspend fun getUserByUsername(username: String): UserResponse {
        return userDao.getUserByUsername(username)?.toUserResponse()
            ?: throw IllegalStateException("User not found")
    }

    override suspend fun getCurrentUser(): UserResponse? {
        val userId = context.dataStore.data.first()[USER_ID_KEY] ?: return null
        return userDao.getUserById(userId)?.toUserResponse()
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(USER_NAME_KEY)
        }
    }

    private fun UserEntity.toUserResponse() = UserResponse(
        id = id,
        username = username,
        email = email,
        fName = fName,
        lName = lName,
        phoneNumber = phoneNumber,
        gender = gender,
        dob = dob,
        address = address,
        roles = setOf(role), // Convert single role to Set<String>
        accountCreationDate = accountCreationDate
    )
}