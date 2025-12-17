package com.example.katoapp.viewModel.state

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val generalCategories: List<String> = emptyList(), // Data dari DB
    val selectedCategories: List<String> = emptyList(), // Filter aktif
    // Nanti tambah val searchResults: List<Prompt> = emptyList()
)
