package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.data.repository.AuthRepository
import com.example.splitexpenses.ui.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onDisplayNameChanged(value: String) {
        _uiState.update { it.copy(displayName = value, displayNameError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun register() {
        val state = _uiState.value
        var hasError = false

        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(displayNameError = "Nome é obrigatório") }
            hasError = true
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email é obrigatório") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Palavra-passe é obrigatória") }
            hasError = true
        }
        if (state.confirmPassword != state.password) {
            _uiState.update { it.copy(confirmPasswordError = "As palavras-passe não coincidem") }
            hasError = true
        }

        if (!hasError) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                val result = authRepository.createUserWithEmailAndPassword(state.email, state.password, state.displayName)
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            emailError = result.exceptionOrNull()?.message ?: "Erro no registo"
                        ) 
                    }
                }
            }
        }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
