package com.example.hospitalapp.ui.viewModels

sealed class BaseUiState<out T> {
    object Loading : BaseUiState<Nothing>()
    object Error : BaseUiState<Nothing>()
    data class Success<T>(val data: T) : BaseUiState<T>()
}