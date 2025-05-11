package com.example.hospitalapp.ui.viewModels

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences

) : ViewModel(){

    var userDetailsUiState: BaseUiState<UserResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    var createUserUiState: BaseUiState<UserResponse?> by mutableStateOf(BaseUiState.Success(null))
        private set

    private var _loginState = mutableStateOf<BaseUiState<Long?>>(BaseUiState.Loading)
    val loginState: State<BaseUiState<Long?>> = _loginState

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    init {
        // Check if user is already logged in
        viewModelScope.launch {
            val userId = userPreferences.getUserId()
            userId?.let {
                getUserById(it)
            }
        }
    }

    fun createUser(request: CreateUserRequest) {
        viewModelScope.launch {
            createUserUiState = BaseUiState.Loading
            try {
                val result = userRepository.createUser(request)
                _currentUser.value = result
                userPreferences.saveUser(result)
                createUserUiState = BaseUiState.Success(result)
                login(request.username, request.password)
            } catch (e: Exception) {
                createUserUiState = BaseUiState.Error
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = BaseUiState.Loading
            try {
                val response = userRepository.login(username, password)
                userPreferences.saveUserId(response.userId)
                _loginState.value = BaseUiState.Success(response.userId)
                // After successful login, get user details
                getUserById(response.userId)
            } catch (e: Exception) {
                _loginState.value = BaseUiState.Error
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
}