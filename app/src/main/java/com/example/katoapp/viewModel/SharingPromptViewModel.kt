package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.SharingPromptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharingPromptViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharingPromptUiState())
    val uiState: StateFlow<SharingPromptUiState> = _uiState.asStateFlow()

    init {
        fetchData()
        fetchGeneralCategories()
        loadPopularPrompts()
        loadTopRatedPrompts()
    }

    //get category
    private fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            //get main category
            val mainCats = promptRepository.getMainCategories()
            //get general category
            val generalCats = promptRepository.getGeneralCategories()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = mainCats,
                    generalCategories = generalCats
                )
            }
        }
    }

    //get general categories
    private fun fetchGeneralCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val categories = promptRepository.getGeneralCategories()
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

    //toggle filter
    fun toggleCategory(category: String) {
        _uiState.update { currentState ->
            val currentList = currentState.selectedCategories.toMutableList()
            if (currentList.contains(category)) {
                currentList.remove(category)
            } else {
                currentList.add(category)
            }
            currentState.copy(selectedCategories = currentList)
        }
    }

    //fun get popular prompt (5)
    private fun loadPopularPrompts() {
        viewModelScope.launch {
            val result = promptRepository.getPopularPrompts()

            _uiState.update {
                it.copy(popularPrompts = result)
            }
        }
    }

    //function get top rated prompt (5)
    private fun loadTopRatedPrompts() {
        viewModelScope.launch {
            val result = promptRepository.getTopRatedPrompts()
            _uiState.update {
                it.copy(topRatedPrompts = result)
            }
        }
    }
}