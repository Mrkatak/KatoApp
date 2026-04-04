package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.AdminTab
import com.example.katoapp.viewModel.state.AdminUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        fetchMainCats()
        changeTab(AdminTab.REPORT)
    }

    private fun fetchMainCats() {
        viewModelScope.launch {
            val mainCats = repository.getMainCategories()
            _uiState.update { it.copy(categories = mainCats) }
        }
    }

    fun changeTab(tab: AdminTab) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    currentTab = tab,
                    selectedMainCategory = ""
                )
            }

            val result = when (tab) {
                AdminTab.REPORT -> repository.getReportedPrompts()
                AdminTab.REVIEW -> repository.getAllSharingPrompts()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    rawPrompts = result,
                    prompts = result
                )
            }
        }
    }

    //filter kategori
    fun selectMainCategory(category: String) {
        _uiState.update { currentState ->
            val newCategory = if (currentState.selectedMainCategory == category) "" else category
            val filteredList = if (newCategory.isEmpty()) {
                currentState.rawPrompts
            } else {
                currentState.rawPrompts.filter { it.category == newCategory }
            }
            currentState.copy(
                selectedMainCategory = newCategory,
                prompts = filteredList
            )
        }
    }


}