package com.example.splitexpenses.data

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val initials: String = ""
)

data class Group(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val members: List<User> = emptyList(),
    val totalBalance: Double = 0.0,
    val categoryIcon: String = "beach_access",
    val userBalances: Map<String, Double> = emptyMap()
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val payerId: String = "",
    val payerName: String = "",
    val participantIds: List<String> = emptyList(),
    val settledParticipants: List<String> = emptyList(),
    val userCost: Double = 0.0
)

data class Settlement(
    val id: String = UUID.randomUUID().toString(),
    val userName: String = "",
    val initials: String = "",
    val amount: Double = 0.0,
    val isPaid: Boolean = false,
    val statusText: String = ""
)
