package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.AuthRepository
import com.example.katoapp.viewModel.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Fungsi cek apakah user sudah login
    fun isUserLoggedIn(): Boolean {
        return repository.currentUser != null
    }

    //function login
    fun login(email: String , pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mohon isi semua kolom") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true , errorMessage = null) }
            try {
                repository.login(email , pass)
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        loginSuccess = true ,
                        successMessage = "Login Berhasil"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false , errorMessage = e.message) }
            }
        }
    }

    //function register
    fun register(username: String , email: String , pass: String) {
        if (username.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mohon isi semua kolom") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true , errorMessage = null) }
            try {
                repository.register(username , email , pass)
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        registerSuccess = true ,
                        successMessage = "Registrasi Berhasil"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false , errorMessage = e.message) }
            }
        }
    }

    //function reset password
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mohon isi email Anda") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true , errorMessage = null) }
            try {
                repository.resetPassword(email)
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        resetPassSuccess = true ,
                        successMessage = "Link reset password telah dikirim ke email Anda"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    //function reset state
    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    //function clear error
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    //function logout
    fun logout() {
        repository.logout()
        _uiState.update { AuthUiState() }
    }
}