package com.example.splitexpenses.ui

import com.example.splitexpenses.data.Expense
import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.User

data class GroupDetailUiState(
    val group: Group? = null,
    val currentUserId: String? = null,
    val expenses: List<Expense> = emptyList(),
    val members: List<User> = emptyList(),
    val newMemberEmail: String = "",
    val totalBalance: Double = 0.0,
    val memberBalances: Map<String, Double> = emptyMap(), // uid -> net balance
    val settlements: List<Debt> = emptyList(), // List of who owes who
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    // Feedback ao utilizador ao adicionar membro por email
    val addMemberError: String? = null,
    val addMemberSuccess: String? = null
)

// Represents a debt between two members
data class Debt(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Double
)
