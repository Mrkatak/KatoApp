package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val prompts: List<Prompt> = emptyList(),
    val categories: List<String> = emptyList()
)
