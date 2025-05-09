package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.network.ApiService
import com.example.hospitalapp.network.model.UserRequest
import com.example.hospitalapp.network.model.UserResponse
import okhttp3.ResponseBody

interface UserRepository {
    suspend fun getAllUsers(): List<UserResponse>
    suspend fun getUserById(id: Long): UserResponse
    suspend fun deleteUser(id: Long)
}

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    override suspend fun getAllUsers(): List<UserResponse> =
        apiService.getAllUsers()

    override suspend fun getUserById(id: Long): UserResponse =
        apiService.getUserById(id)

    override suspend fun deleteUser(id: Long) =
        apiService.deleteUser(id)
}