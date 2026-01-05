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
import kotlin.math.min

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
                    prompts = allLatestPrompts
                )
            }
        }
    }

    //onQueryChange
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }

        //filter suggestion
        if (newQuery.length >= 2) { //minimal 2 huruf
            val cleanQuery = newQuery.trim().lowercase()
            val filteredSuggestions = allLatestPrompts.filter { prompt ->
                val title = prompt.title.lowercase()

                //isMatch or isFuzzy
                val isMatch = title.contains(cleanQuery)
                val isFuzzy = if (!isMatch) hasTypoMatch(title, cleanQuery) else false

                isMatch || isFuzzy
            }
                .map { it.title } //get tittle
                .take(5) //take 5 suggestion

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

    //algoritma fuzzy
    private fun hasTypoMatch(text: String, query: String): Boolean {
        val words = text.split(" ")
        for (word in words) {
            //toleransi typo
            if (calculateLevenshteinDistance(word, query) <= 2) return true
        }
        return false
    }

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

    fun setMainCategory(category: String) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedMainCategory == category) "" else category
            val filteredList = applyLocalFilters(
                newSelection,
                currentState.selectedCategories
            )

            currentState.copy(
                selectedMainCategory = newSelection,
                prompts = filteredList
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
            val filteredList = applyLocalFilters(
                currentState.selectedMainCategory,
                currentList
            )

            currentState.copy(
                selectedCategories = currentList,
                prompts = filteredList
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