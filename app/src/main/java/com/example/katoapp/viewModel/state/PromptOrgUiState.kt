package com.example.katoapp.viewModel.state

import android.net.Uri
import com.example.katoapp.data.model.Prompt

data class PromptOrgUiState(
    val isLoading: Boolean = false ,
    val categories: List<String> = emptyList() ,
    val selectedImageUri: Uri? = null ,
    val generalCategories: List<String> = emptyList() ,
    val selectedGeneralCategories: List<String> = emptyList() ,
    val errorMessage: String? = null ,
    val isSuccess: Boolean = false ,
    val promptData: Prompt? = null ,
)
