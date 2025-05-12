package com.example.hospitalapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hospitalapp.HospitalApplication
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.data.repositories.UserRepository
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.UserResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences

) : ViewModel(){
    private val TAG = "UserViewModel"
    private var _loginState = mutableStateOf<BaseUiState<Long?>>(BaseUiState.Success(null))
    val loginState: State<BaseUiState<Long?>> = _loginState

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    var userDetailsUiState: BaseUiState<UserResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    var createUserUiState: BaseUiState<UserResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set


    init {
        viewModelScope.launch {
            val userId = userPreferences.getUserId()
            userId?.let {
                getUserById(it)
            }
        }
    }

    fun createUser(request: CreateUserRequest) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Creating user: ${request.username}")
                createUserUiState = BaseUiState.Loading
                errorMessage = null

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
                    throw IllegalArgumentException("Invalid email format")
                }

                // Validate password length
                if (request.password.length < 6) {
                    throw IllegalArgumentException("Password must be at least 6 characters")
                }

                val result = userRepository.createUser(request)
                Log.d(TAG, "User created successfully: ${result.id}")

                _currentUser.value = result
                userPreferences.saveUser(result)
                userPreferences.saveUserId(result.id)
                createUserUiState = BaseUiState.Success(result)

            } catch (e: Exception) {
                Log.e(TAG, "Error creating user", e)
                errorMessage = when {
                    e is IllegalArgumentException -> e.message
                    e.message?.contains("409") == true -> "Username or email already exists"
                    e.message?.contains("400") == true -> "Invalid input data"
                    e is IOException -> "Network error: Please check your connection"
                    else -> "Error creating account: ${e.message}"
                }
                Log.e(TAG, "Error details: ${e.message}", e)
                createUserUiState = BaseUiState.Error
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = BaseUiState.Loading
                val response = userRepository.login(username, password)
                userPreferences.saveUserId(response.userId)

                // After successful login, get user details
                getUserById(response.userId)

                _loginState.value = BaseUiState.Success(response)
            } catch (e: Exception) {
                _loginState.value = BaseUiState.Error
                errorMessage = when {
                    e.message?.contains("401") == true -> "Invalid username or password"
                    e is IOException -> "Network error: Please check your connection"
                    else -> "Login failed: ${e.message}"
                }
                e.printStackTrace()
            }
        }
    }

    fun getUserById(id: Long) {
        viewModelScope.launch {
            userDetailsUiState = BaseUiState.Loading
            try {
                val result = userRepository.getUserById(id)
                _currentUser.value = result
                userPreferences.saveUser(result)
                userDetailsUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                userDetailsUiState = BaseUiState.Error
                errorMessage = "Failed to get user details: ${e.message}"
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUserData()
            _currentUser.value = null
            _loginState.value = BaseUiState.Success(null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication
                UserViewModel(app.container.userRepository, app.userPreferences)
            }
        }
    }
    fun hasRole(role: String): Boolean {
        return _currentUser.value?.roles?.contains(role) ?: false
    }

    fun getPrimaryRole(): String? {
        val roles = _currentUser.value?.roles
        return when {
            roles?.contains("PATIENT") == true -> "PATIENT"
            roles?.contains("DOCTOR") == true -> "DOCTOR"
            roles?.contains("ADMIN") == true -> "ADMIN"
            else -> null
        }
    }
}