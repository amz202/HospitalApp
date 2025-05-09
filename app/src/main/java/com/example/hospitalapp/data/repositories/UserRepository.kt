package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.UserRequest
import com.example.hospitalapp.network.model.UserResponse
import okhttp3.ResponseBody

interface UserRepository {
    suspend fun getUser(id: Long): UserResponse
    suspend fun getAllUsers(): List<UserResponse>
    suspend fun createUser(user: UserRequest): UserResponse
    suspend fun updateUser(id: Long, user: UserRequest): UserResponse
    suspend fun deleteUser(id: Long): ResponseBody
    suspend fun getUserByEmail(email: String): UserResponse
    suspend fun getUserByUsername(username: String): UserResponse
}

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    override suspend fun getUser(id: Long): UserResponse =
        apiService.getUser(id)

    override suspend fun getAllUsers(): List<UserResponse> =
        apiService.getAllUsers()

    override suspend fun createUser(user: UserRequest): UserResponse =
        apiService.createUser(user)

    override suspend fun updateUser(id: Long, user: UserRequest): UserResponse =
        apiService.updateUser(id, user)

    override suspend fun deleteUser(id: Long): ResponseBody =
        apiService.deleteUser(id)

    override suspend fun getUserByEmail(email: String): UserResponse =
        apiService.getUserByEmail(email)

    override suspend fun getUserByUsername(username: String): UserResponse =
        apiService.getUserByUsername(username)
}