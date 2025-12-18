package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val generalCategories: List<String> = emptyList(),
    val selectedCategories: List<String> = emptyList(),
    val searchResults: List<Prompt> = emptyList()
)
