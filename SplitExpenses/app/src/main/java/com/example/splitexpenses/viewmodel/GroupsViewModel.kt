package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.User
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.data.repository.AuthRepository
import com.example.splitexpenses.data.repository.GroupRepository
import com.example.splitexpenses.ui.GroupsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GroupsViewModel(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = combine(_uiState, groupRepository.observeGroups()) { state, groups ->
        state.copy(groups = groups)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroupsUiState())

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
            _uiState.update { it.copy(currentUser = user) }
        }
    }

    fun refreshCurrentUser() {
        loadCurrentUser()
    }

    fun reloadGroups() {
        loadCurrentUser()
        // O observeGroups() já reage ao utilizador logado, mas aqui pode forçar atualização da UI local se necessário
    }

    fun onGroupNameChanged(name: String) {
        _uiState.update { it.copy(newGroupName = name) }
    }

    fun onGroupDescriptionChanged(description: String) {
        _uiState.update { it.copy(newGroupDescription = description) }
    }

    fun createGroup() {
        if (_uiState.value.newGroupName.isBlank()) return
        
        val newGroup = Group(
            id = UUID.randomUUID().toString(),
            name = _uiState.value.newGroupName,
            description = _uiState.value.newGroupDescription,
            members = listOf(_uiState.value.currentUser),
            totalBalance = 0.0
        )
        
        viewModelScope.launch {
            val result = groupRepository.createGroup(newGroup)
            if (result.isSuccess) {
                _uiState.update { state ->
                    state.copy(
                        newGroupName = "",
                        newGroupDescription = ""
                    )
                }
            }
        }
    }
}
