// FILE: ui/NewExpenseScreen.kt
package com.example.splitexpenses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitexpenses.viewmodel.ExpenseViewModel

@Composable
fun NewExpenseScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nova Despesa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("O que foi?", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            TextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = { Text("Ex: Jantar de Sábado") },
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                color = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MONTANTE TOTAL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("€", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = uiState.amount,
                            onValueChange = { viewModel.onAmountChange(it) },
                            placeholder = { Text("0.00", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                            textStyle = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quem Pagou Dropdown
            var expandedPayer by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Quem pagou?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = expandedPayer,
                    onExpandedChange = { expandedPayer = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = uiState.payer?.name ?: "Selecionar...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayer) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedPayer,
                        onDismissRequest = { expandedPayer = false }
                    ) {
                        uiState.participants.map { it.user }.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name) },
                                onClick = {
                                    viewModel.setPayer(user)
                                    expandedPayer = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dividir com", fontWeight = FontWeight.Bold)
                Text("Selecionar Todos", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(uiState.participants) { index, participant ->
                    ParticipantChip(
                        name = participant.user.name,
                        initials = participant.user.initials,
                        isSelected = participant.isSelected,
                        onClick = { viewModel.toggleParticipant(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                enabled = uiState.isValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Guardar Despesa", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ParticipantChip(name: String, initials: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp)).clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEEEEEE)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(initials, color = if (isSelected) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
