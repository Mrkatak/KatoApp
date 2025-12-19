package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class SharingPromptUiState (
    val isLoading: Boolean = false ,
    val username: String = "User" ,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val categories: List<String> = emptyList() ,
    val popularPrompts: List<Prompt> = emptyList() ,
    val topRatedPrompts: List<Prompt> = emptyList(),
    val generalCategories: List<String> = emptyList(),
    val selectedCategories: List<String> = emptyList(),
    val searchResults: List<Prompt> = emptyList()
)