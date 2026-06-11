package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import com.example.splitexpenses.data.Expense
import com.example.splitexpenses.data.User
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.data.repository.GroupRepository
import com.example.splitexpenses.ui.GroupDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.repository.AuthRepository

class GroupDetailViewModel(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private var currentGroupId: String? = null

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    fun loadGroup(groupId: String) {
        if (currentGroupId == groupId) return
        currentGroupId = groupId

        // Initialize current user id in state
        _uiState.update { it.copy(currentUserId = authRepository.currentUser?.uid) }

        viewModelScope.launch {
            groupRepository.observeGroup(groupId).collect { group ->
                group?.let {
                    _uiState.update { state ->
                        state.copy(
                            group = it,
                            members = it.members
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            groupRepository.observeExpenses(groupId).collect { expensesList ->
                _uiState.update { state ->
                    val newBalance = expensesList.sumOf { it.amount }
                    state.copy(expenses = expensesList, totalBalance = newBalance)
                }
                calculateBalancesAndSettlements()
            }
        }
    }

    private fun calculateBalancesAndSettlements() {
        val state = _uiState.value
        val expenses = state.expenses
        val members = state.members

        // 1. Calculate Net Balances
        // Map of userId -> net balance (positive = gets money back, negative = owes money)
        val balances = mutableMapOf<String, Double>()
        members.forEach { balances[it.id] = 0.0 }

        for (expense in expenses) {
            val costPerPerson = if (expense.participantIds.isNotEmpty()) {
                expense.amount / expense.participantIds.size
            } else {
                0.0
            }

            for (participantId in expense.participantIds) {
                // Só processa quem ainda não pagou a sua parte
                if (!expense.settledParticipants.contains(participantId)) {
                    // Retira dinheiro a quem deve
                    val currentParticipantBalance = balances[participantId] ?: 0.0
                    balances[participantId] = currentParticipantBalance - costPerPerson
                    
                    // Dá esse mesmo dinheiro ao pagador
                    val currentPayerBalance = balances[expense.payerId] ?: 0.0
                    balances[expense.payerId] = currentPayerBalance + costPerPerson
                }
            }
        }

        // Round to avoid floating point issues
        val roundedBalances = balances.mapValues { Math.round(it.value * 100.0) / 100.0 }.mapKeys { it.key } // Ensure Map<String, Double>

        // Guardar saldos no Firestore para serem vistos na listagem de Grupos
        currentGroupId?.let { groupId ->
            viewModelScope.launch {
                groupRepository.updateGroupBalances(groupId, roundedBalances)
            }
        }

        // 2. Calculate Settlements (Greedy Algorithm)
        val debtors = roundedBalances.filter { it.value < 0 }.mapValues { -it.value }.toMutableMap()
        val creditors = roundedBalances.filter { it.value > 0 }.toMutableMap()

        val settlements = mutableListOf<com.example.splitexpenses.ui.Debt>()

        for ((debtorId, debtAmount) in debtors) {
            var remainingDebt = debtAmount
            val creditorIterator = creditors.iterator()

            while (remainingDebt > 0.01 && creditorIterator.hasNext()) {
                val creditor = creditorIterator.next()
                val creditorId = creditor.key
                val creditAmount = creditor.value

                if (creditAmount > 0.01) {
                    val settledAmount = minOf(remainingDebt, creditAmount)
                    
                    val fromUser = members.find { it.id == debtorId }
                    val toUser = members.find { it.id == creditorId }
                    
                    if (fromUser != null && toUser != null) {
                        settlements.add(
                            com.example.splitexpenses.ui.Debt(
                                fromUserId = fromUser.id,
                                fromUserName = fromUser.name,
                                toUserId = toUser.id,
                                toUserName = toUser.name,
                                amount = Math.round(settledAmount * 100.0) / 100.0
                            )
                        )
                    }

                    remainingDebt -= settledAmount
                    creditors[creditorId] = creditAmount - settledAmount
                }
            }
        }

        _uiState.update { it.copy(memberBalances = roundedBalances, settlements = settlements) }
    }

    fun payMyPartOfExpense(expenseId: String, currentUserId: String) {
        currentGroupId?.let { groupId ->
            viewModelScope.launch {
                groupRepository.payMyPartOfExpense(groupId, expenseId, currentUserId)
            }
        }
    }

    fun onMemberEmailChanged(email: String) {
        // Limpar feedback ao escrever
        _uiState.update { it.copy(newMemberEmail = email, addMemberError = null, addMemberSuccess = null) }
    }

    /**
     * Procura o utilizador pelo email na coleção "users" do Firestore.
     * Só adiciona se o utilizador existir — impede adicionar pessoas sem conta na app.
     * Usa o UID real do Firebase para que o filtro de grupos funcione corretamente.
     */
    fun addMember() {
        val email = _uiState.value.newMemberEmail.trim()
        if (email.isBlank()) return

        // Verificar se já está no grupo
        val alreadyMember = _uiState.value.members.any {
            it.email.equals(email, ignoreCase = true)
        }
        if (alreadyMember) {
            _uiState.update { it.copy(addMemberError = "Este utilizador já está no grupo.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, addMemberError = null, addMemberSuccess = null) }

        // Pesquisar pelo email no Firestore
        viewModelScope.launch {
            val result = groupRepository.findUserByEmail(email)
            if (result.isSuccess) {
                val foundUser = result.getOrNull()
                if (foundUser == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            addMemberError = "Utilizador não encontrado. Verifica o email."
                        )
                    }
                    return@launch
                }

                val updatedMembers = _uiState.value.members + foundUser
                currentGroupId?.let { id ->
                    val updateResult = groupRepository.addMemberToGroup(id, updatedMembers)
                    if (updateResult.isSuccess) {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                newMemberEmail = "",
                                addMemberSuccess = "${foundUser.name} adicionado com sucesso!"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, addMemberError = "Erro ao atualizar grupo: ${updateResult.exceptionOrNull()?.message}")
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, addMemberError = "Erro de rede: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    fun clearAddMemberFeedback() {
        _uiState.update { it.copy(addMemberError = null, addMemberSuccess = null) }
    }

    fun addExpense(expense: Expense) {
        currentGroupId?.let { id ->
            viewModelScope.launch {
                groupRepository.addExpense(id, expense)
            }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
