package com.example.katoapp.viewModel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.PromptOrgUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject


@HiltViewModel
class PromptOrgViewModel @Inject constructor(
    private val repository: PromptRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptOrgUiState())
    val uiState: StateFlow<PromptOrgUiState> = _uiState.asStateFlow()

    val promptId: String = savedStateHandle["promptId"] ?: ""

    init {
        fetchData()
        loadData()
    }

    //function get category
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
                withTimeout(15000L){
                    repository.savePrompt(
                        title, content, mainCategory, subCategories,
                        aiModel, modelVersion, imageUri, isSharing
                    )
                }
                //if success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Terjadi kesalahan saat menyimpan"
                    )
                }
            }catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Terjadi kesalahan saat menyimpan"
                    )
                }
            }
        }
    }

    //load data prompt
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val prompt = repository.getPromptById(promptId)//get data
            //get data category
            val mainCats = repository.getMainCategories()
            val generalCats = repository.getGeneralCategories()

            if (prompt != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        promptData = prompt,
                        categories = mainCats,
                        generalCategories = generalCats
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageLoad = "Data tidak ditemukan"
                    )
                }
            }
        }
    }

    //function update prompt
    fun updatePrompt(
        title: String, content: String, mainCategory: String,
        subCategories: List<String>, aiModel: String, modelVersion: String,
        isSharing: Boolean
    ) {
        val currentData = _uiState.value.promptData ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updatePrompt(
                    promptId = currentData.id,
                    title = title,
                    content = content,
                    mainCategory = mainCategory,
                    subCategories = subCategories,
                    aiModel = aiModel,
                    modelVersion = modelVersion,
                    imageUri = _uiState.value.selectedImageUri, // gambar baru
                    currentImageUrl = currentData.imageUrl,     // gambar lama
                    isSharing = isSharing
                )
                _uiState.update { it.copy(
                    isLoading = false ,
                    isSuccess = true
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false ,
                    errorMessage = e.message
                ) }
            }
        }
    }

    //fun delete prompt
    fun deletePrompt(onSuccess: () -> Unit) {
        val currentPrompt = _uiState.value.promptData ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deletePrompt(currentPrompt.id, currentPrompt.imageUrl)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal menghapus: ${e.message}")
                }
            }
        }
    }

    //reset state
    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }





}



