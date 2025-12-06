package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.AuthRepository
import com.example.katoapp.viewModel.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardUserViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    //function load user data
    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            //get uid from repository
            val currentUser = repository.currentUser
            if (currentUser != null) {
                //get user data from db
                val userData = repository.getUserData(currentUser.uid)
                if (userData != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            username = userData.username,
                            email = userData.email
                        )
                    }
                } else {
                    //if data not found
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            username = currentUser.displayName ?: "Pengguna01",
                            email = currentUser.email ?: ""
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User tidak di temukan"
                    )
                }
            }
        }
    }
}