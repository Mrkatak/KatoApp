package com.example.katoapp.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.PromptOrgUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PromptOrgViewModel @Inject constructor(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptOrgUiState())
    val uiState: StateFlow<PromptOrgUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            //get main category
            val mainCats = repository.getMainCategories()
            //get general category
            val generalCats = repository.getGeneralCategories()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = mainCats,
                    generalCategories = generalCats
                )
            }
        }
    }

    //function centang/hapus kategori umum
    fun toggleGeneralCategory(category: String) {
        _uiState.update { currentState ->
            val currentList = currentState.selectedGeneralCategories.toMutableList()

            if (currentList.contains(category)) {
                currentList.remove(category) //hapus jika sudah ada
            } else {
                currentList.add(category) // tambah jika belum ada
            }

            currentState.copy(selectedGeneralCategories = currentList)
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    //function save Prompt
    fun savePrompt(
        title: String,
        content: String,
        mainCategory: String,
        subCategories: List<String>,
        aiModel: String,
        modelVersion: String,
        imageUri: Uri?,
        isSharing: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.savePrompt(
                    title, content, mainCategory, subCategories,
                    aiModel, modelVersion, imageUri, isSharing
                )
                //if success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                //if error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Terjadi kesalahan saat menyimpan"
                    )
                }
            }
        }
    }

    //reset state
    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }





}