package com.example.splitexpenses.ui

import com.example.splitexpenses.data.Settlement

data class SettlementUiState(
    val totalToReceive: Double = 0.0,
    val settlements: List<Settlement> = emptyList(),
    val filter: String = "all"
)
