package com.example.splitexpenses.ui

import com.example.splitexpenses.data.User

data class ProfileUiState(
    val user: User = User(),
    val editedName: String = "",
    val editedEmail: String = "",
    val isLoading: Boolean = false
)
