package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.model.Prompt
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

    //cache data mentah (nanti di ganti derek langsung dari repository)
    private var allLatestPrompts: List<Prompt> = emptyList()

    init {
        fetchData()
        loadInitialData()
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

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            //get category
            val mainCats = promptRepository.getMainCategories()
            val generalCats = promptRepository.getGeneralCategories()
            //get prompt
            allLatestPrompts = promptRepository.getLatestPrompts()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = mainCats,
                    generalCategories = generalCats,
                    selectedMainCategory = "",
                    popularPrompts = allLatestPrompts
                )
            }
        }
    }

    // Update Text SearchBar
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun setMainCategory(category: String) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedMainCategory == category) "" else category
            val filteredList = applyLocalFilters(newSelection, currentState.selectedCategories)

            currentState.copy(
                selectedMainCategory = newSelection,
                popularPrompts = filteredList
            )
        }
    }

    fun toggleCategory(category: String) {
        _uiState.update { currentState ->
            val currentList = currentState.selectedCategories.toMutableList()
            if (currentList.contains(category)) {
                currentList.remove(category)
            } else {
                currentList.add(category)
            }

            val filteredList = applyLocalFilters(currentState.selectedMainCategory, currentList)

            currentState.copy(
                selectedCategories = currentList,
                popularPrompts = filteredList
            )
        }
    }

    private fun applyLocalFilters(mainCat: String, generalCats: List<String>): List<Prompt> {
        var result = allLatestPrompts

        //filter Main
        if (mainCat.isNotEmpty()) {
            result = result.filter { it.category == mainCat }
        }
        //filter general
        if (generalCats.isNotEmpty()) {
            result = result.filter { prompt ->
                prompt.subCategories.any { it in generalCats }
            }
        }

        return result
    }


}