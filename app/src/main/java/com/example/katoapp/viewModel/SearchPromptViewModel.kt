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

//    fun selectCategory(category: String) {
//        _uiState.update { currentState ->
//            // Jika diklik lagi, batalkan pilihan (Deselect)
//            val newSelection = if (currentState.selectedCategories.contains(category)) {
//                emptyList()
//            } else {
//                listOf(category) // Hanya satu item dalam list
//            }
//            currentState.copy(
//                selectedCategories = newSelection
//            )
//        }
//    }
//
//    //toggle filter
//    fun toggleCategory(category: String) {
//        _uiState.update { currentState ->
//            val currentList = currentState.selectedCategories.toMutableList()
//            if (currentList.contains(category)) {
//                currentList.remove(category)
//            } else {
//                currentList.add(category)
//            }
//            currentState.copy(
//                selectedCategories = currentList
//            )
//        }
//    }

    //fun search by main category
    fun searchByCategory(param: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    searchResults = emptyList()
                )
            }
            val results = when {
                //format search
                param.startsWith("SEARCH:") -> {
                    val rawContent = param.removePrefix("SEARCH:")
                    val parts = rawContent.split("|")
                    val queryText = parts.getOrNull(0) ?: ""
                    val mainCategory = parts.getOrNull(1) ?: "All"
                    val filtersString = parts.getOrNull(2) ?: ""
                    val filters = if (filtersString.isNotEmpty()) filtersString.split(",") else emptyList()

                    // update search query
                    _uiState.update {
                        it.copy(
                            searchQuery = queryText
                        )
                    }
                    //cari berdasarkan judul & subCategory
                    var searchRes = repository.searchPrompts(queryText, filters)

                    //filter tambahan utk mainCategory
                    if (mainCategory != "All" && mainCategory.isNotEmpty()) {
                        searchRes = searchRes.filter { it.category == mainCategory }
                    }
                    searchRes
                }
                //handle button lainnya
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