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
import com.example.hospitalapp.data.UserData
import com.example.hospitalapp.data.UserManager
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.data.repositories.UserRepository
import com.example.hospitalapp.network.model.CreateUserRequest
import com.example.hospitalapp.network.model.LoginRequest
import com.example.hospitalapp.network.model.LoginResponse
import com.example.hospitalapp.network.model.SignupRequest
import com.example.hospitalapp.network.model.UserResponse
import com.example.hospitalapp.network.model.UserRole
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var userUiState: BaseUiState<UserResponse> by mutableStateOf(BaseUiState.Loading)
        private set

    var usersListUiState: BaseUiState<List<UserResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var createUserUiState: BaseUiState<Long?> by mutableStateOf(BaseUiState.Success(null))

    var loginState: BaseUiState<LoginResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    private val _usersList = MutableStateFlow<List<UserResponse>>(emptyList())
    val usersList: StateFlow<List<UserResponse>> = _usersList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun createInitialUser(role: UserRole, userName: String, password: String) {
        viewModelScope.launch {
            createUserUiState = BaseUiState.Loading
            try {
                val userId = userRepository.createInitialUser(role=role, username = userName, password=password)
                createUserUiState = BaseUiState.Success(userId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error creating user"
                createUserUiState = BaseUiState.Error
            }
        }
    }

    fun getUserById(id: Long) {
        viewModelScope.launch {
            userUiState = BaseUiState.Loading
            try {
                val user = userRepository.getUserById(id)
                _currentUser.value = user
                userUiState = BaseUiState.Success(user)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching user"
                userUiState = BaseUiState.Error
            }
        }
    }

    fun getUsersByRole(role: UserRole) {
        viewModelScope.launch {
            usersListUiState = BaseUiState.Loading
            try {
                val users = userRepository.getUsersByRole(role)
                _usersList.value = users
                usersListUiState = BaseUiState.Success(users)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching users"
                usersListUiState = BaseUiState.Error
            }
        }
    }

    fun getLatestUserByRole(role: UserRole) {
        viewModelScope.launch {
            userUiState = BaseUiState.Loading
            try {
                val user = userRepository.getLatestUserByRole(role)
                if (user != null) {
                    _currentUser.value = user
                    userUiState = BaseUiState.Success(user)
                } else {
                    _errorMessage.value = "No user found for role: ${role.name}"
                    userUiState = BaseUiState.Error
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching latest user"
                userUiState = BaseUiState.Error
            }
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(id)
                // Refresh user list if needed
                _currentUser.value?.let { current ->
                    getUsersByRole(UserRole.valueOf(current.role))
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error deleting user"
            }
        }
    }
    init {
        // Check if user is already logged in
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                // We'll use the UserManager to check if a user is logged in
                UserManager.currentUser?.let { userData ->
                    // User is logged in, fetch full details
                    getUserById(userData.id)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error checking login status"
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginState = BaseUiState.Loading
            try {
                val loginRequest = LoginRequest(username = username, password = password)
                val response = userRepository.login(loginRequest)

                // Update UserManager with the current user data
                val user = userRepository.getUserById(response.userId)
                UserManager.updateUser(
                    UserData(
                        id = user.id,
                        email = "",
                        fName = "",
                        lName = "",
                        phoneNumber = "",
                        dob ="",
                        role = user.role
                    )
                )

                // Update state
                _currentUser.value = user
                userUiState = BaseUiState.Success(user)
                loginState = BaseUiState.Success(response)
            } catch (e: IllegalStateException) {
                _errorMessage.value = "Invalid username or password"
                loginState = BaseUiState.Error
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
                loginState = BaseUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                UserViewModel(
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}