package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class SavePromptUiState(
    val isLoading: Boolean = false,
    val selectedFilter: String = "Private",
    val isRefreshing: Boolean = false,
    val prompts: List<Prompt> = emptyList()
)