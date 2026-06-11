// FILE: ui/CreateGroupScreen.kt
package com.example.splitexpenses.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitexpenses.viewmodel.GroupsViewModel

@Composable
fun CreateGroupScreen(
    viewModel: GroupsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Grupo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            Text("Organiza as tuas despesas", style = MaterialTheme.typography.headlineMedium)
            Text("Dá um nome ao teu novo grupo.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = uiState.newGroupName,
                onValueChange = { viewModel.onGroupNameChanged(it) },
                label = { Text("Nome do Grupo") },
                placeholder = { Text("Ex: Viagem Surf Trip") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = uiState.newGroupDescription,
                onValueChange = { viewModel.onGroupDescriptionChanged(it) },
                label = { Text("Descrição (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    viewModel.createGroup()
                    onBack()
                },
                enabled = uiState.newGroupName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Criar Grupo")
            }
        }
    }
}
