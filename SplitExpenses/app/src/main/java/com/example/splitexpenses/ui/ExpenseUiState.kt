package com.example.splitexpenses.ui

import com.example.splitexpenses.data.User

data class ExpenseUiState(
    val description: String = "",
    val amount: String = "",
    val payer: User? = null,
    val participants: List<ParticipantSelection> = emptyList(),
    val isDivideEqually: Boolean = true,
    val isValid: Boolean = false
)

data class ParticipantSelection(
    val user: User,
    val isSelected: Boolean = false
)
