// FILE: ui/GroupDetailScreen.kt
package com.example.splitexpenses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.splitexpenses.data.Expense
import com.example.splitexpenses.data.User
import com.example.splitexpenses.viewmodel.GroupDetailViewModel

@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupDetailViewModel,
    onBack: () -> Unit,
    onNavigateToNewExpense: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var selectedMemberForDetails by remember { mutableStateOf<User?>(null) }
    var selectedExpenseForDetails by remember { mutableStateOf<Expense?>(null) }

    LaunchedEffect(groupId) {
        viewModel.loadGroup(groupId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(uiState.group?.name ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Member")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewExpense) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            BalanceTopCard(balance = uiState.totalBalance)
            
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("Despesas") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("Membros") }
                )
            }

            if (uiState.selectedTab == 0) {
                if (uiState.expenses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sem despesas", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.expenses) { expense ->
                            ExpenseCard(
                                expense = expense,
                                onClick = { selectedExpenseForDetails = expense }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.members) { member ->
                        MemberItem(
                            user = member,
                            netBalance = uiState.memberBalances[member.id] ?: 0.0,
                            onClick = { selectedMemberForDetails = member }
                        )
                    }
                }
            }
        }
    }

    if (showAddMemberDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddMemberDialog = false
                viewModel.clearAddMemberFeedback()
            },
            title = { Text("Adicionar Membro") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Introduz o email da pessoa que queres adicionar. Ela tem de ter conta na app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = uiState.newMemberEmail,
                        onValueChange = { viewModel.onMemberEmailChanged(it) },
                        label = { Text("Email") },
                        placeholder = { Text("ex: rafa@email.com") },
                        singleLine = true,
                        enabled = !uiState.isLoading
                    )
                    // Feedback de erro
                    uiState.addMemberError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Feedback de sucesso
                    uiState.addMemberSuccess?.let { success ->
                        Text(
                            text = success,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (uiState.addMemberSuccess != null) {
                            // Já adicionou com sucesso, fechar o dialog
                            showAddMemberDialog = false
                            viewModel.clearAddMemberFeedback()
                        } else {
                            viewModel.addMember()
                        }
                    },
                    enabled = !uiState.isLoading && uiState.newMemberEmail.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else if (uiState.addMemberSuccess != null) {
                        Text("Fechar")
                    } else {
                        Text("Adicionar")
                    }
                }
            },
            dismissButton = {
                if (uiState.addMemberSuccess == null) {
                    TextButton(onClick = {
                        showAddMemberDialog = false
                        viewModel.clearAddMemberFeedback()
                    }) { Text("Cancelar") }
                }
            }
        )
    }

    selectedMemberForDetails?.let { member ->
        AlertDialog(
            onDismissRequest = { selectedMemberForDetails = null },
            title = { Text("Saldos de ${member.name}") },
            text = {
                val memberDebts = uiState.settlements.filter { it.fromUserId == member.id }
                val memberCredits = uiState.settlements.filter { it.toUserId == member.id }
                
                Column {
                    if (memberDebts.isEmpty() && memberCredits.isEmpty()) {
                        Text("Tudo saldado! Ninguém deve a ninguém.")
                    }
                    
                    if (memberDebts.isNotEmpty()) {
                        Text("A Dever:", fontWeight = FontWeight.Bold, color = Color(0xFFF44336), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        memberDebts.forEach { debt ->
                            Text("- Deve ${String.format("%.2f", debt.amount)} € a ${debt.toUserName}")
                        }
                    }
                    
                    if (memberCredits.isNotEmpty()) {
                        Text("A Receber:", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        memberCredits.forEach { credit ->
                            Text("- ${credit.fromUserName} deve-lhe ${String.format("%.2f", credit.amount)} €")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedMemberForDetails = null }) { Text("Fechar") }
            }
        )
    }

    selectedExpenseForDetails?.let { expense ->
        AlertDialog(
            onDismissRequest = { selectedExpenseForDetails = null },
            title = { Text("Detalhes da Despesa") },
            text = {
                Column {
                    Text(expense.description, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Valor Total: ${String.format("%.2f", expense.amount)} €", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Pagador:", fontWeight = FontWeight.Bold)
                    Text(expense.payerName)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dividido por:", fontWeight = FontWeight.Bold)
                    val participantNames = uiState.members
                        .filter { expense.participantIds.contains(it.id) }
                        .joinToString(", ") { 
                            val settled = expense.settledParticipants.contains(it.id)
                            if (settled) "${it.name} (Pago)" else it.name
                        }
                    
                    Text(if (participantNames.isNotEmpty()) participantNames else "Ninguém")
                    
                    val currentUserId = uiState.currentUserId
                    val unpaidParticipants = expense.participantIds.filter { it != expense.payerId && !expense.settledParticipants.contains(it) }
                    val unpaidOthers = uiState.members.filter { unpaidParticipants.contains(it.id) && it.id != currentUserId }
                    
                    if (unpaidOthers.isNotEmpty()) {
                        val names = unpaidOthers.joinToString(", ") { it.name }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Falta $names pagar",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    val isParticipant = currentUserId != null && expense.participantIds.contains(currentUserId)
                    val isNotPayer = currentUserId != expense.payerId
                    val hasNotPaid = currentUserId != null && !expense.settledParticipants.contains(currentUserId)
                    
                    if (isParticipant && isNotPayer && hasNotPaid) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.payMyPartOfExpense(expense.id, currentUserId!!)
                                selectedExpenseForDetails = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Pagar minha parte")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedExpenseForDetails = null }) { Text("Fechar") }
            }
        )
    }
}

@Composable
fun BalanceTopCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("SALDO TOTAL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("${String.format("%.2f", balance)} €", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Balanço do grupo", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, onClick: () -> Unit) {
    val unpaidParticipants = expense.participantIds.filter { it != expense.payerId && !expense.settledParticipants.contains(it) }
    val isPaid = unpaidParticipants.isEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isPaid) 0.6f else 1f)
            .background(Color.White, RoundedCornerShape(16.dp))
            .then(
                if (isPaid) Modifier else Modifier.clickable { onClick() }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = Color(0xFFF5F5F5)) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description,
                fontWeight = FontWeight.Bold,
                textDecoration = if (isPaid) TextDecoration.LineThrough else null
            )
            Text(expense.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${String.format("%.2f", expense.amount)} €", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("PAGO POR ${expense.payerName.uppercase()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun MemberItem(user: User, netBalance: Double, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Text(user.initials, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(user.name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        
        Column(horizontalAlignment = Alignment.End) {
            if (netBalance > 0) {
                Text("Recebe", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("+${String.format("%.2f", netBalance)} €", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            } else if (netBalance < 0) {
                Text("Deve", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("${String.format("%.2f", netBalance)} €", fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
            } else {
                Text("Saldado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("0.00 €", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
