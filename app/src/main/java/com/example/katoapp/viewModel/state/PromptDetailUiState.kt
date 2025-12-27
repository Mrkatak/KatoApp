package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class PromptDetailUiState(
    val isLoading: Boolean = true ,
    val prompt: Prompt? = null ,
    val error: String? = null,
    val isOwner: Boolean = false,
    val isSaved: Boolean = false,
    val bookmarkMessage: String? = null
)