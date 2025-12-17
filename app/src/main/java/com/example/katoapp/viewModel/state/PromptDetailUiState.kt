package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class PromptDetailUiState(
    val isLoading: Boolean = false ,
    val prompt: Prompt? = null ,
    val error: String? = null
)