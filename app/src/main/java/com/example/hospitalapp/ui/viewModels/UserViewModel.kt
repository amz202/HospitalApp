package com.example.hospitalapp.ui.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.example.hospitalapp.network.model.LoginResponse
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.network.model.UserResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val TAG = "UserViewModel"

    companion object {
        private val VALID_GENDERS = setOf("MALE", "FEMALE", "OTHER")
        @RequiresApi(Build.VERSION_CODES.O)
        private val DATE_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication
                UserViewModel(app.container.userRepository, app.userPreferences)
            }
        }
    }

    private var _loginState = mutableStateOf<BaseUiState<LoginResponse?>>(BaseUiState.Success(null))
    val loginState: State<BaseUiState<LoginResponse?>> = _loginState

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun createUser(request: SignupRequest) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Creating user: ${request.username}")
                createUserUiState = BaseUiState.Loading
                errorMessage = null

                validateSignupRequest(request)

                val result = userRepository.createUser(request)
                Log.d(TAG, "User created successfully: ${result.id}")

                _currentUser.value = result
                userPreferences.saveUser(result)
                userPreferences.saveUserId(result.id)
                createUserUiState = BaseUiState.Success(result)

            } catch (e: Exception) {
                Log.e(TAG, "Error creating user", e)
                handleCreateUserError(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateSignupRequest(request: SignupRequest) {
        // Username validation
        if (request.username.isBlank()) {
            throw IllegalArgumentException("Username cannot be empty")
        }
        if (request.username.length < 3) {
            throw IllegalArgumentException("Username must be at least 3 characters")
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            throw IllegalArgumentException("Invalid email format")
        }

        // Password validation
        if (request.password.length < 6) {
            throw IllegalArgumentException("Password must be at least 6 characters")
        }

        // Name validation
        if (request.fName.isBlank() || request.lName.isBlank()) {
            throw IllegalArgumentException("First and last name are required")
        }

        // Gender validation
        if (!VALID_GENDERS.contains(request.gender.uppercase())) {
            throw IllegalArgumentException("Gender must be one of: ${VALID_GENDERS.joinToString(", ")}")
        }

        // Date of birth validation
        try {
            val dob = java.time.LocalDate.parse(request.dob, DATE_FORMATTER)
            val now = java.time.LocalDate.now()
            if (dob.isAfter(now)) {
                throw IllegalArgumentException("Date of birth cannot be in the future")
            }
        } catch (e: java.time.format.DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Use YYYY-MM-DD")
        }

        // Address validation
        if (request.address.isBlank()) {
            throw IllegalArgumentException("Address is required")
        }

        // PhoneNumber validation (if provided)
        request.phoneNumber?.let { phone ->
            if (!phone.matches(Regex("^[+]?[0-9]{10,13}$"))) {
                throw IllegalArgumentException("Invalid phone number format")
            }
        }

        // Roles validation
        if (request.roles.isEmpty()) {
            throw IllegalArgumentException("At least one role is required")
        }
    }

    private fun handleCreateUserError(e: Exception) {
        errorMessage = when (e) {
            is IllegalArgumentException -> e.message
            is IOException -> "Network error: Please check your connection"
            else -> when {
                e.message?.contains("409") == true -> "Username or email already exists"
                e.message?.contains("400") == true -> "Invalid input data"
                else -> "Error creating account: ${e.message}"
            }
        }
        Log.e(TAG, "Error details: ${e.message}", e)
        createUserUiState = BaseUiState.Error
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
}