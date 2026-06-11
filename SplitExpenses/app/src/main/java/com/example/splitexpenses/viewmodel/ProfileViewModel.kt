package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import com.example.splitexpenses.data.User
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.data.repository.AuthRepository
import com.example.splitexpenses.ui.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val firebaseUser = authRepository.currentUser
        if (firebaseUser != null) {
            val name = firebaseUser.displayName ?: "Utilizador"
            val email = firebaseUser.email ?: ""
            val initials = name.split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .map { it.first().uppercaseChar() }
                .joinToString("")
                .ifEmpty { "U" }

            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                initials = initials
            )
            _uiState.update { it.copy(user = user) }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(editedName = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(editedEmail = email) }
    }

    fun saveChanges() {
        val state = _uiState.value
        val firebaseUser = authRepository.currentUser ?: return

        _uiState.update { it.copy(isLoading = true) }

        val newName = if (state.editedName.isNotBlank()) state.editedName else state.user.name
        val newEmail = if (state.editedEmail.isNotBlank()) state.editedEmail else state.user.email

        // Update display name in Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
            if (profileTask.isSuccessful) {
                // Update email if changed
                if (state.editedEmail.isNotBlank() && state.editedEmail != state.user.email) {
                    firebaseUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener { emailTask ->
                        val initials = newName.split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .map { it.first().uppercaseChar() }
                            .joinToString("")
                            .ifEmpty { "U" }

                        val updatedUser = state.user.copy(
                            name = newName,
                            email = if (emailTask.isSuccessful) newEmail else state.user.email,
                            initials = initials
                        )
                        // Sincronizar alterações para o Firestore
                        viewModelScope.launch {
                            authRepository.syncUserToFirestore()
                        }
                        _uiState.update { it.copy(user = updatedUser, editedName = "", editedEmail = "", isLoading = false) }
                    }
                } else {
                    val initials = newName.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                        .ifEmpty { "U" }

                    val updatedUser = state.user.copy(
                        name = newName,
                        initials = initials
                    )
                    // Sincronizar alterações para o Firestore
                    viewModelScope.launch {
                        authRepository.syncUserToFirestore()
                    }
                    _uiState.update { it.copy(user = updatedUser, editedName = "", editedEmail = "", isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
