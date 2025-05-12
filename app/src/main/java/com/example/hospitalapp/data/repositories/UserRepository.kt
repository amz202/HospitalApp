package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.LoginRequest
import com.example.hospitalapp.network.model.LoginResponse
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.network.model.UserResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

interface UserRepository {
    suspend fun getAllUsers(): List<UserResponse>
    suspend fun getUserById(id: Long): UserResponse
    suspend fun deleteUser(id: Long)
    suspend fun createUser(request: SignupRequest): Response<UserResponse>
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun getUserByEmail(email: String): UserResponse
    suspend fun getUserByUsername(username: String): UserResponse
}

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    override suspend fun getAllUsers(): List<UserResponse> =
        apiService.getAllUsers()

    override suspend fun getUserById(id: Long): UserResponse =
        apiService.getUserById(id)

    override suspend fun deleteUser(id: Long) =
        apiService.deleteUser(id)

    override suspend fun createUser(request: SignupRequest): Response<UserResponse> =
        apiService.register(request)

    override suspend fun login(username: String, password: String): LoginResponse =
        apiService.login(LoginRequest(username, password))

    override suspend fun getUserByEmail(email: String): UserResponse =
        apiService.getUserByEmail(email)

    override suspend fun getUserByUsername(username: String): UserResponse =
        apiService.getUserByUsername(username)
}