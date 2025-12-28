package com.example.katoapp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPromptViewModel @Inject constructor(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        fetchGeneralCategories()
    }

    //get general categories
    private fun fetchGeneralCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val categories = repository.getGeneralCategories()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    generalCategories = categories
                )
            }
        }
    }

    // Update Text SearchBar
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun searchByCategory(param: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, searchResults = emptyList()) }

            val results = when {
                param.startsWith("SEARCH_NATURAL:") -> {
                    val rawQuery = param.removePrefix("SEARCH_NATURAL:")
                    _uiState.update { it.copy(searchQuery = rawQuery) }
                    repository.searchPrompts(rawQuery)
                }
                param == "Popular" -> repository.getAllPopularPrompts()
                param == "Rating" -> repository.getAllTopRatedPrompts()
                else -> repository.getPromptsByCategory(param)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    searchResults = results
                )
            }
        }
    }





}