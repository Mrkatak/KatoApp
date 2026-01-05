package com.example.katoapp.viewModel.state

import com.example.katoapp.data.model.Prompt

data class SharingPromptUiState (
    val isLoading: Boolean = false ,
    val username: String = "User" ,
    val errorMessage: String? = null ,

    //search state
    val searchQuery: String = "" ,

    val suggestions: List<String> = emptyList(),

    //list data
    val categories: List<String> = emptyList() ,
    val generalCategories: List<String> = emptyList() ,

    //selection state
    val selectedCategories: List<String> = emptyList() ,
    val selectedMainCategory: String = "" ,

    //data display
    val prompts: List<Prompt> = emptyList() ,
    val topRatedPrompts: List<Prompt> = emptyList() ,
    val searchResults: List<Prompt> = emptyList()
)