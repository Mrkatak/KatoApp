package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class DashboardUiState (
    val isLoading: Boolean = false ,
    val username: String = "User" ,
    val email: String = "" ,
    val errorMessage: String? = null,
    val categories: List<String> = emptyList() ,
    val popularPrompts: List<Prompt> = emptyList() ,
    val topRatedPrompts: List<Prompt> = emptyList(),
    val sharedCount: Int = 0,
    val savedCount: Int = 0
)