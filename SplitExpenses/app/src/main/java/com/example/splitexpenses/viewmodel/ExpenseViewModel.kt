package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import com.example.splitexpenses.data.Expense
import com.example.splitexpenses.data.User
import com.example.splitexpenses.ui.ExpenseUiState
import com.example.splitexpenses.ui.ParticipantSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

class ExpenseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun setGroupMembers(members: List<User>) {
        _uiState.update { state ->
            state.copy(
                participants = members.map { ParticipantSelection(it, true) },
                payer = members.firstOrNull()
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
        validate()
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
        validate()
    }

    fun toggleParticipant(index: Int) {
        _uiState.update { state ->
            val newList = state.participants.toMutableList()
            newList[index] = newList[index].copy(isSelected = !newList[index].isSelected)
            state.copy(participants = newList)
        }
        validate()
    }

    fun setPayer(user: User) {
        _uiState.update { it.copy(payer = user) }
        validate()
    }

    private fun validate() {
        val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0
        val hasSelection = _uiState.value.participants.any { it.isSelected }
        _uiState.update { 
            it.copy(isValid = it.description.isNotBlank() && amountValue > 0 && hasSelection)
        }
    }

    fun getCreatedExpense(): Expense? {
        if (!_uiState.value.isValid) return null
        
        val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0
        val participants = _uiState.value.participants.filter { it.isSelected }.map { it.user }
        
        return Expense(
            description = _uiState.value.description,
            amount = amountValue,
            date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()),
            payerId = _uiState.value.payer?.id ?: "",
            payerName = _uiState.value.payer?.name ?: "",
            participantIds = participants.map { it.id },
            userCost = amountValue / participants.size
        )
    }

    fun clearForm() {
        _uiState.update { ExpenseUiState() }
    }
}
