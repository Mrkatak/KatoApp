package com.example.katoapp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.savedState
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.viewModel.state.PromptDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptDetailViewModel @Inject constructor(
    private val repository: PromptRepository ,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptDetailUiState())
    val uiState: StateFlow<PromptDetailUiState> = _uiState.asStateFlow()

    init {
        val promptId: String? = savedStateHandle["promptId"]
        if (promptId != null) {
            loadPromptDetail(promptId)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "ID Prompt tidak ditemukan"
                )
            }
        }
    }

    //function load prompt detail
    private fun loadPromptDetail(id: String) {
        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getPromptById(id)
            val savedStatus = repository.isPromptSaved(id)

            if (result != null) {
                val currentUid = repository.getCurrentUserUid() //cek current user
                val isMine = currentUid != null && currentUid == result.userId // bandingkan dengan ID pembuat prompt

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        prompt = result,
                        isOwner = isMine,
                        isSaved =  savedStatus
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Data tidak ditemukan"
                    )
                }
            }
        }
    }

    //function toggle bookmark
    fun toggleBookmark() {
        viewModelScope.launch {
            val currentPrompt = _uiState.value.prompt
            if (currentPrompt != null) {
                val oldStatus = _uiState.value.isSaved
                _uiState.update { it.copy(isSaved = !oldStatus) }
                val message = repository.toggleBookmark(currentPrompt)
                _uiState.update { it.copy(bookmarkMessage = message) } //update message
                delay(100)
                _uiState.update { it.copy(bookmarkMessage = null) } // Reset message agar tidak muncul terus
            }
        }
    }

    //function update rating
    fun updateRating(userRating: Int) {
        val currentPrompt = _uiState.value.prompt ?: return

        viewModelScope.launch {
            try {
                repository.updatePromptRating(currentPrompt, userRating)
                //ambil data prompt terbaru
                val updatedPrompt = repository.getPromptById(currentPrompt.id)

                if (updatedPrompt != null) {
                    _uiState.update {
                        it.copy(prompt = updatedPrompt)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //function increment usage
    fun incrementUsage() {
        val currentPrompt = _uiState.value.prompt ?: return
        viewModelScope.launch {
            try {
                repository.incrementUsageCount(currentPrompt)
                _uiState.update {
                    it.copy(prompt = currentPrompt.copy(usageCount = currentPrompt.usageCount + 1))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    //function report prompt
    fun reportPrompt(reason: String) {
        val currentPrompt = _uiState.value.prompt ?: return

        viewModelScope.launch {
            // Tampilkan loading jika perlu, atau biarkan background
            val success = repository.reportPrompt(currentPrompt, reason)

            if (success) {
                _uiState.update { it.copy(bookmarkMessage = "Laporan berhasil dikirim. Terima kasih.") }
            } else {
                _uiState.update { it.copy(bookmarkMessage = "Gagal mengirim laporan.") }
            }

            // Reset pesan toast
            kotlinx.coroutines.delay(100)
            _uiState.update { it.copy(bookmarkMessage = null) }
        }
    }
}