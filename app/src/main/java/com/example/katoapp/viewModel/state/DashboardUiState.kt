package com.example.katoapp.viewModel.state

data class DashboardUiState (
    val isLoading: Boolean = false,
    val username: String = "User",
    val email: String = "",
    val errorMessage: String? = null
)