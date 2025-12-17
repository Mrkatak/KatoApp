package com.example.katoapp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.PromptDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptDetailViewModel @Inject constructor(
    private val repository: PromptRepository ,
    savedStateHandle: SavedStateHandle // Ini untuk menangkap parameter navigasi
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptDetailUiState())
    val uiState: StateFlow<PromptDetailUiState> = _uiState.asStateFlow()

    init {
        // Ambil "promptId" yang dikirim lewat navigasi
        // Pastikan nama key "promptId" sama dengan di NavHost
        val promptId: String? = savedStateHandle["promptId"]

        if (promptId != null) {
            loadPromptDetail(promptId)
        } else {
            _uiState.update { it.copy(error = "ID Prompt tidak ditemukan") }
        }
    }

    private fun loadPromptDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getPromptById(id)

            if (result != null) {
                _uiState.update { it.copy(isLoading = false, prompt = result) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Data tidak ditemukan") }
            }
        }
    }
}