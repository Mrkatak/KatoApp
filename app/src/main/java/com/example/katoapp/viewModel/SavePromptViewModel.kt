package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.SavePromptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavePromptViewModel @Inject constructor(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavePromptUiState())
    val uiState: StateFlow<SavePromptUiState> = _uiState.asStateFlow()

    init {
        fetchPrivatePrompts()
    }

    init {
        loadDataBasedOnFilter("Private")
    }

    //function filter data
    fun onFilterChanged(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        loadDataBasedOnFilter(filter)
    }

    //function get private prompt
    private fun fetchPrivatePrompts(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            val result = repository.getPrivatePrompts()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    prompts = result
                )
            }
        }
    }

    //function refresh data
    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(1500)
            loadDataBasedOnFilter(_uiState.value.selectedFilter, isRefresh = true)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadDataBasedOnFilter(filter: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true) }
            }

            // Panggil fungsi Repository yang sesuai
            val result = when (filter) {
                "Private" -> repository.getPrivatePrompts() // Ambil dari koleksi pribadi user
                "Sharing" -> repository.getSharingPrompts() // Ambil dari koleksi publik (SharingPrompt)
                "Simpan" -> emptyList() // Belum ada fitur bookmark
                else -> emptyList()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    prompts = result
                )
            }
        }
    }

    private fun clearData() {
        _uiState.update { it.copy(prompts = emptyList(), isLoading = false) }
    }
}