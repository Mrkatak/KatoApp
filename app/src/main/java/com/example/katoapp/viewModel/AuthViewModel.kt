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

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    // Fungsi ini dipanggil oleh MainActivity saat aplikasi dibuka
    fun checkUserSession() {
        viewModelScope.launch {
            val currentUser = repository.currentUser
            if (currentUser != null) {
                // 1. User sudah login di Firebase Auth
                // 2. Sekarang cek ke Firestore: Apakah dia admin?
                val email = currentUser.email ?: ""
                val isAdmin = repository.checkIfUserIsAdmin(email)

                if (isAdmin) {
                    _startDestination.value = "DashboardAdminScreen"
                } else {
                    _startDestination.value = "MainUserScreen"
                }
            } else {
                // Belum login sama sekali
                _startDestination.value = "LoginScreen"
            }
        }
    }


    // Fungsi cek apakah user sudah login
    fun isUserLoggedIn(): Boolean {
        return repository.currentUser != null
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mohon isi semua kolom") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 1. Login ke Firebase Auth
                repository.login(email, pass)

                // 2. Cek apakah UID tersebut adalah Admin
                val isAdmin = repository.checkIfUserIsAdmin(email)

                // 3. Update State
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        isAdmin = isAdmin, // Set status admin di sini
                        successMessage = if (isAdmin) "Login sebagai Admin" else "Login Berhasil"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    //intent google signIn
    fun getGoogleLoginIntent() = repository.getGoogleSignInIntent()

    //fun login google
    fun googleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true , errorMessage = null) }
            try {
                repository.loginWithGoogle(idToken)
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        loginSuccess = true ,
                        successMessage = "Login Google Berhasil"
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
        val usernameRegex = "^[a-zA-Z0-9]+$".toRegex()
        if (username.length > 50){
            _uiState.update {
                it.copy(errorMessage = "Nama maksimum 50 karakter")
            }
            return
        }
        if (!username.matches(usernameRegex)) {
            _uiState.update {
                it.copy(errorMessage = "Nama hanya boleh huruf, angka, dan spasi")
            }
            return
        }
        val emailRegex = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.com$".toRegex()
        if (email.length > 50) {
            _uiState.update { it.copy(errorMessage = "Email maksimum 50 karakter") }
            return
        }
        if (!email.matches(emailRegex)) {
            _uiState.update { it.copy(errorMessage = "Format email salah (harus ada @ dan .com, tanpa simbol lain)") }
            return
        }
        if (pass.length <= 6) {
            _uiState.update { it.copy(errorMessage = "Password harus lebih dari 6 karakter") }
            return
        }
        if (pass.length > 30) {
            _uiState.update { it.copy(errorMessage = "Password maksimal 30 karakter") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true ,
                    errorMessage = null
                )
            }
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
                _uiState.update {
                    it.copy(
                        isLoading = false ,
                        errorMessage = e.message
                    )
                }
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
            _uiState.update {
                it.copy(
                    isLoading = true ,
                    errorMessage = null
                )
            }
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