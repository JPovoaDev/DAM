package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import com.example.splitexpenses.data.Settlement
import com.example.splitexpenses.ui.SettlementUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettlementViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettlementUiState())
    val uiState: StateFlow<SettlementUiState> = _uiState.asStateFlow()

    fun addSettlement(settlement: Settlement) {
        _uiState.update { state ->
            state.copy(settlements = state.settlements + settlement)
        }
    }

    fun markAsPaid(id: String) {
        _uiState.update { state ->
            val newList = state.settlements.map {
                if (it.id == id) it.copy(isPaid = true, statusText = "Liquidado") else it
            }
            state.copy(settlements = newList)
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(filter = filter) }
    }
}
