package com.example.splitexpenses.ui

import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.User

data class GroupsUiState(
    val currentUser: User = User(),
    val groups: List<Group> = emptyList(),
    val newGroupName: String = "",
    val newGroupDescription: String = "",
    val totalExpenses: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
