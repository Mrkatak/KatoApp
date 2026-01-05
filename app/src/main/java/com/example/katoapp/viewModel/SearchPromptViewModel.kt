package com.example.katoapp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class SearchPromptViewModel @Inject constructor(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    //cache data
    private var allPromptsCache: List<Prompt> = emptyList()

    init {
        fetchGeneralCategories()
        fetchDataForAutocomplete()
    }

    //get general categories
    private fun fetchGeneralCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val categories = repository.getGeneralCategories()
            _uiState.update {
                it.copy(
                    generalCategories = categories
                )
            }
        }
    }

    //get latest prompt
    private fun fetchDataForAutocomplete() {
        viewModelScope.launch {
            allPromptsCache = repository.getLatestPrompts()
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        if (newQuery.length >= 2) {
            val cleanQuery = newQuery.trim().lowercase()
            val filteredSuggestions = allPromptsCache.filter { prompt ->
                val title = prompt.title.lowercase()
                val isMatch = title.contains(cleanQuery)
                val isFuzzy = if (!isMatch) hasTypoMatch(title, cleanQuery) else false

                isMatch || isFuzzy
            }
                .map { it.title }
                .distinct() // Hapus duplikat judul
                .take(5) //Take 5 suggestion

            _uiState.update { it.copy(suggestions = filteredSuggestions) }
        } else {
            _uiState.update { it.copy(suggestions = emptyList()) }
        }
    }

    //saat user klik suggestion
    fun onSuggestionClick(suggestion: String) {
        _uiState.update {
            it.copy(
                searchQuery = suggestion,
                suggestions = emptyList()
            )
        }
    }

    //function search
    fun searchByCategory(param: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    searchResults = emptyList()
                )
            }

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

    //algoritma fuzzy
    private fun hasTypoMatch(text: String, query: String): Boolean {
        val words = text.split(" ")
        for (word in words) {
            if (calculateLevenshteinDistance(word, query) <= 2) return true
        }
        return false
    }

    //levenshtein
    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) {
            for (j in 0..s2.length) {
                if (i == 0) dp[i][j] = j
                else if (j == 0) dp[i][j] = i
                else dp[i][j] = min(
                    dp[i - 1][j - 1] + costOfSubstitution(s1[i - 1], s2[j - 1]),
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun costOfSubstitution(a: Char, b: Char): Int = if (a == b) 0 else 1





}





