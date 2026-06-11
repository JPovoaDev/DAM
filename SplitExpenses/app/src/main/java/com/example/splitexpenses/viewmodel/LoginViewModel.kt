package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.data.repository.AuthRepository
import com.example.splitexpenses.ui.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, loginError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }
    }

    fun login() {
        val state = _uiState.value
        var hasError = false

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email é obrigatório") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Palavra-passe é obrigatória") }
            hasError = true
        }

        if (!hasError) {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            viewModelScope.launch {
                val result = authRepository.signInWithEmailAndPassword(state.email, state.password)
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            loginError = result.exceptionOrNull()?.message ?: "Erro no login"
                        ) 
                    }
                }
            }
        }
    }
    
    fun onGoogleLoginSuccess() {
        viewModelScope.launch {
            authRepository.syncUserToFirestore()
            _uiState.update { it.copy(isSuccess = true) }
        }
    }
    
    fun onGoogleLoginError(error: String) {
        _uiState.update { it.copy(loginError = error) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
