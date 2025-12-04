package com.example.katoapp.viewModel.state

data class AuthUiState(
    val successMessage: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val resetPassSuccess: Boolean = false
)
