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
        loadTopFivePrompts()
    }

    //get category
    private fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            //get main category
            val mainCats = promptRepository.getMainCategories()
            //get general category
            val generalCats = promptRepository.getGeneralCategories()
            val defaultMainCat = if (mainCats.isNotEmpty()) mainCats[0] else ""

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = mainCats,
                    generalCategories = generalCats,
                    selectedMainCategory = defaultMainCat
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
            currentState.copy(
                selectedCategories = currentList
            )
        }
    }

    //get top 5 prompt
    private fun loadTopFivePrompts() {
        viewModelScope.launch {
            val popularResult = promptRepository.getPopularPrompts()
            val topRatedResult = promptRepository.getTopRatedPrompts()

            _uiState.update {
                it.copy(
                    popularPrompts = popularResult,
                    topRatedPrompts = topRatedResult
                )
            }
        }
    }

    //control MainCategory
    fun setMainCategory(category: String) {
        _uiState.update { currentState ->
            //klik lagi utk batalkan
//            val newSelection = if (currentState.selectedMainCategory == category) "" else category
            currentState.copy(
                selectedMainCategory = category
            )
        }
    }
}