// FILE: ui/GroupsListScreen.kt
package com.example.splitexpenses.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.User
import com.example.splitexpenses.viewmodel.GroupsViewModel

@Composable
fun GroupsListScreen(
    viewModel: GroupsViewModel,
    onNavigateToGroup: (String) -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSettlements: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Olá,", style = MaterialTheme.typography.labelSmall)
                        Text(uiState.currentUser.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGroup,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Group")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    label = { Text("Grupos") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToExplore,
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                    label = { Text("Explorar") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Text(
                text = "Meus Grupos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            if (uiState.groups.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ainda não tens grupos", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.groups) { group ->
                        GroupItem(
                            group = group,
                            currentUserId = uiState.currentUser.id,
                            onClick = { onNavigateToGroup(group.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupItem(group: Group, currentUserId: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, fontWeight = FontWeight.Bold)
                Text("${group.members.size} membros", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
            
            val myBalance = group.userBalances[currentUserId] ?: 0.0
            val color = when {
                myBalance > 0 -> Color(0xFF4CAF50)
                myBalance < 0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }
            val prefix = if (myBalance > 0) "+" else ""
            
            Text(
                "$prefix${String.format("%.2f", myBalance)} €",
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
